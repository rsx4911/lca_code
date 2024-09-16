package com.greendelta.collaboration.search;

import java.util.Map;

import org.openlca.core.model.AllocationMethod;
import org.openlca.jsonld.Enums;

public enum ModellingApproach {

	PHYSICAL, ECONOMIC, CAUSAL, SYSTEM_EXPANSION, NONE, UNKNOWN, NOT_APPLICABLE;

	public static ModellingApproach from(Map<String, Object> map) {
		if (map == null)
			return null;
		var value = map.get("modellingApproach");
		if (value == null)
			return null;
		if (value instanceof ModellingApproach approach)
			return approach;
		var sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

	public static ModellingApproach fromDefaultAllocationMethod(Map<String, Object> map) {
		if (map == null)
			return null;
		var value = Maps.getString(map, "defaultAllocationMethod");
		if (value == null)
			return ModellingApproach.UNKNOWN;
		if (value.equals(Enums.getLabel(AllocationMethod.PHYSICAL)))
			return ModellingApproach.PHYSICAL;
		if (value.equals(Enums.getLabel(AllocationMethod.ECONOMIC)))
			return ModellingApproach.ECONOMIC;
		if (value.equals(Enums.getLabel(AllocationMethod.CAUSAL)))
			return ModellingApproach.CAUSAL;
		if (value.equals(Enums.getLabel(AllocationMethod.NONE)))
			return ModellingApproach.NONE;
		return ModellingApproach.UNKNOWN;
	}

}