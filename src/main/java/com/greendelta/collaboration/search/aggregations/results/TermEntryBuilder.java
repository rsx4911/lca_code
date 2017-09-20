package com.greendelta.collaboration.search.aggregations.results;

import java.util.HashMap;

public class TermEntryBuilder {

	private String key;
	private long count;
	
	public TermEntryBuilder key(String key) {
		this.key = key;
		return this;
	}

	public TermEntryBuilder count(long count) {
		this.count = count;
		return this;
	}
	
	public AggregationResultEntry build() {
		return new AggregationResultEntry(key, count, new HashMap<>());
	}
	
}
