package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.lca.search.SearchFilterValue.Type;
import com.greendelta.lca.search.SearchQueryBuilder;
import com.greendelta.lca.search.SearchSorting;

public class BrowseService {

	private final SearchService searchService;

	@Inject
	public BrowseService(SearchService searchService) {
		this.searchService = searchService;
	}

	public List<ModelType> getRootContent(Repository repo) {
		List<ModelType> types = new ArrayList<>();
		for (ModelType type : ModelTypes.SORTED) {
			File dir = repo.getModelDir(type, false);
			if (!dir.exists())
				continue;
			if (getAll(repo, type).isEmpty())
				continue;
			types.add(type);
		}
		return types;
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		return sort(filter(searchService.getAll(repo, type)));
	}

	public List<IndexEntry> getUncategorized(Repository repo, ModelType type, String nameFilter) {
		List<IndexEntry> results = new ArrayList<>();
		results.addAll(getRootCategories(repo, type, nameFilter));
		results.addAll(getRootModels(repo, type, nameFilter));
		return sort(results);
	}

	private List<IndexEntry> getRootModels(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo, nameFilter);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
			builder.filter("categoryRefId", type.name(), Type.PHRASE);
		}
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return filter(result);
	}

	private List<IndexEntry> getRootCategories(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo, nameFilter);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, ModelType.CATEGORY.name());
			builder.filter("categoryType", type.name(), Type.PHRASE);
			builder.filter("categoryRefId", type.name(), Type.PHRASE);
		}
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return filter(result);
	}

	public List<IndexEntry> getForCategory(Repository repo, String id) {
		return getForCategory(repo, id, null);
	}

	public List<IndexEntry> getForCategory(Repository repo, String id, String nameFilter) {
		SearchQueryBuilder builder = builder(repo, nameFilter)
				.filter("categoryRefId", id, Type.PHRASE);
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		List<IndexEntry> result = searchService.search(builder.build()).data;
		return sort(filter(result));
	}

	private List<IndexEntry> sort(List<IndexEntry> entries) {
		Collections.sort(entries, (e1, e2) -> {
			if (e1.type == e2.type)
				return e1.name.toLowerCase().compareTo(e2.name.toLowerCase());
			if (e1.type == ModelType.CATEGORY)
				return -1;
			return 1;
		});
		return entries;
	}

	private SearchQueryBuilder builder(Repository repo, String nameFilter) {
		SearchQueryBuilder builder = searchService.builder(repo);
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return builder;
	}

	private List<IndexEntry> filter(List<IndexEntry> entries) {
		return filterDeleted(filterPrevious(entries));
	}

	private List<IndexEntry> filterDeleted(List<IndexEntry> entries) {
		List<IndexEntry> filtered = new ArrayList<>();
		for (IndexEntry entry : entries) {
			if (entry.action == IndexAction.DELETE)
				continue;
			filtered.add(entry);
		}
		return filtered;
	}

	// entries are sorted descending by commit timestamp
	// only retain the last commited element for a ref id
	// means only retain the first element in the list
	private List<IndexEntry> filterPrevious(List<IndexEntry> entries) {
		Set<String> alreadyAdded = new HashSet<>();
		List<IndexEntry> filtered = new ArrayList<>();
		for (IndexEntry entry : entries) {
			String key = entry.type.name() + "_" + entry.refId;
			if (alreadyAdded.contains(key))
				continue;
			filtered.add(entry);
			alreadyAdded.add(key);
		}
		return filtered;
	}

	public IndexEntry getDataset(Repository repo, ModelType type, String refId, String commitId) {
		return searchService.get(repo, type, refId, commitId);
	}

	public boolean hasDataset(Repository repo, ModelType type, String refId, String commitId) {
		IndexEntry entry = searchService.get(repo, type, refId, commitId);
		return entry != null && entry.action != IndexAction.DELETE;
	}

	public boolean hasDataset(Repository repo, String refId) {
		IndexEntry entry = searchService.getLast(repo, refId);
		return entry != null && entry.action != IndexAction.DELETE;
	}

}
