package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.SearchSorting;

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
			if (!params.repo.has(type))
				continue;
			if (getUncategorized(type, params).isEmpty())
				continue;
			ObjectMap map = ObjectMap.fromMap(new HashMap<>());
			map.put("type", type);
			ObjectMap last = getLastChanged(type, null, params);
			// put commit info of last changed children
			map.put("commitId", last.get("commitId"));
			map.put("commitMessage", last.get("commitMessage"));
			map.put("commitTimestamp", last.get("commitTimestamp"));
			map.put("deleted", params.includeDeleted
					&& getUncategorized(type, params.clone().includeDeleted(false)).isEmpty());
			types.add(map);
		}
		return types;
	}

	public long getCount(ModelType type, String path, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.page(1);
		builder.pageSize(1);
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		if (path != null) {
			builder.filter("fullPath", SearchFilterValue.wildcard(path + "/*"));
		}
		SearchResult<ObjectMap> result = searchService.searchRaw(builder.build());
		return result.resultInfo.totalCount;
	}

	public List<ObjectMap> getAll(ModelType type, BrowseParameter params) {
		return getAll(type, null, params);
	}

	public List<ObjectMap> getAll(ModelType type, String path, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		if (path != null) {
			builder.filter("fullPath", SearchFilterValue.wildcard(path + "/*"));
		}
		return searchService.searchRaw(builder.build()).data;
	}

	ObjectMap getLastChanged(ModelType categoryType, String path, BrowseParameter params) {
		ObjectMap lastModel = getLastChanged(categoryType, null, path, params);
		ObjectMap lastCategory = getLastChanged(ModelType.CATEGORY, categoryType, path, params);
		long modelTimestamp = lastModel != null ? lastModel.getLong("commitTimestamp") : 0;
		long categoryTimestamp = lastCategory != null ? lastCategory.getLong("commitTimestamp") : 0;
		if (modelTimestamp > categoryTimestamp)
			return lastModel;
		return lastCategory;
	}

	private ObjectMap getLastChanged(ModelType type, ModelType categoryType, String path, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		if (categoryType != null) {
			builder.filter("categoryType", SearchFilterValue.term(categoryType.name()));
		}
		if (path != null) {
			builder.filter("fullPath", SearchFilterValue.wildcard(path + "/?*"));
		}
		builder.page(1);
		builder.pageSize(1);
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	public List<ObjectMap> getUncategorized(ModelType type, BrowseParameter params) {
		List<ObjectMap> results = new ArrayList<>();
		results.addAll(getRootCategories(type, params));
		results.addAll(getRootModels(type, params));
		return sort(convert(results));
	}

	private List<ObjectMap> getRootCategories(ModelType type, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(ModelType.CATEGORY.name()));
		builder.filter("categoryType", SearchFilterValue.term(type.name()));
		builder.filter("categoryRefId", SearchFilterValue.term(type.name()));
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		appendCommitInfo(result, params);
		return result;
	}

	private List<ObjectMap> getRootModels(ModelType type, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params);
		builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		builder.filter("categoryRefId", SearchFilterValue.term(type.name()));
		return searchService.searchRaw(builder.build()).data;
	}

	public List<ObjectMap> getForCategory(Repository repo, String refId) {
		BrowseParameter params = new BrowseParameter(repo);
		SearchQueryBuilder builder = builder(params)
				.filter("categoryRefId", SearchFilterValue.term(refId));
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		return sort(convert(result));
	}

	public List<ObjectMap> getForCategory(String refId, BrowseParameter params) {
		SearchQueryBuilder builder = builder(params)
				.filter("categoryRefId", SearchFilterValue.term(refId));
		List<ObjectMap> result = searchService.searchRaw(builder.build()).data;
		appendCommitInfo(result, params);
		return sort(convert(result));
	}

	private void appendCommitInfo(List<ObjectMap> entries, BrowseParameter params) {
		params = params.clone().removeFilter().includeDeleted(true);
		for (ObjectMap entry : entries) {
			ModelType type = ModelTypes.from(entry, "type");
			if (type != ModelType.CATEGORY)
				continue;
			type = ModelTypes.from(entry, "categoryType");
			String path = entry.getString("fullPath");
			ObjectMap lastChanged = getLastChanged(type, path, params);
			if (lastChanged == null || lastChanged.getLong("commitTimestamp") < entry.getLong("commitTimestamp")) {
				lastChanged = entry;
			}
			entry.put("commitId", lastChanged.get("commitId"));
			entry.put("commitMessage", lastChanged.get("commitMessage"));
			entry.put("commitTimestamp", lastChanged.get("commitTimestamp"));
		}
	}

	private SearchQueryBuilder builder(BrowseParameter params) {
		SearchQueryBuilder builder = searchService.builder(params.repo);
		if (!Strings.isNullOrEmpty(params.nameFilter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + params.nameFilter.toLowerCase() + "*"));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		if (!params.includeDeleted) {
			Set<SearchFilterValue> values = new HashSet<>();
			values.add(SearchFilterValue.term(IndexAction.ADD.name()));
			values.add(SearchFilterValue.term(IndexAction.UPDATE.name()));
			builder.filter("action", values);
		}
		if (params.commitId == null)
			return builder.filter("mostRecent", SearchFilterValue.term(true));
		Commit commit = historyService.getCommit(params.repo, params.commitId);
		if (commit == null)
			return builder;
		builder.filter("commits", SearchFilterValue.term(params.commitId));
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
		return Collections.convertToList(entries, parser::convert);
	}

	public ObjectMap getDataset(Repository repo, String refId, String commitId) {
		return searchService.getRaw(repo, refId, commitId);
	}

	public ObjectMap getMostRecent(Repository repo, String refId, String commitId) {
		return searchService.getMostRecentUntil(repo, refId, commitId);
	}

	public static class BrowseParameter implements Cloneable {

		public Repository repo;
		public String nameFilter;
		public String commitId;
		public boolean includeDeleted;

		public BrowseParameter(Repository repo) {
			this(repo, null, null);
		}

		public BrowseParameter(Repository repo, String commitId) {
			this(repo, null, commitId);
		}

		public BrowseParameter(Repository repo, String nameFilter, String commitId) {
			this.repo = repo;
			this.nameFilter = nameFilter;
			this.commitId = commitId;
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

}
