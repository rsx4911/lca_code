package com.greendelta.collaboration.model.glad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

public enum Nomenclature {

	ILCD;

	@SuppressWarnings("unchecked")
	public static List<Nomenclature> from(Map<String, Object> map) {
		if (map == null)
			return null;
		Object value = map.get("supportedNomenclatures");
		if (value instanceof Nomenclature[])
			return Arrays.asList((Nomenclature[]) value);
		if (value instanceof Collection)
			return new ArrayList<>((Collection<Nomenclature>) value);
		List<String> values = new ArrayList<>();
		if (value instanceof String[]) {
			values = Arrays.asList((String[]) value);
		} else if (value instanceof Collection) {
			try {
				values = new ArrayList<>((Collection<String>) value);
			} catch (Exception e) {
				LogManager.getLogger(Nomenclature.class).warn("Could not parse supported nomenclatures", e);
			}
		}
		if (values == null || values.isEmpty())
			return null;
		List<Nomenclature> result = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			result.add(Nomenclature.valueOf(values.get(i)));
		}
		return result;
	}

}