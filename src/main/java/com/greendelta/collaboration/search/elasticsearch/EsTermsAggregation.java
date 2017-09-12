package com.greendelta.collaboration.search.elasticsearch;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

class EsTermsAggregation implements EsAggregation {

	private static final String TYPE = "terms";
	private final String searchField;
	private final String displayField;
	private final String name;

	EsTermsAggregation(String name, String field) {
		this(name, field, field);
	}

	EsTermsAggregation(String name, String searchField, String displayField) {
		this.name = name;
		this.searchField = searchField;
		this.displayField = displayField;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public TermQueryBuilder getQuery(String value) {
		return QueryBuilders.termQuery(searchField, value.toLowerCase());
	}

	@Override
	public TermsAggregationBuilder getAggregation() {
		return AggregationBuilders.terms(getName()).field(displayField).size(3000);
	}

}
