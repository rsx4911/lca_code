package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.Collection;
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
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.SearchSorting;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.results.AggregationResultBuilder;

public class SearchService {

	private final SearchClient client;
	private final RepositoryService repoService;
	private final IndexEntryParser parser = new IndexEntryParser();

	@Inject
	public SearchService(SearchClient searchClient, RepositoryService repoService) {
		this.client = searchClient;
		this.repoService = repoService;
	}

	public void createIndex(Map<String, Object> settings) {
		client.create(settings);
	}

	public SearchResult<IndexEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		List<Repository> repos = repoService.getAllAccessible();
		if (repos.isEmpty())
			return buildEmptyResult(page, pageSize);
		SearchQueryBuilder builder = new SearchQueryBuilder();
		ModelType type = getFilteredModelType(filters.get(Aggregations.MODEL_TYPE.name));
		for (SearchAggregation aggregation : Aggregations.getFilters(type)) {
			Set<String> filterValues = filters.get(aggregation.name);
			if (aggregation.name.equals(Aggregations.REPOSITORY.name)) {
				putRepositoryFilter(builder, filterValues, repos);
			} else if (filterValues != null && !filterValues.isEmpty()) {
				for (String filterValue : filterValues) {
					builder.aggregation(aggregation, filterValue);
				}
			} else {
				builder.aggregation(aggregation);
			}
		}
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(query, SearchFields.get(type));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		builder.page(page);
		builder.pageSize(pageSize);
		return SearchResults.convert(client.search(builder.build()), parser::parse);
	}

	private SearchResult<IndexEntry> buildEmptyResult(int page, int pageSize) {
		SearchResult<IndexEntry> result = new SearchResult<>();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = pageSize;
		for (SearchAggregation aggr : Aggregations.PROCESS_FILTERS) {
			result.aggregations.add(new AggregationResultBuilder().type(aggr.type).name(aggr.name).build());
		}
		return result;
	}

	private ModelType getFilteredModelType(Set<String> values) {
		if (values == null)
			return null;
		if (values.size() > 1)
			return null;
		return ModelType.valueOf(values.iterator().next());
	}

	private void putRepositoryFilter(SearchQueryBuilder builder, Set<String> values, List<Repository> repos) {
		for (Repository repo : repos) {
			if (values != null && !values.contains(repo.toId()))
				continue;
			builder.aggregation(Aggregations.REPOSITORY, repo.toId());
		}
	}

	public SearchResult<IndexEntry> search(SearchQuery query) {
		return SearchResults.convert(client.search(query), parser::parse);
	}

	SearchResult<ObjectMap> searchRaw(SearchQuery query) {
		return SearchResults.convert(client.search(query), parser::convert);
	}

	public List<IndexEntry> getAll(Repository repo) {
		return getAll(repo, (ModelType) null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return parser.parse(client.search(builder.build()));
	}

	public List<IndexEntry> getAll(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (commit != null) {
			builder.filter("commitId", SearchFilterValue.phrase(commit.id));
		}
		return parser.parse(client.search(builder.build()));
	}

	SearchQueryBuilder builder(String repoId) {
		return new SearchQueryBuilder()
				.page(0)
				.aggregation(Aggregations.REPOSITORY, repoId);
	}

	public IndexEntry get(Repository repo, ModelType type, String refId, String commitId) {
		String id = repo.toId() + "/" + refId + "/" + commitId;
		return parser.parse(client.get(type.name().toLowerCase(), id));
	}

	ObjectMap getRaw(Repository repo, ModelType type, String refId, String commitId) {
		String id = repo.toId() + "/" + refId + "/" + commitId;
		return parser.convert(client.get(type.name().toLowerCase(), id));
	}

	IndexAction getLastAction(String repoId, String refId) {
		ObjectMap latest = getLatest(repoId, refId, null);
		if (latest == null)
			return null;
		return IndexAction.from(latest);
	}

	ObjectMap getLatest(String repoId, String refId, Commit until) {
		List<ObjectMap> latest = getLatest(repoId, java.util.Collections.singleton(refId), until);
		if (latest == null || latest.isEmpty())
			return null;
		return latest.get(0);
	}

	List<ObjectMap> getLatest(String repoId, Set<String> refIds, Commit until) {
		List<ObjectMap> results = new ArrayList<>();
		Set<String> remaining = new HashSet<>(refIds);
		Set<String> added = new HashSet<>();
		while (!remaining.isEmpty()) {
			Set<String> next = Collections.pop(remaining, 1000);
			SearchQueryBuilder builder = builder(repoId);
			if (next.size() == 1) {
				builder.filter("refId", SearchFilterValue.phrase(next.iterator().next()));
			} else {
				builder.filter("refId", SearchFilterValue.phrase(next));
			}
			if (until != null) {
				builder.filter("commitTimestamp", SearchFilterValue.to(until.timestamp));
			}
			builder.sortBy("commitTimestamp", SearchSorting.DESC);
			SearchResult<ObjectMap> result = searchRaw(builder.build());
			if (result.data.isEmpty())
				continue;
			for (ObjectMap data : result.data) {
				String refId = data.get("refId").toString();
				if (added.contains(refId))
					continue;
				results.add(data);
				added.add(refId);
			}
		}
		return results;
	}

	public IndexEntry getFirst(String repoId, String refId) {
		SearchQueryBuilder builder = builder(repoId);
		builder.filter("refId", SearchFilterValue.phrase(refId));
		builder.filter("action", SearchFilterValue.phrase(IndexAction.ADD.name()));
		builder.sortBy("commitTimestamp", SearchSorting.ASC);
		SearchResult<Map<String, Object>> result = client.search(builder.build());
		if (result.data.isEmpty())
			return null;
		return parser.parse(result.data.get(0));
	}

	public void index(Collection<IndexEntry> entries) {
		if (entries.isEmpty())
			return;
		Map<String, Map<String, Map<String, Object>>> contentsByIdByType = new HashMap<>();
		for (IndexEntry entry : entries) {
			Map<String, Object> content = toMap(entry);
			Map<String, Map<String, Object>> contentsById = contentsByIdByType.get(entry.type.name().toLowerCase());
			if (contentsById == null) {
				contentsByIdByType.put(entry.type.name().toLowerCase(), contentsById = new HashMap<>());
			}
			contentsById.put(entry.toIndexId(), content);
		}
		client.index(contentsByIdByType);
	}

	public void index(IndexEntry entry) {
		ObjectMap content = toMap(entry);
		client.index(entry.type.name().toLowerCase(), entry.toIndexId(), content);
	}

	private ObjectMap toMap(IndexEntry entry) {
		setDummyCategoryId(entry);
		ObjectMap map = ObjectMap.fromObject(entry);
		map.put("typeOrdinal", ModelTypes.getOrdinal(entry.type, entry.categoryType));
		return map;
	}

	private void setDummyCategoryId(IndexEntry entry) {
		if (!Strings.isNullOrEmpty(entry.categoryRefId))
			return;
		if (entry.type == ModelType.CATEGORY) {
			entry.categoryRefId = entry.categoryType.name();
		} else {
			entry.categoryRefId = entry.type.name();
		}
	}

	public void remove(Collection<IndexEntry> entries) {
		if (entries.isEmpty())
			return;
		Map<String, Set<String>> idsByType = new HashMap<>();
		for (IndexEntry entry : entries) {
			Set<String> ids = idsByType.get(entry.type.name().toLowerCase());
			if (ids == null) {
				idsByType.put(entry.type.name().toLowerCase(), ids = new HashSet<>());
			}
			ids.add(entry.toIndexId());
		}
		client.remove(idsByType);
	}

	public void remove(IndexEntry entry) {
		client.remove(entry.type.name().toLowerCase(), entry.toIndexId());
	}

}
