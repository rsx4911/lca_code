package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum ModellingApproach {

	PHYSICAL, ECONOMIC, CAUSAL, NONE, UNKNOWN;

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