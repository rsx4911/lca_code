package com.greendelta.collaboration.util;

import java.util.Arrays;
import java.util.List;

import org.openlca.core.model.ModelType;

public class ModelTypes {

	public static final List<String> DEFAULT_ORDER = Arrays.asList(new String[] {
			ModelType.PROJECT.name(),
			ModelType.PRODUCT_SYSTEM.name(),
			ModelType.PROCESS.name(),
			ModelType.IMPACT_METHOD.name(),
			ModelType.IMPACT_CATEGORY.name(),
			ModelType.FLOW.name(),
			ModelType.EPD.name(),
			ModelType.RESULT.name(),
			ModelType.SOCIAL_INDICATOR.name(),
			ModelType.PARAMETER.name(),
			ModelType.DQ_SYSTEM.name(),
			ModelType.FLOW_PROPERTY.name(),
			ModelType.UNIT_GROUP.name(),
			ModelType.CURRENCY.name(),
			ModelType.ACTOR.name(),
			ModelType.SOURCE.name(),
			ModelType.LOCATION.name()
	});

}
