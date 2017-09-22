package com.greendelta.collaboration.search.aggregations;

public class TermsAggregation extends SearchAggregation {

	public final static String TYPE = "terms";
	
	public TermsAggregation(String name, String field) {
		super(name, TYPE, field);
	}

}
