package com.greendelta.collaboration.search.aggregations;

public abstract class SearchAggregation {
	
	public final String name;
	public final String type;
	public final String field;

	protected SearchAggregation(String name, String type, String field) {
		this.name = name;
		this.type = type;
		this.field = field;
	}
	
}
