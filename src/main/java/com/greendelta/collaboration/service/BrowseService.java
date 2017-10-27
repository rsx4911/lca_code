package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.lca.search.SearchFilterValue;
import com.greendelta.lca.search.SearchQueryBuilder;
import com.greendelta.lca.search.SearchSorting;

// This service filters previous versions of multiple data sets, so only the latest remains
public class BrowseService {

	private final SearchService searchService;
	private final HistoryService historyService;
	private final IndexEntryParser parser = new IndexEntryParser();

	@Inject
	public BrowseService(SearchService searchService, HistoryService historyService) {
		this.searchService = searchService;
		this.historyService = historyService;
	}

	public List<ObjectMap> getRootContent(BrowseParameter params) {
		List<ObjectMap> types = new ArrayList<>();
		for (ModelType type : ModelTypes.SORTED) {
			File dir = params.repo.getModelDir(type, false);
			if (!dir.exists())
				continue;
			List<ObjectMap> all = getAll(type, params, true);
			if (all.isEmpty())
				continue;
			ObjectMap map = ObjectMap.fromMap(new HashMap<>());
			map.put("type", type);
			map.put("count", all.size());
			ObjectMap last = all.get(0);
			if (!params.includeDeleted) {
				last = getAll(type, params.clone().includeDeleted(true), true).get(0);
			}
			// put commit info of last changed children
			map.put("commitId", last.get("commitId"));
			map.put("commitMessage", last.get("commitMessage"));
			map.put("commitTimestamp", last.get("commitTimestamp"));
			map.put("deleted", params.includeDeleted && getAll(type, params.clone().includeDeleted(false)).isEmpty());
			types.add(map);
		}
		return types;
	}

	public List<ObjectMap> getAll(Repository repo, ModelType type) {
		return convert(getAll(type, new BrowseParameter(repo)));
	}

	private List<ObjectMap> getAll(ModelType type, BrowseParameter params) {
		return getAll(type, params, false);
	}

	private List<ObjectMap> getAll(ModelType type, BrowseParameter params, boolean ignoreIfMoved) {
		SearchQueryBuilder builder = builder(params);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		return new DataFilter(result, params, ignoreIfMoved).apply();
	}

	public List<ObjectMap> getUncategorized(ModelType type, BrowseParameter params) {
		List<ObjectMap> results = new ArrayList<>();
		results.addAll(getRootCategories(type, params));
		results.addAll(getRootModels(type, params));
		return sort(convert(results));
	}

	private List<ObjectMap> getRootCategories(ModelType type, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.aggregation(Aggregations.MODEL_TYPE, ModelType.CATEGORY.name());
		builder.filter("categoryType", SearchFilterValue.phrase(type.name()));
		builder.filter("categoryRefId", SearchFilterValue.phrase(type.name()));
		List<ObjectMap> result = new DataFilter(searchService.searchRaw(builder.build()).data, params).apply();
		Map<String, List<ObjectMap>> lastForPath = getForPath(getAll(type, params.clone().removeFilter()), 1);
		updateCommitInfo(lastForPath, result);
		return result;
	}

	private List<ObjectMap> getRootModels(ModelType type, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		builder.filter("categoryRefId", SearchFilterValue.phrase(type.name()));
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		return new DataFilter(result, params).apply();
	}

	public List<ObjectMap> getForCategory(Repository repo, String refId) {
		BrowseParameter params = new BrowseParameter(repo);
		SearchQueryBuilder builder = builder(params)
				.filter("categoryRefId", SearchFilterValue.phrase(refId));
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		return sort(convert(new DataFilter(result, params).apply()));
	}

