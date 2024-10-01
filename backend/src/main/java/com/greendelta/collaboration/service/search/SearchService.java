package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.search.DsEntry;
import com.greendelta.collaboration.search.DsEntryParser;
import com.greendelta.collaboration.search.Index;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.results.AggregationResultBuilder;
import com.greendelta.search.wrapper.score.Comparator;
import com.greendelta.search.wrapper.score.Score;

@Service
public class SearchService {

	private static final String[] SEARCH_FIELDS = { "refId", "versions.name" };
	private final RepositoryService repoService;
	private final UserService userService;
	private final SettingsService settings;
	private final DsEntryParser parser = new DsEntryParser();

	public SearchService(RepositoryService repoService, UserService userService,
			SettingsService settings) {
		this.repoService = repoService;
		this.userService = userService;
		this.settings = settings;
	}

	public SearchResult<DsEntry> query(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		try (var accessibleRepos = repoService.getAllAccessible()) {
			if (accessibleRepos.isEmpty())
				return buildEmptyResult(page, pageSize);
			var builder = new SearchQueryBuilder();
			var filteredTypes = getFilteredModelTypes(filters.get(Aggregations.MODEL_TYPE.name));
			putAggregations(builder, accessibleRepos, filteredTypes, filters);
			if (!Strings.nullOrEmpty(query)) {
				builder.query(toWildcardQuery(query.toLowerCase()), SEARCH_FIELDS);
			}
			builder.page(page);
			builder.pageSize(pageSize);
			applyTypeOrder(builder);
			var client = userService.getCurrentUser().isAnonymous()
					? settings.searchConfig.getSearchClient(SearchIndex.PUBLIC)
					: settings.searchConfig.getSearchClient(SearchIndex.PRIVATE);
			var searchQuery = builder.build();
			var result = client.search(searchQuery);
			return SearchResults.convert(result, parser::parse);
		}
	}

	private Set<ModelType> getFilteredModelTypes(Set<String> values) {
		if (values == null || values.isEmpty())
			return new HashSet<>();
		return values.stream().map(v -> ModelType.valueOf(v)).collect(Collectors.toSet());
	}

	private void putAggregations(SearchQueryBuilder builder, List<Repository> accessibleRepos,
			Set<ModelType> filteredTypes, Map<String, Set<String>> filters) {
		for (var aggregation : Aggregations.getFilters(filteredTypes)) {
			if (aggregation.name.contains(".") && !aggregation.name.equals(Aggregations.REPOSITORY.name))
				continue;
			var filterValues = filters.get(aggregation.name);
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
		var repos = new ArrayList<String>();
		for (var repo : accessibleRepos) {
			if (values != null && !values.contains(repo.path()))
				continue;
			repos.add(repo.path());
		}
		if (repos.isEmpty()) {
			builder.aggregation(Aggregations.REPOSITORY);
		} else {
			builder.aggregation(Aggregations.REPOSITORY, SearchFilterValue.term(repos));
		}
	}

	private void putTypeFilter(SearchQueryBuilder builder, Set<ModelType> filteredTypes) {
		var types = new ArrayList<String>();
		var allTypes = settings.serverConfig.getModelTypes();
		for (var type : allTypes) {
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

	private String toWildcardQuery(String query) {
		var splitter = new StringTokenizer(query, "\"", true);
		var escaped = false;
		var values = new ArrayList<String>();
		while (splitter.hasMoreTokens()) {
			var token = splitter.nextToken();
			if ("\"".equals(token)) {
				escaped = !escaped;
			} else if (escaped) {
				values.add("\"" + token + "\"");
			} else {
				token = token.replace("@", " ");
				for (String word : token.trim().split("\\s+")) {
					values.add("*" + word + "*");
				}
			}
		}
		return values.stream().collect(Collectors.joining(" "));
	}

	private void applyTypeOrder(SearchQueryBuilder builder) {
		var score = new Score(Aggregations.MODEL_TYPE.field);
		var types = settings.serverConfig.getModelTypes();
		for (var i = 0; i < types.length; i++) {
			score.addCase(types.length - i + 1, Comparator.EQUALS, "\"" + types[i].name() + "\"");
		}
		score.addElse(1);
		builder.score(score);
	}

	private SearchResult<DsEntry> buildEmptyResult(int page, int pageSize) {
		var result = new SearchResult<DsEntry>();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = pageSize;
		for (var aggr : Aggregations.PROCESS_FILTERS) {
			result.aggregations.add(new AggregationResultBuilder().type(aggr.type).name(aggr.name).build());
		}
		return result;
	}

	Index on(SearchIndex... indices) {
		var clients = Arrays.asList(indices).stream().map(settings.searchConfig::getSearchClient).toList();
		return new Index(clients.toArray(new SearchClient[clients.size()]));
	}

}
