package com.greendelta.collaboration.util;

import java.util.Comparator;
import java.util.Map;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

public class ModelTypes {

	public static final ModelType[] SORTED = { ModelType.PROJECT, ModelType.PRODUCT_SYSTEM, ModelType.PROCESS,
			ModelType.IMPACT_METHOD, ModelType.FLOW, ModelType.SOCIAL_INDICATOR, ModelType.PARAMETER,
			ModelType.DQ_SYSTEM, ModelType.FLOW_PROPERTY, ModelType.UNIT_GROUP, ModelType.CURRENCY,
			ModelType.ACTOR, ModelType.SOURCE, ModelType.LOCATION };

	public static int getOrdinal(ModelType type, ModelType categoryType) {
		if (categoryType != null)
			return SORTED.length - getIndex(categoryType);
		return (SORTED.length * 2) - getIndex(type);
	}

	private static int getIndex(ModelType type) {
		for (int i = 0; i < SORTED.length; i++)
			if (SORTED[i] == type)
				return i;
		return Integer.MAX_VALUE;
	}

	public static Comparator<ModelType> getComparator() {
		return new TypeComparator();
	}

	public static int compare(ModelType t1, ModelType t2) {
		return Integer.compare(getIndex(t1), getIndex(t2));
	}

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
			if (value == com.greendelta.collaboration.model.glad.ProcessType.SYSTEM)
				return ProcessType.LCI_RESULT;
			return ProcessType.UNIT_PROCESS;
		}
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		if (sValue.toLowerCase().equals("system"))
			return ProcessType.LCI_RESULT;
		if (sValue.toLowerCase().equals("unit") || sValue.toLowerCase().equals("unknown"))
			return ProcessType.UNIT_PROCESS;
		return ProcessType.valueOf(sValue.toUpperCase());
	}

	private static class TypeComparator implements Comparator<ModelType> {

		@Override
		public int compare(ModelType t1, ModelType t2) {
			return compare(t1, t2);
		}

	}

}