	public List<ObjectMap> getForCategory(String refId, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params)
				.filter("categoryRefId", SearchFilterValue.phrase(refId));
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		result = new DataFilter(result, params).apply();
		// get last commit info
		ObjectMap category = getDataset(params.repo, refId, params.commitId);
		ModelType type = category.get("categoryType");
		String path = category.get("fullPath");
		List<ObjectMap> children = getAllCategoryChildren(type, path, params.clone().removeFilter()
				.includeDeleted(true));
		int depth = path.split("/").length + 1;
		Map<String, List<ObjectMap>> lastForPath = getForPath(children, depth);
		updateCommitInfo(lastForPath, result);
		return sort(convert(result));
	}

	private List<ObjectMap> getAllCategoryChildren(ModelType categoryType, String path, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.filter("type", SearchFilterValue.phrase(Arrays.asList(categoryType.name(), ModelType.CATEGORY.name())));
		builder.filter("fullPath", SearchFilterValue.wildcard(path + "/?*"));
		return new DataFilter(searchService.searchRaw(builder.build()).data, params).apply();
	}

	private Map<String, List<ObjectMap>> getForPath(List<ObjectMap> entries, int depth) {
		Map<String, List<ObjectMap>> map = new HashMap<>();
		for (ObjectMap entry : entries) {
			String path = getSubPath(entry.get("fullPath"), depth);
			List<ObjectMap> pathEntries = map.get(path);
			if (pathEntries == null) {
				map.put(path, pathEntries = new ArrayList<>());
			}
			pathEntries.add(entry);
		}
		return map;
	}

	private void updateCommitInfo(Map<String, List<ObjectMap>> lastForPath, List<ObjectMap> entries) {
		for (ObjectMap entry : entries) {
			if (entry.get("type") != ModelType.CATEGORY)
				continue;
			// entries are supposed to be sorted by timestamp
			List<ObjectMap> children = lastForPath.get(entry.get("fullPath"));
			if (children != null) {
				ObjectMap lastChild = children.get(0);
				entry.put("commitId", lastChild.get("commitId"));
				entry.put("commitMessage", lastChild.get("commitMessage"));
				entry.put("commitTimestamp", lastChild.get("commitTimestamp"));
				int count = 0;
				for (ObjectMap child : children) {
					if (child.get("type") == ModelType.CATEGORY)
						continue;
					count++;
				}
				entry.put("count", count);
			} else {
				entry.put("count", 0);
			}

		}
	}

	private String getSubPath(String path, int depth) {
		String subPath = "";
		String[] pathSplit = path.split("/");
		for (int i = 0; i < depth; i++) {
			if (!subPath.isEmpty()) {
				subPath += "/";
			}
			subPath += pathSplit[i];
		}
		return subPath;
	}

	private SearchQueryBuilder builder(BrowseParameter params) {
		SearchQueryBuilder builder = searchService.builder(params.repo.toId());
		if (!Strings.isNullOrEmpty(params.nameFilter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + params.nameFilter + "*"));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		if (params.commitId == null)
			return builder;
		Commit commit = historyService.getCommit(params.repo, params.commitId);
		if (commit == null)
			return builder;
		builder.filter("commitTimestamp", SearchFilterValue.to(commit.timestamp));
		return builder;
	}

	private List<ObjectMap> sort(List<ObjectMap> entries) {
		java.util.Collections.sort(entries, (e1, e2) -> {
			if (e1.get("type") == e2.get("type"))
				return e1.getString("name").toLowerCase().compareTo(e2.getString("name").toLowerCase());
			if (e1.get("type") == ModelType.CATEGORY)
				return -1;
			return 1;
		});
		return entries;
	}

	private List<ObjectMap> convert(List<ObjectMap> entries) {
		return Collections.convert(entries, parser::convert);
	}

	public ObjectMap getDataset(Repository repo, String refId, String commitId) {
		Commit commit = historyService.getCommit(repo, commitId);
		return searchService.getLatest(repo.toId(), refId, commit);
	}

	public ObjectMap getDataset(Repository repo, ModelType type, String refId, String commitId) {
		return searchService.getRaw(repo, type, refId, commitId);
	}

	public static class BrowseParameter implements Cloneable {

		public Repository repo;
		public String nameFilter;
		public String commitId;
		public boolean includeDeleted;

		public BrowseParameter(Repository repo) {
			this(repo, null, null, false);
		}

		public BrowseParameter(Repository repo, String commitId, boolean includeDeleted) {
			this(repo, null, commitId, includeDeleted);
		}

		public BrowseParameter(Repository repo, String nameFilter, String commitId, boolean includeDeleted) {
			this.repo = repo;
			this.nameFilter = nameFilter;
			this.commitId = commitId;
			this.includeDeleted = includeDeleted;
		}

		public BrowseParameter includeDeleted(boolean value) {
			includeDeleted = value;
			return this;
		}

		public BrowseParameter removeFilter() {
			this.nameFilter = null;
			return this;
		}

		@Override
		public BrowseParameter clone() {
			BrowseParameter p = new BrowseParameter(repo);
			p.nameFilter = nameFilter;
			p.commitId = commitId;
			p.includeDeleted = includeDeleted;
			return p;
		}

	}

	private class DataFilter {

		private List<ObjectMap> entries;
		private final BrowseParameter params;
		private final boolean ignoreIfMoved;

		private DataFilter(List<ObjectMap> entries, BrowseParameter params) {
			this(entries, params, false);
		}

		private DataFilter(List<ObjectMap> entries, BrowseParameter params, boolean ignoreIfMoved) {
			this.entries = entries;
			this.params = params;
			this.ignoreIfMoved = ignoreIfMoved;
		}

		private List<ObjectMap> apply() {
			filterPrevious();
			if (!params.includeDeleted)
				filterDeleted();
			if (!ignoreIfMoved)
				filterMoved();
			return entries;
		}

		private void filterPrevious() {
			// filter previous elements (only retain the newest)
			Set<String> alreadyAdded = new HashSet<>();
			entries = Collections.filter(entries, (e) -> !alreadyAdded.add(e.get("refId")));
		}

		private void filterDeleted() {
			entries = Collections.filter(entries, (e) -> e.get("action") == IndexAction.DELETE);
		}

		private void filterMoved() {
			// filter moved non-category elements (only retain if is newest)
			Commit commit = params.commitId != null ? historyService.getCommit(params.repo, params.commitId)
					: historyService.getLastCommit(params.repo);
			Set<String> refIds = new HashSet<>();
			for (ObjectMap entry : entries) {
				if (commit.id.equals(entry.get("commitId")))
					continue;
				refIds.add(entry.get("refId"));
			}
			List<ObjectMap> latest = searchService.getLatest(params.repo.toId(), refIds, commit);
			Map<String, String> latestMap = Collections.map(latest, (e) -> e.get("refId"), (e) -> e.get("commitId"));
			entries = Collections.filter(entries, (e) -> latestMap.containsKey(e.get("refId"))
					&& !e.get("commitId").equals(latestMap.get(e.get("refId"))));
		}
	}

}
