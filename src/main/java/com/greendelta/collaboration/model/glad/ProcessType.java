package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum ProcessType {

	UNIT, SYSTEM, UNKNOWN;

	public static ProcessType from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("processType");
		if (value == null)
			return null;
		if (value instanceof ProcessType)
			return (ProcessType) value;
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

}