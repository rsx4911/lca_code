package com.greendelta.collaboration.search;

import java.util.List;
import java.util.Map;

import com.greendelta.collaboration.search.aggregations.AggregationResult;

public interface SearchResult {

	public List<Map<String, Object>> getData();

	public List<AggregationResult> getAggregations();

	public ResultInfo getResultInfo();

	public SearchQuery<?> getOriginalQuery();

	public class ResultInfo {

		public long pageSize;
		public long totalCount;
		public int currentPage;
		public int pageCount;
		public long searchMillis;

	}

}
