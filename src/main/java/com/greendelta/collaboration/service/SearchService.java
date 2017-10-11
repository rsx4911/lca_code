package com.greendelta.collaboration.service;

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
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.lca.search.SearchClient;
import com.greendelta.lca.search.SearchFilterValue.Type;
import com.greendelta.lca.search.SearchQuery;
import com.greendelta.lca.search.SearchQueryBuilder;
import com.greendelta.lca.search.SearchResult;
import com.greendelta.lca.search.SearchSorting;
import com.greendelta.lca.search.aggregations.SearchAggregation;
import com.greendelta.lca.search.aggregations.results.AggregationResultBuilder;

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
		builder.filter("action", Type.PHRASE, IndexAction.ADD.name(), IndexAction.UPDATE.name());
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(query, "name");
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

	public List<IndexEntry> getAll(Repository repo) {
		return getAll(repo, null, null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		return getAll(repo, type, null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", Type.WILDCART, "*" + nameFilter + "*");
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return parser.parse(client.search(builder.build()));
	}

	public List<IndexEntry> getAll(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("commitId", Type.PHRASE, commit.id);
		return parser.parse(client.search(builder.build()));
	}

	SearchQueryBuilder builder(Repository repo) {
		return new SearchQueryBuilder()
				.page(0)
				.aggregation(Aggregations.REPOSITORY, repo.toId());
	}

	public IndexEntry get(Repository repo, ModelType type, String refId, String commitId) {
		String id = repo.toId() + "/" + refId + "/" + commitId;
		return parser.parse(client.get(type.name().toLowerCase(), id));
	}

	public IndexEntry getLast(Repository repo, String refId) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("refId", Type.PHRASE, refId);
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		SearchResult<Map<String, Object>> result = client.search(builder.build());
		if (result.data.isEmpty())
			return null;
		return parser.parse(result.data.get(0));
	}

	public void index(Collection<IndexEntry> entries) {
		Map<String, Map<String, Map<String, Object>>> contentsByIdByType = new HashMap<>();
		for (IndexEntry entry : entries) {
			setDummyCategoryId(entry);
			Map<String, Object> content = ObjectMap.fromObject(entry);
			Map<String, Map<String, Object>> contentsById = contentsByIdByType.get(entry.type.name());
			if (contentsById == null) {
				contentsByIdByType.put(entry.type.name(), contentsById = new HashMap<>());
			}
			contentsById.put(entry.toIndexId(), content);
		}
		client.index(contentsByIdByType);
	}

	public void index(IndexEntry entry) {
		setDummyCategoryId(entry);
		Map<String, Object> content = ObjectMap.fromObject(entry);
		client.index(entry.type.name().toLowerCase(), entry.toIndexId(), content);
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
		Map<String, Set<String>> idsByType = new HashMap<>();
		for (IndexEntry entry : entries) {
			Set<String> ids = idsByType.get(entry.type.name());
			if (ids == null) {
				idsByType.put(entry.type.name(), ids = new HashSet<>());
			}
			ids.add(entry.toIndexId());
		}
		client.remove(idsByType);
	}

	public void remove(IndexEntry entry) {
		client.remove(entry.type.name().toLowerCase(), entry.toIndexId());
	}

}
