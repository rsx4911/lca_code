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
import com.google.gson.JsonPrimitive;
import com.greendelta.collaboration.model.index.FlowIndexEntry;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.BrowseService;

class BrowseReferenceFiller {

	private final BrowseService browseService;
	private final FetchService fetchService;
	private final Repository repo;
	private final String commitId;
	private final Map<String, IndexEntry> indexCache = new HashMap<>();
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
					String type = oValue.has("@type") ? oValue.get("@type").getAsString() : null;
					ModelType mType = getType(type);
					if (oValue.has("@id") && mType != null) {
						fillReference(oValue, dataset);
					} else {
						fillReferencedElements(oValue);
						if (FlowPropertyFactor.class.getSimpleName().equals(type)) {
							setReferenceUnit(oValue);
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
		correctCategoryStructure(reference);
		if (hasCompleteInfo(reference, mType))
			return;
		if (mType == ModelType.UNIT) {
			fillUnit(reference, parent);
			return;
		}
		IndexEntry indexEntry = getIndexEntry(id);
		if (indexEntry == null)
			return;
		reference.addProperty("name", indexEntry.name);
		switch (mType) {
		case PROCESS:
			ProcessType processType = ((ProcessIndexEntry) indexEntry).processType;
			if (processType != null) {
				reference.addProperty("processType", processType.name());
			}
			reference.add("category", toCategoryArray(indexEntry, false));
			break;
		case FLOW:
			FlowType flowType = ((FlowIndexEntry) indexEntry).flowType;
			if (flowType != null) {
				reference.addProperty("flowType", flowType.name());
			}
			reference.add("category", toCategoryArray(indexEntry, false));
			break;
		case CATEGORY:
			reference.add("name", toCategoryArray(indexEntry, true));
			break;
		case SOCIAL_INDICATOR:
		case NW_SET:
			String json = fetchService.getDataset(repo, mType, id, indexEntry.commitId);
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

	private void correctCategoryStructure(JsonObject reference) {
		if (reference.has("category") && reference.get("category").isJsonArray())
			return;
		if (reference.has("categoryPath") && reference.get("categoryPath").isJsonArray()) {
			reference.add("category", reference.remove("categoryPath"));
			return;
		}
		if (reference.has("categoryPaths") && reference.get("categoryPaths").isJsonArray()) {
			reference.add("category", reference.remove("categoryPaths"));
			return;
		}
	}

	static JsonArray toCategoryArray(IndexEntry indexEntry, boolean appendOwn) {
		JsonArray array = new JsonArray();
		if (indexEntry.categories != null) {
			for (String category : indexEntry.categories) {
				array.add(new JsonPrimitive(category));
			}
		}
		if (appendOwn) {
			array.add(new JsonPrimitive(indexEntry.name));
		}
		return array;
	}

	private void setReferenceUnit(JsonObject reference) {
		if (reference.has("referenceUnit"))
			return;
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
		if (unit == null || !unit.has("@id"))
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
		if (type == null)
			return null;
		for (ModelType modelType : ModelType.values()) {
			if (modelType.getModelClass() == null)
				continue;
			if (type.equals(modelType.getModelClass().getSimpleName()))
				return modelType;
		}
		return null;
	}

	private IndexEntry getIndexEntry(String refId) {
		if (indexCache.containsKey(refId))
			return indexCache.get(refId);
		IndexEntry indexEntry = browseService.getMostRecent(repo, refId, commitId);
		if (indexEntry == null)
			return null;
		ModelType type = indexEntry.type;
		if (type == ModelType.PROCESS || type == ModelType.IMPACT_CATEGORY || type == ModelType.PRODUCT_SYSTEM
				|| type == ModelType.PROJECT || type == ModelType.IMPACT_METHOD || type == ModelType.NW_SET)
			return indexEntry;
		indexCache.put(refId, indexEntry);
		return indexEntry;
	}

	private JsonObject getDataset(ModelType type, String refId) {
		if (dataCache.containsKey(refId))
			return dataCache.get(refId);
		IndexEntry indexEntry = getIndexEntry(refId);
		if (indexEntry == null)
			return null;
		String data = fetchService.getDataset(repo, type, refId, indexEntry.commitId);
		JsonObject object = new Gson().fromJson(data, JsonObject.class);
		if (type == ModelType.PROCESS || type == ModelType.IMPACT_CATEGORY || type == ModelType.PRODUCT_SYSTEM
				|| type == ModelType.PROJECT || type == ModelType.IMPACT_METHOD || type == ModelType.NW_SET)
			return object;
		dataCache.put(refId, object);
		return object;
	}

	private boolean hasCompleteInfo(JsonObject object, ModelType type) {
		switch (type) {
		case PROCESS:
			return object.has("processType") && object.has("category") && object.get("category").isJsonArray();
		case FLOW:
			return object.has("flowType") && object.has("category") && object.get("category").isJsonArray();
		case CATEGORY:
			return object.has("name") && object.get("name").isJsonArray();
		case SOCIAL_INDICATOR:
		case IMPACT_CATEGORY:
		case NW_SET:
			return false;
		default:
			return object.has("name");
		}
	}

}
