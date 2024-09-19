package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.openlca.core.model.ModelType;

import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.TermsAggregation;

public class Aggregations {

	public static final TermsAggregation GROUP = new TermsAggregation("group", "versions.repos.group");
	public static final TermsAggregation REPOSITORY = new TermsAggregation("repositoryId", "versions.repos.path");
	public static final TermsAggregation MODEL_TYPE = new TermsAggregation("type", "type");
	public static final TermsAggregation REPOSITORY_TAGS = new TermsAggregation("repositoryTags",
			"versions.repos.tags");
	public static final TermsAggregation DATASET_TAGS = new TermsAggregation("tags", "versions.tags");
	// process specific aggregations
	public static final TermsAggregation PROCESS_TYPE = new TermsAggregation("processType", "versions.processType");
	public static final TermsAggregation MODELLING_APPROACH = new TermsAggregation("modellingApproach",
			"versions.modellingApproach");
	public static final TermsAggregation START_YEAR = new TermsAggregation("validFromYear", "versions.validFromYear");
	public static final TermsAggregation END_YEAR = new TermsAggregation("validUntilYear", "versions.validUntilYear");
	public static final TermsAggregation LOCATION = new TermsAggregation("location", "versions.location");
	public static final TermsAggregation CONTACT = new TermsAggregation("contact", "versions.contact");
	public static final TermsAggregation CATEGORY = new TermsAggregation("categoryPaths", "versions.categoryPaths");
	public static final TermsAggregation REVIEW_TYPE = new TermsAggregation("reviewType", "versions.reviewTypes");
	public static final TermsAggregation COMPLIANCE_DECLARATION = new TermsAggregation("complianceDeclaration", "versions.complianceDeclarations");
	public static final TermsAggregation FLOW_COMPLETENESS = new TermsAggregation("flowCompleteness", "versions.flowCompleteness");
	// flow specific aggregations
	public static final TermsAggregation FLOW_TYPE = new TermsAggregation("flowType", "versions.flowType");

	public static final SearchAggregation[] DEFAULT_FILTERS = new SearchAggregation[] {
			GROUP, REPOSITORY, MODEL_TYPE, REPOSITORY_TAGS, DATASET_TAGS };
	public static final SearchAggregation[] PROCESS_FILTERS = new SearchAggregation[] {
			PROCESS_TYPE, MODELLING_APPROACH, LOCATION, START_YEAR, END_YEAR, CONTACT, REVIEW_TYPE, COMPLIANCE_DECLARATION, FLOW_COMPLETENESS };
	public static final SearchAggregation[] EPD_FILTERS = new SearchAggregation[] {
			START_YEAR, END_YEAR };
	public static final SearchAggregation[] FLOW_FILTERS = new SearchAggregation[] {
			FLOW_TYPE };

	public static final SearchAggregation[] getFilters(Set<ModelType> modelTypes) {
		var modelType = modelTypes != null && modelTypes.size() == 1 ? modelTypes.iterator().next() : null;
		var filters = new ArrayList<>(Arrays.asList(DEFAULT_FILTERS));
		if (modelType == ModelType.PROCESS) {
			filters.addAll(Arrays.asList(PROCESS_FILTERS));
		} else if (modelType == ModelType.EPD) {
			filters.addAll(Arrays.asList(EPD_FILTERS));
		} else if (modelType == ModelType.FLOW) {
			filters.addAll(Arrays.asList(FLOW_FILTERS));
		}
		if (modelType != null) {
			filters.add(CATEGORY);
		}
		return filters.toArray(new SearchAggregation[filters.size()]);
	}

}
