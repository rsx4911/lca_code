package com.greendelta.collaboration.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

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

	public static ModelType from(Map<String, Object> map) {
		return from(map, "type");
	}

	public static ModelType from(Map<String, Object> map, String field) {
		if (map == null)
			return null;
		var value = map.get(field);
		if (value == null)
			return null;
		if (value instanceof ModelType type)
			return type;
		return parse(value.toString());
	}

	public static ModelType parse(String value) {
		if (value.isEmpty())
			return null;
		for (ModelType type : ModelType.values())
			if (type.name().equals(value.toUpperCase()))
				return type;
		return null;
	}

	public static FlowType flowType(Map<String, Object> map) {
		if (map == null)
			return null;
		var value = map.get("flowType");
		if (value == null)
			return null;
		if (value instanceof FlowType type)
			return type;
		var sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return FlowType.valueOf(sValue.toUpperCase());
	}

	public static ProcessType processType(Map<String, Object> map) {
		if (map == null)
			return null;
		var value = map.get("processType");
		if (value == null)
			return null;
		if (value instanceof ProcessType type)
			return type;
		if (value instanceof com.greendelta.collaboration.model.glad.ProcessType) {
			if (value == com.greendelta.collaboration.model.glad.ProcessType.FULLY_AGGREGATED)
				return ProcessType.LCI_RESULT;
			return ProcessType.UNIT_PROCESS;
		}
		var sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		if (sValue.toLowerCase().equals("system") || sValue.toLowerCase().equals("fully_aggregated")
				|| sValue.toLowerCase().equals("lci_result"))
			return ProcessType.LCI_RESULT;
		if (sValue.toLowerCase().equals("unit") || sValue.toLowerCase().equals("unit_process")
				|| sValue.toLowerCase().equals("unknown"))
			return ProcessType.UNIT_PROCESS;
		return ProcessType.valueOf(sValue.toUpperCase());
	}

}
