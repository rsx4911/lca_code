package com.greendelta.collaboration.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.greendelta.collaboration.search.SearchFilter.Conjunction;
import com.greendelta.collaboration.search.aggregations.SearchAggregation;

public class SearchQuery {

	public final static int DEFAULT_PAGE_SIZE = 10;
	private final Set<SearchAggregation> aggregations;
	private final List<SearchFilter> filters = new ArrayList<>();
	private final Map<String, SearchSorting> sortBy = new HashMap<>();
	private String query;
	private int page;
	private int pageSize;

	SearchQuery(Set<SearchAggregation> aggregations) {
		if (aggregations != null)
			this.aggregations = aggregations;
		else
			this.aggregations = new HashSet<>();
	}

	void addFilter(String name, Set<SearchFilterValue> values, Conjunction type) {
		SearchFilter parameter = null;
		for (SearchFilter param : filters) {
			if (param.name.equals(name)) {
				parameter = param;
				break;
			}
		}
		if (parameter == null)
			filters.add(parameter = new SearchFilter(name, type));
		parameter.addAll(values);
	}

	void setSortBy(Map<String, SearchSorting> sortBy) {
		this.sortBy.clear();
		this.sortBy.putAll(sortBy);
	}

	void setQuery(String query) {
		this.query = query;
	}

	public Set<SearchAggregation> getAggregations() {
		return aggregations;
	}

	public SearchAggregation getAggregation(String name) {
		for (SearchAggregation aggregation : aggregations)
			if (aggregation.name.equals(name))
				return aggregation;
		return null;
	}

	public boolean hasAggregation(String name) {
		return getAggregation(name) != null;
	}

	public List<SearchFilter> getFilters() {
		return filters;
	}

	public Map<String, SearchSorting> getSortBy() {
		return sortBy;
	}

	public int getPage() {
		return page;
	}

	void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		String s = "{page=" + page + ", ";
		s += "pageSize=" + pageSize + ", ";
		s += "query=" + (query != null ? query : "");
		s += "aggregations=" + joinFilters(true) + ", ";
		s += "fitlers=" + joinFilters(false) + ", ";
		return s + "sortBy=" + joinSortBy() + "}";
	}

	private String joinSortBy() {
		String s = "[";
		int i = 0;
		for (Entry<String, SearchSorting> entry : sortBy.entrySet()) {
			s += entry.getKey() + "=" + entry.getValue();
			i++;
			if (i < sortBy.size()) {
				s += ", ";
			}
		}
		return s + "]";
	}

	private String joinFilters(boolean aggregations) {
		String s = "[";
		int i = 0;
		for (SearchFilter value : filters) {
			if (hasAggregation(value.name) != aggregations)
				continue;
			s += value.name + "=" + join(value.values);
			i++;
			if (i < filters.size()) {
				s += ", ";
			}
		}
		return s + "]";
	}

	private String join(Set<SearchFilterValue> list) {
		if (list.isEmpty())
			return "";
		if (list.size() == 1)
			return list.iterator().next().value;
		String s = "[";
		int i = 0;
		for (SearchFilterValue value : list) {
			s += value.value;
			i++;
			if (i < list.size()) {
				s += ", ";
			}
		}
		return s + "]";
	}
}
