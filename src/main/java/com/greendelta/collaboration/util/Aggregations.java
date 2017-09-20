package com.greendelta.collaboration.util;

import com.greendelta.collaboration.search.aggregations.SearchAggregation;
import com.greendelta.collaboration.search.aggregations.TermsAggregation;

public interface Aggregations {

	TermsAggregation REPOSITORY = new TermsAggregation("Repository", "repositoryId");
	TermsAggregation MODEL_TYPE = new TermsAggregation("Model type", "type");
	TermsAggregation CATEGORY_TYPE = new TermsAggregation("Category type", "categoryType");
	TermsAggregation CATEGORY = new TermsAggregation("Category", "categoryRefId");
	TermsAggregation REF_ID = new TermsAggregation("Ref id", "refId");

	SearchAggregation[] ALL = new SearchAggregation[] { REPOSITORY, MODEL_TYPE, CATEGORY_TYPE, CATEGORY, REF_ID };

}
