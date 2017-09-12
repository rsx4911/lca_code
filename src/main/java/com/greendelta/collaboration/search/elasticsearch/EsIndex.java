package com.greendelta.collaboration.search.elasticsearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.greendelta.collaboration.search.SearchIndex;

class EsIndex implements SearchIndex {

	final String name;
	final String type;
	final Map<String, String> data = new HashMap<>();
	
	static EsIndex build(String name, String type, String settingsPath, String mappingPath) throws IOException {
		String settings = Resources.toString(Resources.getResource(settingsPath), Charsets.UTF_8);
		String mapping = Resources.toString(Resources.getResource(mappingPath), Charsets.UTF_8);
		return new EsIndex(name, type, settings, mapping);
	}

	private EsIndex(String name, String type, String settings, String mapping) {
		this.name = name;
		this.type = type;
		this.data.put("settings", settings);
		this.data.put("mapping", mapping);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public Map<String, String> getData() {
		return data;
	}

}
