package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.search.SearchQueryBuilder;
import com.greendelta.collaboration.search.SearchFilterValue.Type;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.ModelTypes;

public class BrowseService {

	private final SearchService searchService;
	private final HistoryService historyService;
	private final FetchService fetchService;

	@Inject
	public BrowseService(SearchService searchService, HistoryService historyService, FetchService fetchService) {
		this.searchService = searchService;
		this.historyService = historyService;
		this.fetchService = fetchService;
	}

	public List<ModelType> getRootContent(Repository repo) {
		List<ModelType> types = new ArrayList<>();
		for (ModelType type : ModelTypes.SORTED) {
			File dir = repo.getModelDir(type, false);
			if (dir.exists())
				types.add(type);
		}
		return types;
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		return onlyIfExists(repo, onlyLast(searchService.getAll(repo, type)));
	}

	public List<IndexEntry> getUncategorized(Repository repo, ModelType type, String nameFilter) {
		List<IndexEntry> results = new ArrayList<>();
		results.addAll(getRootCategories(repo, type, nameFilter));
		results.addAll(getRootModels(repo, type, nameFilter));
		return results;
	}

	private List<IndexEntry> getRootModels(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = searchService.builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
			builder.aggregation(Aggregations.CATEGORY, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		List<IndexEntry> result = searchService.search(builder.build());
		return onlyIfExists(repo, onlyLast(result));
	}

	private List<IndexEntry> getRootCategories(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = searchService.builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, ModelType.CATEGORY.name());
			builder.aggregation(Aggregations.CATEGORY_TYPE, type.name());
			builder.aggregation(Aggregations.CATEGORY, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		List<IndexEntry> result = searchService.search(builder.build());
		return onlyIfExists(repo, onlyLast(result));
	}

	public List<IndexEntry> getForCategory(Repository repo, String id) {
		return getForCategory(repo, id, null);
	}

	public List<IndexEntry> getForCategory(Repository repo, String id, String nameFilter) {
		SearchQueryBuilder builder = searchService.builder(repo)
				.aggregation(Aggregations.CATEGORY, id);
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		List<IndexEntry> result = searchService.search(builder.build());
		return onlyIfExists(repo, onlyLast(result));
	}

	private List<IndexEntry> onlyLast(List<IndexEntry> entries) {
		List<IndexEntry> filtered = new ArrayList<>();
		for (IndexEntry entry : entries) {
			if (!historyService.isLastCommit(entry))
				continue;
			filtered.add(entry);
		}
		return filtered;
	}

	private List<IndexEntry> onlyIfExists(Repository repo, List<IndexEntry> entries) {
		List<IndexEntry> filtered = new ArrayList<>();
		for (IndexEntry entry : entries) {
			if (!fetchService.hasDataset(repo, entry.type, entry.refId, entry.commitId))
				continue;
			filtered.add(entry);
		}
		return filtered;
	}

	public IndexEntry getDataset(Repository repo, ModelType type, String refId, String commitId) {
		return searchService.get(repo, type, refId, commitId);
	}

	public boolean categoryExists(Repository repo, String categoryId) {
		return searchService.contains(repo, categoryId);
	}

}
