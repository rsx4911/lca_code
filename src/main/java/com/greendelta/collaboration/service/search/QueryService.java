package com.greendelta.collaboration.service.search;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.results.AggregationResultBuilder;

class QueryService {

	private final SettingsService settingsService;
	private final RepositoryService repoService;
	private final UserService userService;
	private final IndexEntryParser parser = new IndexEntryParser();

	@Inject
	QueryService(SettingsService settingsService, RepositoryService repoService, UserService userService) {
		this.settingsService = settingsService;
		this.repoService = repoService;
		this.userService = userService;
	}

	SearchResult<IndexEntry> query(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		List<Repository> repos = repoService.getAllAccessible();
		if (repos.isEmpty())
			return buildEmptyResult(page, pageSize);
		SearchQueryBuilder builder = new SearchQueryBuilder();
		ModelType type = getFilteredModelType(filters.get(Aggregations.MODEL_TYPE.name));
		putAggregations(builder, repos, filters, type);
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(query, SearchFields.get(type, loggedIn));
		}
		builder.page(page);
		builder.pageSize(pageSize);
		Scoring.applyType(builder);
		Scoring.applyCommitTimestamp(builder);
		SearchClient client = settingsService.getSearchConfig().getSearchClient();
		SearchResult<Map<String, Object>> result = client.search(builder.build());
		if (loggedIn)
			return SearchResults.convert(result, parser::parse);
		return prepResult(result);
	}

	private void putAggregations(SearchQueryBuilder builder, List<Repository> repos, Map<String, Set<String>> filters,
			ModelType type) {
		for (SearchAggregation aggregation : Aggregations.getFilters(type)) {
			Set<String> filterValues = filters.get(aggregation.name);
			if (aggregation.name.equals(Aggregations.REPOSITORY.name)) {
				putRepositoryFilter(builder, filterValues, repos);
			} else if (aggregation.name.equals(Aggregations.MODEL_TYPE.name)) {
				if (type == null) {
					builder.aggregation(Aggregations.MODEL_TYPE, getModelTypes());
				} else {
					builder.aggregation(Aggregations.MODEL_TYPE, type.name());
				}
			} else if (filterValues != null && !filterValues.isEmpty()) {
				for (String filterValue : filterValues) {
					builder.aggregation(aggregation, filterValue);
				}
			} else {
				builder.aggregation(aggregation);
			}
		}
	}

	private SearchResult<IndexEntry> prepResult(SearchResult<Map<String, Object>> result) {
		// TODO only return newest and undeleted versions to anonymous users
		return SearchResults.convert(result, parser::parse);
	}

	private String[] getModelTypes() {
		Set<String> types = new HashSet<>();
		for (ModelType type : ModelType.categorized()) {
			types.add(type.name());
		}
		return types.toArray(new String[types.size()]);
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

}
