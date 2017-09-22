package com.greendelta.collaboration.platform.guice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greendelta.collaboration.util.Resources;

class EsMapping {

	private static Gson gson = new Gson();
	private String type;

	EsMapping(String type) {
		this.type = type;
	}

	String build() {
		Map<String, Object> map = Collections.singletonMap(type, Collections.singletonMap("properties", fieldMap()));
		return gson.toJson(map);
	}

	private Map<String, Object> fieldMap() {
		Map<String, Object> map = loadFieldMap("es-fields.json");
		map.putAll(loadFieldMap("es-fields-" + type + ".json"));
		return map;
	}

	private Map<String, Object> loadFieldMap(String name) {
		String json = Resources.get(getClass(), name);
		if (json == null)
			return new HashMap<>();
		return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());
	}

}
