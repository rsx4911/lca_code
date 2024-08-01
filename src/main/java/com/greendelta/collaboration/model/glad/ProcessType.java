package com.greendelta.collaboration.model.glad;

import java.util.Map;

import org.openlca.jsonld.Enums;

import com.greendelta.collaboration.util.Maps;

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

	public static ProcessType fromProcessType(Map<String, Object> map) {
		var value = Maps.getString(map, "processType");
		if (value == null)
			return ProcessType.UNKNOWN;
		if (value.equals(Enums.getLabel(org.openlca.core.model.ProcessType.LCI_RESULT)))
			return ProcessType.FULLY_AGGREGATED;
		if (value.equals(Enums.getLabel(org.openlca.core.model.ProcessType.UNIT_PROCESS)))
			return ProcessType.UNIT;
		return ProcessType.UNKNOWN;
	}

}