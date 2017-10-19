package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
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
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.lca.search.SearchFilterValue;
import com.greendelta.lca.search.SearchQueryBuilder;
import com.greendelta.lca.search.SearchSorting;

// This service filters previous versions of multiple data sets, so only the latest remains
public class BrowseService {

	private final SearchService searchService;
	private final HistoryService historyService;

	@Inject
	public BrowseService(SearchService searchService, HistoryService historyService) {
		this.searchService = searchService;
		this.historyService = historyService;
	}

	public List<Map<String, Object>> getRootContent(Repository repo, String untilCommitId, boolean includeDeleted) {
		List<Map<String, Object>> types = new ArrayList<>();
		for (ModelType type : ModelTypes.SORTED) {
			File dir = repo.getModelDir(type, false);
			if (!dir.exists())
				continue;
			if (getAll(repo, type, untilCommitId, includeDeleted).isEmpty())
				continue;
			Map<String, Object> map = new HashMap<>();
			map.put("type", type);
			map.put("deleted", includeDeleted && getAll(repo, type, untilCommitId).isEmpty());
			types.add(map);
		}
		return types;
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		return getAll(repo, type, null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type, String untilCommitId) {
		return getAll(repo, type, untilCommitId, false);
	}

	private List<IndexEntry> getAll(Repository repo, ModelType type, String untilCommitId, boolean includeDeleted) {
		SearchQueryBuilder builder = searchService.builder(repo.toId());
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		addCommitFilter(builder, repo, untilCommitId);
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return sort(filter(result, repo, untilCommitId, includeDeleted, false));
	}

	public List<IndexEntry> getUncategorized(Repository repo, ModelType type, String untilCommitId, String nameFilter,
			boolean includeDeleted) {
		List<IndexEntry> results = new ArrayList<>();
		results.addAll(getRootCategories(repo, type, untilCommitId, nameFilter, includeDeleted));
		results.addAll(getRootModels(repo, type, untilCommitId, nameFilter, includeDeleted));
		return sort(results);
	}

	private List<IndexEntry> getRootModels(Repository repo, ModelType type, String untilCommitId, String nameFilter,
			boolean includeDeleted) {
		SearchQueryBuilder builder = builder(repo, nameFilter);
		builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		builder.filter("categoryRefId", SearchFilterValue.phrase(type.name()));
		addCommitFilter(builder, repo, untilCommitId);
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return filter(result, repo, untilCommitId, includeDeleted, true);
	}

	private List<IndexEntry> getRootCategories(Repository repo, ModelType type, String untilCommitId,
			String nameFilter, boolean includeDeleted) {
		SearchQueryBuilder builder = builder(repo, nameFilter);
		builder.aggregation(Aggregations.MODEL_TYPE, ModelType.CATEGORY.name());
		builder.filter("categoryType", SearchFilterValue.phrase(type.name()));
		builder.filter("categoryRefId", SearchFilterValue.phrase(type.name()));
		addCommitFilter(builder, repo, untilCommitId);
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return filter(result, repo, untilCommitId, includeDeleted, true);
	}

	public List<IndexEntry> getForCategory(Repository repo, String id) {
		return getForCategory(repo, id, null);
	}

	public List<IndexEntry> getForCategory(Repository repo, String id, String untilCommitId) {
		return getForCategory(repo, id, untilCommitId, null, false);
	}

	public List<IndexEntry> getForCategory(Repository repo, String id, String untilCommitId, String nameFilter,
			boolean includeDeleted) {
		SearchQueryBuilder builder = builder(repo, nameFilter)
				.filter("categoryRefId", SearchFilterValue.phrase(id));
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + nameFilter + "*"));
		}
		addCommitFilter(builder, repo, untilCommitId);
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return sort(filter(result, repo, untilCommitId, includeDeleted, true));
	}

	private List<IndexEntry> sort(List<IndexEntry> entries) {
		java.util.Collections.sort(entries, (e1, e2) -> {
			if (e1.type == e2.type)
				return e1.name.toLowerCase().compareTo(e2.name.toLowerCase());
			if (e1.type == ModelType.CATEGORY)
				return -1;
			return 1;
		});
		return entries;
	}

	private SearchQueryBuilder builder(Repository repo, String nameFilter) {
		SearchQueryBuilder builder = searchService.builder(repo.toId());
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + nameFilter + "*"));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return builder;
	}

	private void addCommitFilter(SearchQueryBuilder builder, Repository repo, String untilCommitId) {
		if (untilCommitId == null)
			return;
		Commit commit = historyService.getCommit(repo, untilCommitId);
		if (commit == null)
			return;
		builder.filter("commitTimestamp", SearchFilterValue.to(commit.timestamp));
	}

	private List<IndexEntry> filter(List<IndexEntry> entries, Repository repo, String untilCommitId,
			boolean includeDeleted, boolean checkMoved) {
		// filter previous elements (only retain the newest)
		Set<String> alreadyAdded = new HashSet<>();
		entries = Collections.filter(entries, (e) -> !alreadyAdded.add(e.refId));
		if (!includeDeleted) {
			// filter deleted entries
			entries = Collections.filter(entries, (e) -> e.action == IndexAction.DELETE);
		}
		if (!checkMoved)
			return entries;
		// filter moved non-category elements (only retain if is newest)
		Commit commit = untilCommitId != null ? historyService.getCommit(repo, untilCommitId) : null;
		List<String> refIds = Collections.convert(entries, (e) -> e.refId);
		List<IndexEntry> latest = searchService.getLatest(repo.toId(), new HashSet<>(refIds), commit);
		Map<String, IndexEntry> latestMap = Collections.map(latest, (e) -> e.refId);
		entries = Collections.filter(entries, (e) -> e.type != ModelType.CATEGORY
				&& !e.commitId.equals(latestMap.get(e.refId).commitId));
		return entries;
	}

	public IndexEntry getDataset(Repository repo, String refId, String untilCommitId) {
		Commit commit = historyService.getCommit(repo, untilCommitId);
		return searchService.getLatest(repo.toId(), refId, commit);
	}

	public IndexEntry getDataset(Repository repo, ModelType type, String refId, String commitId) {
		return searchService.get(repo, type, refId, commitId);
	}

}
