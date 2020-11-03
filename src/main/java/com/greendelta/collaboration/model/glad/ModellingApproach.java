package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum ModellingApproach {

	PHYSICAL, ECONOMIC, CAUSAL, SYSTEM_EXPANSION, NONE, UNKNOWN, NOT_APPLICABLE;

	public static ModellingApproach from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("modellingApproach");
		if (value == null)
			return null;
		if (value instanceof ModellingApproach)
			return (ModellingApproach) value;
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

}