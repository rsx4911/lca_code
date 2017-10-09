package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.core.model.ModelType;

import com.greendelta.lca.search.aggregations.SearchAggregation;
import com.greendelta.lca.search.aggregations.TermsAggregation;

public class Aggregations {

	public static final TermsAggregation REPOSITORY = new TermsAggregation("repositoryId");
	public static final TermsAggregation MODEL_TYPE = new TermsAggregation("type");
	public static final TermsAggregation CATEGORY_TYPE = new TermsAggregation("categoryType");
	public static final TermsAggregation CATEGORY = new TermsAggregation("categoryRefId");
	public static final TermsAggregation REF_ID = new TermsAggregation("refId");
	public static final TermsAggregation COMMIT_ID = new TermsAggregation("commitId");
	// process specific aggregations
	public static final TermsAggregation PROCESS_TYPE = new TermsAggregation("processType");
	public static final TermsAggregation MODELLING_APPROACH = new TermsAggregation("modellingApproach");
	
	public static final SearchAggregation[] DEFAULT_FILTERS = new SearchAggregation[] { REPOSITORY, MODEL_TYPE };
	public static final SearchAggregation[] PROCESS_FILTERS = new SearchAggregation[] { PROCESS_TYPE, MODELLING_APPROACH};
	
	public static final SearchAggregation[] getFilters(ModelType modelType) {
		List<SearchAggregation> filters = new ArrayList<>(Arrays.asList(DEFAULT_FILTERS));
		if (modelType == ModelType.PROCESS) {
			filters.addAll(Arrays.asList(PROCESS_FILTERS));
		}
		return filters.toArray(new SearchAggregation[filters.size()]);
 	}

}
