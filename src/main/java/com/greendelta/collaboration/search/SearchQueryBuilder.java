package com.greendelta.collaboration.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.greendelta.collaboration.search.SearchParameter.Conjunction;
import com.greendelta.collaboration.search.SearchParameterValue.Type;
import com.greendelta.collaboration.search.aggregations.SearchAggregation;

public class SearchQueryBuilder {

	private String query;
	private Map<String, SearchParameter> filters = new HashMap<>();
	private int page = 0;
	private int pageSize = SearchQuery.DEFAULT_PAGE_SIZE;
	private Set<SearchAggregation> aggregations = new HashSet<>();
	private Map<String, SearchSorting> sortBy = new HashMap<>();

	public SearchQueryBuilder query(String query) {
		this.query = query;
		return this;
	}

	public SearchQueryBuilder page(int page) {
		this.page = page;
		return this;
	}

	public SearchQueryBuilder pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public SearchQueryBuilder aggregation(SearchAggregation aggregation) {
		return aggregation(aggregation, null);
	}

	public SearchQueryBuilder aggregation(SearchAggregation aggregation, String value) {
		if (aggregation == null)
			return this;
		this.aggregations.add(aggregation);
		if (value == null)
			return this;
		filter(aggregation.name, value);
		return this;
	}

	public SearchQueryBuilder filter(String field, String value) {
		if (field == null || value == null)
			return this;
		SearchParameter values = this.filters.get(field);
		if (values == null)
			this.filters.put(field, values = new SearchParameter(field, Conjunction.OR));
		values.add(new SearchParameterValue(value, Type.PHRASE));
		return this;
	}

	public SearchQueryBuilder sortBy(String field, SearchSorting order) {
		if (field == null || order == null)
			return null;
		this.sortBy.put(field, order);
		return this;
	}

	public SearchQuery build() {
		return build(Conjunction.AND);
	}

	public SearchQuery build(Conjunction queryConjunctionType) {
		SearchQuery searchQuery = new SearchQuery(aggregations);
		if (page >= 0) {
			searchQuery.setPage(page);
			searchQuery.setPageSize(pageSize);
		}
		if (query != null) {
			searchQuery.addParameter("_all", split(query), queryConjunctionType);
			searchQuery.setQuery(query);
		}
		searchQuery.setFilters(new ArrayList<>(filters.values()));
		searchQuery.setSortBy(sortBy);
		return searchQuery;
	}

	private static Set<SearchParameterValue> split(String query) {
		Set<SearchParameterValue> splitted = new HashSet<>();
		StringTokenizer splitter = new StringTokenizer(query, "\"", true);
		boolean escaped = false;
		while (splitter.hasMoreTokens()) {
			String token = splitter.nextToken();
			if ("\"".equals(token))
				escaped = !escaped;
			else if (escaped)
				splitted.add(new SearchParameterValue(token, Type.PHRASE));
			else {
				token = token.replace("@", " ");
				for (String word : token.trim().split("\\s+"))
					if (word.contains("-"))
						splitted.add(new SearchParameterValue(word, Type.PHRASE));
					else
						splitted.add(new SearchParameterValue(word, Type.WILDCART));
			}
		}
		return splitted;
	}

}
