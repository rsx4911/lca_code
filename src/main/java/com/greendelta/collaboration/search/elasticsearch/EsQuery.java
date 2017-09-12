package com.greendelta.collaboration.search.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.search.sort.SortOrder;

import com.greendelta.collaboration.search.SearchParameter.Conjunction;
import com.greendelta.collaboration.search.SearchParameterValue;
import com.greendelta.collaboration.search.SearchQuery;

class EsQuery implements SearchQuery<EsParameter> {

	private final List<EsParameter> parameters = new ArrayList<>();
	private final List<EsParameter> filters = new ArrayList<>();
	private final Map<String, SortOrder> sortBy = new HashMap<>();
	private final Set<EsAggregation> aggregations;
	private int page;
	private int pageSize;

	EsQuery(Set<EsAggregation> aggregations) {
		if (aggregations != null)
			this.aggregations = aggregations;
		else
			this.aggregations = new HashSet<>();
	}

	Set<EsAggregation> getAggregations() {
		return aggregations;
	}

	void addParameter(String name, Set<SearchParameterValue> values, Conjunction type) {
		EsParameter parameter = null;
		for (EsParameter param : parameters) {
			if (param.name.equals(name)) {
				parameter = param;
				break;
			}
		}
		if (parameter == null)
			parameters.add(parameter = new EsParameter(name, type));
		parameter.addAll(values);
	}

	void setFilters(List<EsParameter> filters) {
		this.filters.clear();
		this.filters.addAll(filters);
	}

	void setSortBy(Map<String, SortOrder> sortBy) {
		this.sortBy.clear();
		this.sortBy.putAll(sortBy);
	}

	public List<EsParameter> getParameters() {
		return parameters;
	}

	public List<EsParameter> getFilters() {
		return filters;
	}

	public Map<String, SortOrder> getSortBy() {
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

}
