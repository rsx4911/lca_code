package com.greendelta.collaboration.util;

import java.util.Map;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

public class ModelTypes {

	public static ModelType from(Map<String, Object> map, String field) {
		if (map == null)
			return null;
		Object value = map.get(field);
		if (value == null)
			return null;
		if (value instanceof ModelType)
			return (ModelType) value;
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
		Object value = map.get("flowType");
		if (value == null)
			return null;
		if (value instanceof FlowType)
			return (FlowType) value;
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return FlowType.valueOf(sValue.toUpperCase());
	}

	public static ProcessType processType(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("processType");
		if (value == null)
			return null;
		if (value instanceof ProcessType)
			return (ProcessType) value;
		if (value instanceof com.greendelta.collaboration.model.glad.ProcessType) {
			if (value == com.greendelta.collaboration.model.glad.ProcessType.FULLY_AGGREGATED)
				return ProcessType.LCI_RESULT;
			return ProcessType.UNIT_PROCESS;
		}
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		if (sValue.toLowerCase().equals("system") || sValue.toLowerCase().equals("fully_aggregated") || sValue.toLowerCase().equals("lci_result"))
			return ProcessType.LCI_RESULT;
		if (sValue.toLowerCase().equals("unit") || sValue.toLowerCase().equals("unit_process") || sValue.toLowerCase().equals("unknown"))
			return ProcessType.UNIT_PROCESS;
		return ProcessType.valueOf(sValue.toUpperCase());
	}

}
