package com.greendelta.collaboration.util;

import java.util.Comparator;
import java.util.Map;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;

public class ModelTypes {

	public static final ModelType[] SORTED = { ModelType.PROJECT, ModelType.PRODUCT_SYSTEM, ModelType.PROCESS,
			ModelType.FLOW, ModelType.IMPACT_METHOD, ModelType.SOCIAL_INDICATOR, ModelType.PARAMETER,
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

	public static org.openlca.core.model.ProcessType processType(ProcessType internal) {
		switch (internal) {
		case SYSTEM:
			return org.openlca.core.model.ProcessType.LCI_RESULT;
		case UNIT:
			return org.openlca.core.model.ProcessType.UNIT_PROCESS;
		default:
			return null;
		}
	}

	public static org.openlca.core.model.ProcessType processType(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("processType");
		if (value == null)
			return null;
		if (value instanceof org.openlca.core.model.ProcessType)
			return (org.openlca.core.model.ProcessType) value;
		if (value instanceof ProcessType)
			return processType((ProcessType) value);
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return org.openlca.core.model.ProcessType.valueOf(sValue.toUpperCase());
	}

	private static class TypeComparator implements Comparator<ModelType> {

		@Override
		public int compare(ModelType t1, ModelType t2) {
			return compare(t1, t2);
		}

	}

}
