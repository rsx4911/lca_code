package com.greendelta.collaboration.webservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openlca.core.model.FlowPropertyFactor;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;

class BrowseReferenceFiller {

	private final BrowseService browseService;
	private final FetchService fetchService;
	private final Repository repo;
	private final String commitId;
	private final Map<String, ObjectMap> indexCache = new HashMap<>();
	private final Map<String, JsonObject> dataCache = new HashMap<>();

	BrowseReferenceFiller(BrowseService browseService, FetchService fetchService, Repository repo, String commitId) {
		this.browseService = browseService;
		this.fetchService = fetchService;
		this.repo = repo;
		this.commitId = commitId;
	}

	void fillReferencedElements(JsonObject dataset) {
		for (Entry<String, JsonElement> entry : dataset.entrySet()) {
			JsonElement value = entry.getValue();
			if (value.isJsonObject()) {
				JsonObject oValue = value.getAsJsonObject();
				if (oValue.has("@id")) {
					fillReference(oValue, dataset);
				} else {
					fillReferencedElements(oValue);
				}
			}
			if (value.isJsonArray()) {
				for (JsonElement element : value.getAsJsonArray()) {
					if (!element.isJsonObject())
						continue;
					JsonObject oValue = element.getAsJsonObject();
					if (oValue.has("@id")) {
						fillReference(oValue, dataset);
					} else {
						fillReferencedElements(oValue);
						if (oValue.has("@type")) {
							String type = oValue.get("@type").getAsString();
							if (type.equals(FlowPropertyFactor.class.getSimpleName())) {
								setReferenceUnit(oValue);
								return;
							}
						}
					}
				}
			}
		}
	}

	private void fillReference(JsonObject reference, JsonObject parent) {
		String id = reference.get("@id").getAsString();
		String type = reference.get("@type").getAsString();
		if (type == null || id == null)
			return;
		ModelType mType = getType(type);
		if (mType == ModelType.UNIT) {
			fillUnit(reference, parent);
			return;
		}
		ObjectMap indexEntry = getIndexEntry(id);
		if (indexEntry == null)
			return;
		reference.addProperty("name", indexEntry.getString("name"));
		switch (mType) {
		case PROCESS:
			ProcessType processType = ProcessType.from(indexEntry);
			if (processType != null) {
				reference.addProperty("processType", processType.name());
			}
			reference.addProperty("category", getCategory(indexEntry));
			break;
		case FLOW:
			FlowType flowType = ModelTypes.flowType(indexEntry);
			if (flowType != null) {
				reference.addProperty("flowType", flowType.name());
			}
			reference.addProperty("category", getCategory(indexEntry));
			break;
		case CATEGORY:
			reference.addProperty("name", indexEntry.getString("fullPath"));
			break;
		case SOCIAL_INDICATOR:
		case IMPACT_CATEGORY:
		case NW_SET:
			String commitId = indexEntry.getString("commitId");
			String json = fetchService.getDataset(repo, mType, id, commitId);
			JsonObject dataset = new Gson().fromJson(json, JsonObject.class);
			fillReferencedElements(dataset);
			for (Entry<String, JsonElement> entry : dataset.entrySet()) {
				reference.add(entry.getKey(), entry.getValue());
			}
			break;
		default:
			break;
		}
	}

	private void setReferenceUnit(JsonObject reference) {
		JsonElement flowProperty = reference.get("flowProperty");
		if (flowProperty == null || !flowProperty.isJsonObject())
			return;
		JsonArray units = getUnits(flowProperty.getAsJsonObject());
		if (units == null)
			return;
		for (JsonElement unit : units) {
			if (!unit.isJsonObject())
				continue;
			JsonObject unitObj = unit.getAsJsonObject();
			if (!unitObj.has("referenceUnit"))
				continue;
			if (Boolean.parseBoolean(unitObj.get("referenceUnit").toString()) == false)
				continue;
			reference.add("referenceUnit", unitObj.get("name"));
			break;
		}
		return;
	}

	private void fillUnit(JsonObject unit, JsonObject parent) {
		if (!unit.has("@id"))
			return;
		String unitId = unit.get("@id").getAsString();
		JsonObject u = getUnit(unitId, parent);
		if (u == null)
			return;
		unit.addProperty("name", u.get("name").getAsString());
	}

	private JsonArray getUnits(JsonObject flowProperty) {
		if (!flowProperty.has("@id"))
			return null;
		String fpId = flowProperty.get("@id").getAsString();
		flowProperty = getDataset(ModelType.FLOW_PROPERTY, fpId);
		if (flowProperty == null)
			return null;
		JsonElement unitGroup = flowProperty.get("unitGroup");
		if (unitGroup == null || !unitGroup.isJsonObject())
			return null;
		JsonObject ugObject = unitGroup.getAsJsonObject();
		if (!ugObject.has("@id"))
			return null;
		String ugId = ugObject.get("@id").getAsString();
		ugObject = getDataset(ModelType.UNIT_GROUP, ugId);
		if (ugObject == null || !ugObject.has("units") || !ugObject.get("units").isJsonArray())
			return null;
		return ugObject.get("units").getAsJsonArray();
	}

	private JsonObject getUnit(String unitId, JsonObject parent) {
		if (dataCache.containsKey(unitId))
			return dataCache.get(unitId);
		JsonElement flowProperty = parent.get("flowProperty");
		if (flowProperty == null || !flowProperty.isJsonObject())
			return null;
		JsonObject fpObject = flowProperty.getAsJsonObject();
		JsonArray units = getUnits(fpObject);
		for (JsonElement unit : units) {
			if (!unit.isJsonObject())
				continue;
			JsonObject unitObj = unit.getAsJsonObject();
			if (!unitObj.has("@id") || !unitObj.has("name"))
				continue;
			if (!unitId.equals(unitObj.get("@id").getAsString()))
				continue;
			dataCache.put(unitId, unitObj);
			return unitObj;
		}
		return null;
	}

	private ModelType getType(String type) {
		for (ModelType modelType : ModelType.values()) {
			if (modelType.getModelClass() == null)
				continue;
			if (type.equals(modelType.getModelClass().getSimpleName()))
				return modelType;
		}
		return null;
	}

	private String getCategory(ObjectMap indexEntry) {
		String fullPath = indexEntry.getString("fullPath");
		if (fullPath == null || !fullPath.contains("/"))
			return null;
		String name = indexEntry.getString("name");
		fullPath = fullPath.substring(0, fullPath.length() - name.length() - 1);
		return fullPath;
	}

	private ObjectMap getIndexEntry(String refId) {
		if (indexCache.containsKey(refId))
			return indexCache.get(refId);
		ObjectMap indexEntry = browseService.getDataset(repo, refId, commitId);
		indexCache.put(refId, indexEntry);
		return indexEntry;
	}

	private JsonObject getDataset(ModelType type, String refId) {
		if (dataCache.containsKey(refId))
			return dataCache.get(refId);
		ObjectMap indexEntry = getIndexEntry(refId);
		String data = fetchService.getDataset(repo, type, refId, indexEntry.getString("commitId"));
		JsonObject object = new Gson().fromJson(data, JsonObject.class);
		dataCache.put(refId, object);
		return object;
	}

}
