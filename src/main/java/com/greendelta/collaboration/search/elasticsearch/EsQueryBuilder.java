package com.greendelta.collaboration.search.elasticsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.elasticsearch.search.sort.SortOrder;

import com.greendelta.collaboration.search.SearchParameter.Conjunction;
import com.greendelta.collaboration.search.SearchParameterValue;
import com.greendelta.collaboration.search.SearchParameterValue.Type;

class EsQueryBuilder {

	private String query;
	private Map<String, EsParameter> filters = new HashMap<>();
	private int page = -1;
	private int pageSize = EsQuery.DEFAULT_PAGE_SIZE;
	private Set<EsAggregation> aggregations = new HashSet<>();
	private Map<String, SortOrder> sortBy = new HashMap<>();

	EsQueryBuilder query(String query) {
		this.query = query;
		return this;
	}

	EsQueryBuilder page(int page) {
		this.page = page;
		return this;
	}

	EsQueryBuilder pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	EsQueryBuilder facets(Set<EsAggregation> aggregations) {
		this.aggregations = aggregations;
		return this;
	}

	EsQueryBuilder filter(String field, String value) {
		return filter(field, Collections.singleton(value));
	}

	EsQueryBuilder filter(String field, Set<String> values) {
		return filter(Collections.singletonMap(field, values));
	}

	EsQueryBuilder filter(Map<String, Set<String>> filters) {
		for (String key : filters.keySet()) {
			EsParameter values = this.filters.get(key);
			if (values == null)
				this.filters.put(key, values = new EsParameter(key, Conjunction.OR));
			Set<String> stringValues = filters.get(key);
			for (String value : stringValues)
				values.add(new SearchParameterValue(value, Type.PHRASE));
		}
		return this;
	}

	EsQueryBuilder sortBy(String field, SortOrder order) {
		this.sortBy.put(field, order);
		return this;
	}

	EsQuery build() {
		return build(Conjunction.AND);
	}

	EsQuery build(Conjunction queryConjunctionType) {
		EsQuery searchQuery = new EsQuery(aggregations);
		if (page >= 0) {
			searchQuery.setPage(page);
			searchQuery.setPageSize(pageSize);
		}
		if (query != null)
			searchQuery.addParameter("_all", split(query), queryConjunctionType);
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
