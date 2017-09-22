package com.greendelta.collaboration.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.greendelta.collaboration.search.SearchFilter.Conjunction;
import com.greendelta.collaboration.search.SearchFilterValue.Type;
import com.greendelta.collaboration.search.aggregations.SearchAggregation;

public class SearchQueryBuilder {

	private String query;
	private String[] queryFields;
	private int page = 0;
	private int pageSize = SearchQuery.DEFAULT_PAGE_SIZE;
	private Map<String, SearchFilter> filters = new HashMap<>();
	private Set<SearchAggregation> aggregations = new HashSet<>();
	private Map<String, SearchSorting> sortBy = new HashMap<>();

	public SearchQueryBuilder query(String query, String queryField) {
		return query(query, new String[] { queryField });
	}

	public SearchQueryBuilder query(String query, String[] queryFields) {
		if (queryFields == null || queryFields.length == 0)
			return this;
		this.query = query;
		this.queryFields = queryFields;
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
		if (!hasAggregation(aggregation.name)) {
			this.aggregations.add(aggregation);
		}
		if (value == null)
			return this;
		filter(aggregation.name, value, Type.PHRASE);
		return this;
	}

	private boolean hasAggregation(String name) {
		for (SearchAggregation aggregation : aggregations)
			if (aggregation.name.equals(name))
				return true;
		return false;
	}

	public SearchQueryBuilder filter(String field, String value, Type type) {
		if (field == null || value == null)
			return this;
		SearchFilter filter = this.filters.get(field);
		SearchFilterValue filterValue = new SearchFilterValue(value, type);
		if (filter == null) {
			this.filters.put(field, filter = new SearchFilter(field, filterValue));
		} else {
			filter.values.add(filterValue);
		}
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
			for (String field : queryFields) {
				searchQuery.addFilter(field, split(query), queryConjunctionType);
			}
			searchQuery.setQuery(query);
		}
		for (SearchFilter filter : filters.values()) {
			searchQuery.addFilter(filter.field, filter.values, filter.conjunction);
		}
		searchQuery.setSortBy(sortBy);
		return searchQuery;
	}

	private static Set<SearchFilterValue> split(String query) {
		Set<SearchFilterValue> splitted = new HashSet<>();
		StringTokenizer splitter = new StringTokenizer(query, "\"", true);
		boolean escaped = false;
		while (splitter.hasMoreTokens()) {
			String token = splitter.nextToken();
			if ("\"".equals(token)) {
				escaped = !escaped;
			} else if (escaped) {
				splitted.add(new SearchFilterValue(token, Type.PHRASE));
			} else {
				token = token.replace("@", " ");
				for (String word : token.trim().split("\\s+")) {
					splitted.add(new SearchFilterValue(word, Type.PHRASE));
				}
			}
		}
		return splitted;
	}

}
