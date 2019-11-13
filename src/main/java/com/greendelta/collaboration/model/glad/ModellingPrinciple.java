package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum ModellingPrinciple {

	ATTRIBUTIONAL, CONSEQUENTIAL, UNKNOWN;

	public static ModellingPrinciple from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("modellingPrinciple");
		if (value == null)
			return null;
		if (value instanceof ModellingPrinciple)
			return (ModellingPrinciple) value;
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

}