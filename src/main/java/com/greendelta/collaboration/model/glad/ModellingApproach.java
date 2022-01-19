package com.greendelta.collaboration.model.glad;

import java.util.Map;

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

}