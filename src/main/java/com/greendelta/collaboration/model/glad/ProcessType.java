package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum ProcessType {

	UNIT, FULLY_AGGREGATED, UNKNOWN;

	public static ProcessType from(Map<String, Object> map) {
		if (map == null)
			return null;
		var value = map.get("processType");
		if (value == null)
			return null;
		if (value instanceof ProcessType type)
			return type;
		var sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

}