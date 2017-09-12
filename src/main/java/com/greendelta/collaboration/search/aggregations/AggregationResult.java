package com.greendelta.collaboration.search.aggregations;

import java.util.List;
import java.util.Map;

public class AggregationResult {

	public final String name;
	public final String type;
	public final long totalCount;
	public final Map<String, String> data;
	public final List<AggregationResultEntry> entries;

	AggregationResult(String name, String type, long totalCount, Map<String, String> data, List<AggregationResultEntry> entries) {
		this.name = name;
		this.type = type;
		this.totalCount = totalCount;
		this.data = data;
		this.entries = entries;
	}

}
