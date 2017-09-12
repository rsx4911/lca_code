package com.greendelta.collaboration.search.aggregations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationResultBuilder {

	private String name;
	private String type;
	private long totalCount;
	private Map<String, String> data = new HashMap<>();
	private List<AggregationResultEntry> entries = new ArrayList<>();
	
	public AggregationResultBuilder name(String name) {
		this.name = name;
		return this;
	}

	public AggregationResultBuilder type(String type) {
		this.type = type;
		return this;
	}
	
	public AggregationResultBuilder totalCount(long totalCount) {
		this.totalCount = totalCount;
		return this;
	}
	
	public AggregationResultBuilder putData(String key, Object value) {
		this.data.put(key, value == null ? null : value.toString());
		return this;
	}
	
	public AggregationResultBuilder addEntry(AggregationResultEntry entry) {
		this.entries.add(entry);
		return this;
	}
	
	public AggregationResult build() {
		return new AggregationResult(name, type, totalCount, data, entries);
	}

}
