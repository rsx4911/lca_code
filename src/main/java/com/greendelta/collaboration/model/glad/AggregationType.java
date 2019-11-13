package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum AggregationType {

	HORIZONTAL, VERTICAL, NONE, UNKNOWN;

	public static AggregationType from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("aggregationType");
		if (value == null)
			return null;
		if (value instanceof AggregationType)
			return (AggregationType) value;
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

}