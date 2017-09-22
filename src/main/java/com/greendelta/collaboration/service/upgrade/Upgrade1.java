package com.greendelta.collaboration.service.upgrade;

import java.util.List;

import org.openlca.core.model.ModelType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SearchService;

public class Upgrade1 implements IUpgrade {

	@Override
	public String fromSchema() {
		return "http://openlca.org/schema/v1.0/";
	}

	@Override
	public String toSchema() {
		return "http://openlca.org/schema/v1.1/";
	}

	@Override
	public void run(Repository repo, SearchService searchService, GetJson getJson, PutJson putJson) {
		List<IndexEntry> entries = searchService.getAll(repo, ModelType.PROCESS);
		for (IndexEntry entry : entries) {
			JsonObject json = getJson.apply(repo, entry);
			process(json);
			putJson.apply(repo, entry, json);
		}

	}

	private static void process(JsonObject p) {
		if (!p.has("exchanges"))
			return;
		JsonArray exchanges = p.get("exchanges").getAsJsonArray();
		for (JsonElement e : exchanges) {
			exchange((JsonObject) e);
		}
	}

	private static void exchange(JsonObject e) {
		if (!e.has("pedigreeUncertainty"))
			return;
		e.add("dqEntry", e.get("pedigreeUncertainty"));
		e.remove("pedigreeUncertainty");
	}

}
