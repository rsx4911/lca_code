package com.greendelta.collaboration.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Json;
import org.openlca.jsonld.ZipStore;
import org.openlca.jsonld.input.CategoryImport;
import org.openlca.util.KeyGen;
import org.openlca.util.Strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class LegacyJsonConverter implements Closeable {

	private static final ModelType[] types = {
			ModelType.ACTOR,
			ModelType.CURRENCY,
			ModelType.DQ_SYSTEM,
			ModelType.FLOW,
			ModelType.FLOW_PROPERTY,
			ModelType.IMPACT_METHOD,
			ModelType.LOCATION,
			ModelType.PARAMETER,
			ModelType.PROCESS,
			ModelType.PRODUCT_SYSTEM,
			ModelType.PROJECT,
			ModelType.SOCIAL_INDICATOR,
			ModelType.SOURCE,
			ModelType.UNIT_GROUP,
			ModelType.IMPACT_CATEGORY // must come last, for copying impacts
	};
	private final File file;
	private final ZipStore zip;
	private final Map<ModelType, Map<String, Category>> categories = new HashMap<>();
	private final Map<String, Set<String>> impactCopies = new HashMap<>();

	LegacyJsonConverter(File inputFile) throws IOException {
		this.file = inputFile;
		this.zip = ZipStore.open(inputFile);
	}

	public File run() throws IOException {
		for (var type : types) {
			for (var obj : zip.getAll(type)) {
				convert(type, obj);
			}
		}
		var categories = zip.getJson(CategoryImport.FILE_NAME);
		if (categories == null || !categories.isJsonArray())
			return file;
		for (var category : categories.getAsJsonArray()) {
			putCategory(category.getAsString());
		}
		zip.remove(CategoryImport.FILE_NAME);
		return file;
	}

	private void convert(ModelType type, JsonObject obj) {
		var wasConverted = switch (type) {
			case UNIT_GROUP -> convertUnitGroup(obj);
			case CURRENCY -> convertCurrency(obj);
			case PROCESS -> convertProcess(obj);
			case PARAMETER -> convertParameter(obj);
			case FLOW -> convertFlow(obj);
			case IMPACT_CATEGORY -> convertImpactCategory(obj);
			case PRODUCT_SYSTEM -> convertProductSystem(obj);
			case LOCATION -> convertLocation(obj);
			case IMPACT_METHOD -> convertImpactMethod(obj);
			default -> false;
		};
		var category = putCategory(obj);
		if (category != null) {
			Json.put(obj, "category", category.toJson());
			wasConverted = true;
		}
		if (wasConverted) {
			zip.put(type, obj);
		}
		if (type == ModelType.IMPACT_CATEGORY) {
			makeImpactCategoryCopies(obj);
		}
		putMetaData();
	}

	private boolean convertUnitGroup(JsonObject obj) {
		return renameInArray(obj, "units", "isRefUnit", "referenceUnit");
	}

	private boolean convertCurrency(JsonObject obj) {
		return rename(obj, "refCurrency", "referenceCurrency");
	}

	private boolean convertProcess(JsonObject obj) {
		var converted = rename(obj, "isInfrastructureProcess", "infrastructureProcess");
		converted = renameInObject(obj, "processDocumentation", "isCopyrightProtected", "copyright") || converted;
		var doc = Json.getObject(obj, "processDocumentation");
		if (doc != null) {
			for (var dt : Arrays.asList("creationDate", "validFrom", "validUntil")) {
				var date = Json.getString(doc, dt);
				if (date == null)
					continue;
				var value = date.split("T")[0];
				if (date.equals(value))
					continue;
				Json.put(doc, dt, value);
				converted = true;
			}
		}
		converted = renameInArray(obj, "exchanges", "isAvoidedProduct", "avoidedProduct") || converted;
		converted = renameInArray(obj, "exchanges", "isInput", "input") || converted;
		converted = renameInArray(obj, "exchanges", "isQuantitativeReference", "quantitativeReference") || converted;
		converted = renameInArray(obj, "parameters", "isInputParameter", "inputParameter") || converted;
		return converted;
	}

	private boolean convertParameter(JsonObject obj) {
		return rename(obj, "isInputParameter", "inputParameter");
	}

	private boolean convertFlow(JsonObject obj) {
		var converted = rename(obj, "isInfrastructureFlow", "infrastructureFlow");
		converted = renameInArray(obj, "flowProperties", "isRefFlowProperty", "referenceFlowProperty") || converted;
		return converted;
	}

	private boolean convertImpactCategory(JsonObject obj) {
		var converted = rename(obj, "refUnit", "referenceUnitName");
		if (obj.has("category")) {
			obj.remove("category");
			converted = true;
		}
		return converted;
	}

	private boolean convertProductSystem(JsonObject obj) {
		var converted = rename(obj, "refExchange", "referenceExchange");
		converted = rename(obj, "refProcess", "referenceProcess") || converted;
		var paramSets = Json.getArray(obj, "parameterSets");
		if (paramSets == null || paramSets.isEmpty())
			return converted;
		JsonObject bestMatch = null;
		for (var ps : paramSets) {
			var paramSet = ps.getAsJsonObject();
			if (Json.getBool(paramSet, "isBaseline", false)) {
				bestMatch = paramSet;
				break;
			}
			if (paramSet == null) {
				bestMatch = paramSet;
			}
		}
		if (bestMatch == null)
			return converted;
		Json.put(obj, "parameterRedefs", bestMatch.get("parameters"));
		return true;
	}

	private boolean convertLocation(JsonObject obj) {
		var geo = Json.getObject(obj, "geometry");
		if (geo == null)
			return false;
		var type = Json.getString(geo, "type");
		var coordinates = Json.getArray(geo, "coordinates");
		if (!"MultiPolygon".equals(type) || coordinates == null || coordinates.isEmpty())
			return false;
		var polygons = new JsonArray();
		for (var coords : coordinates) {
			var polygon = new JsonObject();
			Json.put(polygon, "type", "Polygon");
			Json.put(polygon, "coordinates", coords);
			polygons.add(polygon);
		}
		geo = new JsonObject();
		Json.put(geo, "type", "GeometryCollection");
		Json.put(geo, "geometries", polygons);
		Json.put(obj, "geometry", geo);
		return true;
	}

	private boolean convertImpactMethod(JsonObject obj) {
		extractNwSets(obj);
		return handleImpactCategories(obj);
	}

	private void extractNwSets(JsonObject obj) {
		var nwSets = Json.getArray(obj, "nwSets");
		if (nwSets == null || nwSets.isEmpty())
			return;
		for (var nw : nwSets) {
			var nwSet = nw.getAsJsonObject();
			var id = Json.getString(nwSet, "@id");
			if (id == null || id.strip().isEmpty()) {
				Json.put(obj, "@id", UUID.randomUUID().toString());
			}
			Json.put(nwSet, "@type", "NwSet");
			zip.put("nw_sets/" + id + ".json", nwSet);
		}
	}

	private boolean handleImpactCategories(JsonObject obj) {
		var impacts = Json.getArray(obj, "impactCategories");
		if (impacts == null || impacts.isEmpty())
			return false;
		var impactChanged = false;
		var updated = new JsonArray();
		for (var i : impacts) {
			var impact = i.getAsJsonObject();
			var id = Json.getString(impact, "@id");
			if (!impactCopies.containsKey(id)) {
				impactCopies.put(id, new HashSet<>());
				updated.add(impact);
				continue;
			}
			var newId = UUID.randomUUID().toString();
			Json.put(impact, "@id", newId);
			updated.add(impact);
			impactCopies.get(id).add(newId);
			impactChanged = true;
		}
		if (!impactChanged)
			return false;
		Json.put(obj, "impactCategories", impacts);
		return true;
	}

	private Category putCategory(JsonObject obj) {
		var type = Json.getString(obj, "@type");
		var modelType = getModelType(type);
		var categoryPath = Json.getString(obj, "category");
		return putCategory(modelType, categoryPath);
	}

	private void putCategory(String path) {
		if (Strings.nullOrEmpty(path) || !path.contains("/"))
			return;
		var firstSlash = path.indexOf("/");
		var type = path.substring(0, firstSlash);
		var modelType = ModelType.parse(type);
		var categoryPath = path.substring(firstSlash + 1);
		putCategory(modelType, categoryPath);
	}

	private Category putCategory(ModelType modelType, String categoryPath) {
		if (modelType == null || categoryPath == null || categoryPath.strip().isEmpty())
			return null;
		var pool = categories.get(modelType);
		if (pool == null) {
			pool = new HashMap<>();
			categories.put(modelType, pool);
		}
		var segments = Arrays.asList(categoryPath.split("/")).stream()
				.map(String::strip).collect(Collectors.toList());
		Category category = null;
		var walked = "";
		for (var seg : segments) {
			walked += "/" + seg.toLowerCase();
			var next = pool.get(walked);
			if (next != null) {
				category = next;
				continue;
			}
			next = new Category(seg, modelType, category);
			zip.put(ModelType.CATEGORY, next.toJson());
			pool.put(walked, next);
			category = next;
		}
		return category;
	}

	private ModelType getModelType(String type) {
		if (type == null || type.strip().isEmpty())
			return null;
		for (var t : types)
			if (t.getModelClass().getSimpleName().equals(type))
				return t;
		return null;
	}

	private boolean renameInArray(JsonObject obj, String arrayProp, String oldProp, String newProp) {
		var elements = Json.getArray(obj, arrayProp);
		if (elements == null || elements.isEmpty())
			return false;
		var renamed = false;
		for (var e : elements) {
			renamed = rename(e.getAsJsonObject(), oldProp, newProp) || renamed;
		}
		return renamed;
	}

	private boolean renameInObject(JsonObject obj, String objProp, String oldProp, String newProp) {
		var element = Json.getObject(obj, objProp);
		if (element == null)
			return false;
		return rename(element, oldProp, newProp);
	}

	private boolean rename(JsonObject obj, String oldProp, String newProp) {
		if (!obj.has(oldProp))
			return false;
		var value = obj.remove(oldProp);
		obj.add(newProp, value);
		return true;
	}

	private void makeImpactCategoryCopies(JsonObject obj) {
		var id = Json.getString(obj, "@id");
		for (var copyId : impactCopies.get(id)) {
			Json.put(obj, "@id", copyId);
			zip.put(ModelType.IMPACT_CATEGORY, obj);
		}
	}

	private void putMetaData() {
		var metaInfo = new JsonObject();
		Json.put(metaInfo, "client", "openLCA 1.11.0");
		zip.put("meta.info", metaInfo);
		var context = new JsonObject();
		Json.put(context, "@vocab", "http://openlca.org/schema/v1.1/");
		Json.put(context, "@base", "http://openlca.org/schema/v1.1/");
		for (String type : Arrays.asList(
				"modelType", "flowPropertyType", "flowType", "distributionType", "parameterScope", "allocationType",
				"defaultAllocationMethod", "allocationMethod", "processType", "riskLevel")) {
			var vocab = new JsonObject();
			Json.put(vocab, "@type", "@vocab");
			Json.put(context, type, vocab);
		}
		zip.put("context.json", context);
	}

	@Override
	public void close() throws IOException {
		zip.close();
		String uriStr = file.toURI().toASCIIString();
		URI uri = URI.create("jar:" + uriStr);
		var fs = FileSystems.newFileSystem(uri, Map.of());
		var path = fs.getPath("openlca.json");
		Files.delete(path);
		fs.close();
	}

	private class Category {

		private final String name;
		private final ModelType modelType;
		private final Category parent;

		private Category(String name, ModelType modelType, Category parent) {
			this.name = name;
			this.modelType = modelType;
			this.parent = parent;
		}

		private String path() {
			if (parent == null)
				return name;
			return parent.path() + "/" + name;
		}

		private String uuid() {
			var path = new ArrayList<>(Arrays.asList(path().split("/")));
			path.add(0, modelType.name());
			return KeyGen.get(path.toArray(new String[path.size()]));
		}

		private JsonObject toJson() {
			var obj = new JsonObject();
			Json.put(obj, "@type", "Category");
			Json.put(obj, "@id", uuid());
			Json.put(obj, "name", name);
			Json.put(obj, "modelType", modelType.name());
			if (parent != null) {
				var parentObj = parent.toJson();
				parentObj.remove("category");
				Json.put(obj, "category", parentObj);
			}
			return obj;
		}

	}

}
