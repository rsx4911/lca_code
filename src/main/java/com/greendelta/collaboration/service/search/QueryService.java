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
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
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
	private final UserService userService;
	private final ScoreService scoreService;
	private final IndexEntryParser parser = new IndexEntryParser();

	@Inject
	QueryService(SettingsService settingsService, RepositoryService repoService, UserService userService,
			ScoreService scoreService) {
		this.settingsService = settingsService;
		this.repoService = repoService;
		this.userService = userService;
		this.scoreService = scoreService;
	}

	SearchResult<IndexEntry> query(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		List<Repository> repos = repoService.getAllAccessible();
		if (repos.isEmpty())
			return buildEmptyResult(page, pageSize);
		SearchQueryBuilder builder = new SearchQueryBuilder();
		Set<ModelType> types = getFilteredModelTypes(filters.get(Aggregations.MODEL_TYPE.name));
		putAggregations(builder, repos, filters, types);
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		if (!loggedIn) {
			if (filters.get("mostRecent") == null || !filters.get("mostRecent").contains("true")) {
				builder.filter("mostRecent", SearchFilterValue.term(true));
			}
			Set<SearchFilterValue> allowed = new HashSet<>();
			allowed.add(SearchFilterValue.term(IndexAction.ADD.name()));
			allowed.add(SearchFilterValue.term(IndexAction.UPDATE.name()));
			builder.filter("action", allowed);
		}
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(toWildcardQuery(query.toLowerCase()), "name");
		}
		builder.page(page);
		builder.pageSize(pageSize);
		scoreService.apply(builder);
		SearchClient client = settingsService.getSearchConfig().getSearchClient();
		SearchQuery searchQuery = builder.build();
		SearchResult<Map<String, Object>> result = client.search(searchQuery);
		if (loggedIn)
			return SearchResults.convert(result, parser::parse);
		return prepResult(result);
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

	private void putAggregations(SearchQueryBuilder builder, List<Repository> repos, Map<String, Set<String>> filters,
			Set<ModelType> types) {
		for (SearchAggregation aggregation : Aggregations.getFilters(types)) {
			Set<String> filterValues = filters.get(aggregation.name);
			if (aggregation.name.equals(Aggregations.REPOSITORY.name)) {
				putRepositoryFilter(builder, filterValues, repos);
			} else if (aggregation.name.equals(Aggregations.MODEL_TYPE.name)) {
				if (types == null || types.isEmpty()) {
					builder.aggregation(Aggregations.MODEL_TYPE, getModelTypes());
				} else {
					for (ModelType type : types) {
						builder.aggregation(Aggregations.MODEL_TYPE, type.name());
					}
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
		return SearchResults.convert(result, parser::parse);
	}

	private String[] getModelTypes() {
		Set<String> types = new HashSet<>();
		for (ModelType type : settingsService.getModelTypes()) {
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

	private Set<ModelType> getFilteredModelTypes(Set<String> values) {
		if (values == null || values.isEmpty())
			return null;
		Set<ModelType> types = new HashSet<>();
		for (String value : values) {
			types.add(ModelType.valueOf(value));
		}
		return types;
	}

	private void putRepositoryFilter(SearchQueryBuilder builder, Set<String> values, List<Repository> repos) {
		for (Repository repo : repos) {
			if (values != null && !values.contains(repo.toId()))
				continue;
			builder.aggregation(Aggregations.REPOSITORY, repo.toId());
		}
	}

}
