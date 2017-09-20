package com.greendelta.collaboration.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.greendelta.collaboration.search.SearchParameter.Conjunction;
import com.greendelta.collaboration.search.aggregations.SearchAggregation;

public class SearchQuery {

	public final static int DEFAULT_PAGE_SIZE = 10;
	private final Set<SearchAggregation> aggregations;
	private final List<SearchParameter> parameters = new ArrayList<>();
	private final List<SearchParameter> filters = new ArrayList<>();
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

	void addParameter(String name, Set<SearchParameterValue> values, Conjunction type) {
		SearchParameter parameter = null;
		for (SearchParameter param : parameters) {
			if (param.name.equals(name)) {
				parameter = param;
				break;
			}
		}
		if (parameter == null)
			parameters.add(parameter = new SearchParameter(name, type));
		parameter.addAll(values);
	}

	void setFilters(List<SearchParameter> filters) {
		this.filters.clear();
		this.filters.addAll(filters);
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

	public List<SearchParameter> getParameters() {
		return parameters;
	}

	public List<SearchParameter> getFilters() {
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
		s += "aggregations=" + joinAggregations() + ", ";
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

	private String joinAggregations() {
		String s = "[";
		int i = 0;
		for (SearchParameter value : filters) {
			s += value.name + "=" + join(value.values);
			i++;
			if (i < filters.size()) {
				s += ", ";
			}
		}
		return s + "]";
	}

	private String join(Set<SearchParameterValue> list) {
		if (list.isEmpty())
			return "";
		if (list.size() == 1)
			return list.iterator().next().value;
		String s = "[";
		int i = 0;
		for (SearchParameterValue value : list) {
			s += value.value;
			i++;
			if (i < list.size()) {
				s += ", ";
			}
		}
		return s + "]";
	}
}
