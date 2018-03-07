package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.core.model.ModelType;

import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.TermsAggregation;

public class Aggregations {

	public static final TermsAggregation GROUP = new TermsAggregation("group");
	public static final TermsAggregation REPOSITORY = new TermsAggregation("repositoryId");
	public static final TermsAggregation MODEL_TYPE = new TermsAggregation("type");
	// process specific aggregations
	public static final TermsAggregation PROCESS_TYPE = new TermsAggregation("processType");
	public static final TermsAggregation MODELLING_APPROACH = new TermsAggregation("modellingApproach");
	public static final TermsAggregation START_YEAR = new TermsAggregation("validFromYear");
	public static final TermsAggregation END_YEAR = new TermsAggregation("validUntilYear");
	public static final TermsAggregation LOCATION = new TermsAggregation("location");
	public static final TermsAggregation CONTACT = new TermsAggregation("contact");
	public static final TermsAggregation CATEGORY = new TermsAggregation("categoryPaths");
	// flow specific aggregations
	public static final TermsAggregation FLOW_TYPE = new TermsAggregation("flowType");

	public static final SearchAggregation[] DEFAULT_FILTERS = new SearchAggregation[] {
			GROUP, REPOSITORY, MODEL_TYPE};
	public static final SearchAggregation[] PROCESS_FILTERS = new SearchAggregation[] {
			PROCESS_TYPE, MODELLING_APPROACH, LOCATION, START_YEAR, END_YEAR, CONTACT };
	public static final SearchAggregation[] FLOW_FILTERS = new SearchAggregation[] {
			FLOW_TYPE };

	public static final SearchAggregation[] getFilters(ModelType modelType) {
		List<SearchAggregation> filters = new ArrayList<>(Arrays.asList(DEFAULT_FILTERS));
		if (modelType == ModelType.PROCESS) {
			filters.addAll(Arrays.asList(PROCESS_FILTERS));
		} else if (modelType == ModelType.FLOW) {
			filters.addAll(Arrays.asList(FLOW_FILTERS));
		}
		if (modelType != null) {
			filters.add(CATEGORY);
		}
		return filters.toArray(new SearchAggregation[filters.size()]);
	}

}
