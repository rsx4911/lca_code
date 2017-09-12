package com.greendelta.collaboration.search.aggregations;

import java.util.Map;

public class AggregationResultEntry {

	public final String key;
	public final long count;
	public final Map<String, String> data;
	
	AggregationResultEntry(String key, long count, Map<String, String> data) {
		this.key = key;
		this.count = count;
		this.data = data;
	}
	
}
