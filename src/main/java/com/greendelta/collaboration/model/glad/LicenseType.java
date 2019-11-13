package com.greendelta.collaboration.model.glad;

import java.util.Map;

public enum LicenseType {

	FREE, MIXED, CHARGED, UNKNOWN;

	public static LicenseType from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("licenseType");
		if (value == null)
			return null;
		if (value instanceof LicenseType)
			return (LicenseType) value;
		String sValue = value.toString();
		if (sValue.isEmpty())
			return null;
		return valueOf(sValue.toUpperCase());
	}

}