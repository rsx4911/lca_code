package com.greendelta.collaboration.model.index;

import java.util.Map;

public enum IndexAction {

	ADD, UPDATE, DELETE;
	
	public static IndexAction from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("action");
		if (value == null || value.toString().isEmpty())
			return null;
		return valueOf(value.toString().toUpperCase());
	}

}
