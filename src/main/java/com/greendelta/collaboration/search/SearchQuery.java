package com.greendelta.collaboration.search;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.sort.SortOrder;

public interface SearchQuery<P extends SearchParameter> {

	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int MAX_PAGE_SIZE = 100;

	public List<P> getParameters();

	public List<P> getFilters();

	public Map<String, SortOrder> getSortBy();

	public int getPage();

	public int getPageSize();
}
