package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.results.AggregationResultBuilder;

class QueryService {

	private final SettingsService settingsService;
	private final RepositoryService repoService;
	private final ScoreService scoreService;
	private final DsEntryParser parser = new DsEntryParser();

	@Inject
	QueryService(SettingsService settingsService, RepositoryService repoService, ScoreService scoreService) {
		this.settingsService = settingsService;
		this.repoService = repoService;
		this.scoreService = scoreService;
	}

	SearchResult<DsEntry> query(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		List<Repository> accessibleRepos = repoService.getAllAccessible();
		if (accessibleRepos.isEmpty())
			return buildEmptyResult(page, pageSize);
		SearchQueryBuilder builder = new SearchQueryBuilder();
		Set<ModelType> filteredTypes = getFilteredModelTypes(filters.get(Aggregations.MODEL_TYPE.name));
		putAggregations(builder, accessibleRepos, filteredTypes, filters);
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(toWildcardQuery(query.toLowerCase()), "versions.name");
		}
		builder.page(page);
		builder.pageSize(pageSize);
		scoreService.applyTo(builder);
		SearchClient client = settingsService.searchConfig.getSearchClient();
		SearchQuery searchQuery = builder.build();
		SearchResult<Map<String, Object>> result = client.search(searchQuery);
		return SearchResults.convert(result, parser::parse);
	}

	private static String toWildcardQuery(String query) {
		StringTokenizer splitter = new StringTokenizer(query, "\"", true);
		boolean escaped = false;
		List<String> values = new ArrayList<>();
		while (splitter.hasMoreTokens()) {
			String token = splitter.nextToken();
			if ("\"".equals(token)) {
				escaped = !escaped;
			} else if (escaped) {
				values.add("\"" + token + "\"");
			} else {
				token = token.replace("@", " ");
				for (String word : token.trim().split("\\s+")) {
					values.add(word + "*");
				}
			}
		}
		return Collections.join(values, " ");
	}

	private Set<ModelType> getFilteredModelTypes(Set<String> values) {
		if (values == null || values.isEmpty())
			return new HashSet<>();
		Set<ModelType> types = new HashSet<>();
		for (String value : values) {
			types.add(ModelType.valueOf(value));
		}
		return types;
	}

	private void putAggregations(SearchQueryBuilder builder, List<Repository> accessibleRepos,
			Set<ModelType> filteredTypes, Map<String, Set<String>> filters) {
		for (SearchAggregation aggregation : Aggregations.getFilters(filteredTypes)) {
			if (aggregation.name.contains(".") && !aggregation.name.equals(Aggregations.REPOSITORY.name))
				continue;
			Set<String> filterValues = filters.get(aggregation.name);
			if (aggregation.name.equals(Aggregations.REPOSITORY.name)) {
				putRepositoryFilter(builder, filterValues, accessibleRepos);
			} else if (aggregation.name.equals(Aggregations.MODEL_TYPE.name)) {
				putTypeFilter(builder, filteredTypes);
			} else if (filterValues != null && !filterValues.isEmpty()) {
				for (String filterValue : filterValues) {
					builder.aggregation(aggregation, filterValue);
				}
			} else {
				builder.aggregation(aggregation);
			}
		}
	}

	private void putRepositoryFilter(SearchQueryBuilder builder, Set<String> values, List<Repository> accessibleRepos) {
		List<String> repos = new ArrayList<>();
		for (Repository repo : accessibleRepos) {
			if (values != null && !values.contains(repo.toId()))
				continue;
			repos.add(repo.toId());
		}
		if (repos.isEmpty()) {
			builder.aggregation(Aggregations.REPOSITORY);
		} else {
			builder.aggregation(Aggregations.REPOSITORY, SearchFilterValue.term(repos));
		}
	}

	private void putTypeFilter(SearchQueryBuilder builder, Set<ModelType> filteredTypes) {
		List<String> types = new ArrayList<>();
		ModelType[] allTypes = settingsService.serverConfig.getModelTypes();
		for (ModelType type : allTypes) {
			if (!filteredTypes.isEmpty() && !filteredTypes.contains(type))
				continue;
			types.add(type.name());
		}
		if (types.isEmpty()) {
			builder.aggregation(Aggregations.MODEL_TYPE);
		} else {
			builder.aggregation(Aggregations.MODEL_TYPE, SearchFilterValue.term(types));
		}
	}

	private SearchResult<DsEntry> buildEmptyResult(int page, int pageSize) {
		SearchResult<DsEntry> result = new SearchResult<>();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = pageSize;
		for (SearchAggregation aggr : Aggregations.PROCESS_FILTERS) {
			result.aggregations.add(new AggregationResultBuilder().type(aggr.type).name(aggr.name).build());
		}
		return result;
	}

}
