package com.greendelta.collaboration.search.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

/**
 * Allows setting up queries for returning the facet and with facet-specific
 * filtering.
 * 
 */
interface EsAggregation {

	String getName();
	
	String getType();
	
	QueryBuilder getQuery(String value);

	AbstractAggregationBuilder<?> getAggregation();

}
