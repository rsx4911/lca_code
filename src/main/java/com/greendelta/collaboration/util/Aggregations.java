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
	// process specific aggregations
	public static final TermsAggregation PROCESS_TYPE = new TermsAggregation("processType");
	public static final TermsAggregation MODELLING_APPROACH = new TermsAggregation("modellingApproach");
	// flow specific aggregations
	public static final TermsAggregation FLOW_TYPE = new TermsAggregation("flowType");

	public static final SearchAggregation[] DEFAULT_FILTERS = new SearchAggregation[] { REPOSITORY, MODEL_TYPE };
	public static final SearchAggregation[] PROCESS_FILTERS = new SearchAggregation[] { PROCESS_TYPE,
			MODELLING_APPROACH };
	public static final SearchAggregation[] FLOW_FILTERS = new SearchAggregation[] { FLOW_TYPE };

	public static final SearchAggregation[] getFilters(ModelType modelType) {
		List<SearchAggregation> filters = new ArrayList<>(Arrays.asList(DEFAULT_FILTERS));
		if (modelType == ModelType.PROCESS) {
			filters.addAll(Arrays.asList(PROCESS_FILTERS));
		} else if (modelType == ModelType.FLOW) {
			filters.addAll(Arrays.asList(FLOW_FILTERS));
		}
		return filters.toArray(new SearchAggregation[filters.size()]);
	}

}
