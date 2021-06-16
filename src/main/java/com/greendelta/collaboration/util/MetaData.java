package com.greendelta.collaboration.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectStream;
import org.openlca.cloud.api.git.DiffReference;
import org.openlca.cloud.api.git.Reference;
import org.openlca.cloud.api.git.References.Entry;
import org.openlca.cloud.api.git.References.EntryType;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.jsonld.Enums;
import org.openlca.util.Strings;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.service.Repository;

public class MetaData {

	private static final Logger log = LogManager.getLogger(MetaData.class);

	public static ObjectMap forBrowse(Entry e, Repository repo) {
		return toDatasetInfo(e, repo, Mode.BROWSE);
	}

	public static ObjectMap forSearch(Entry e, Repository repo) {
		return toDatasetInfo(e, repo, Mode.SEARCH);
	}

	private static ObjectMap toDatasetInfo(Entry e, Repository repo, Mode mode) {
		ObjectMap entry = ObjectMap.fromObject(e);
		entry.remove("objectId");
		if (e.typeOfEntry != EntryType.DATASET)
			return entry;
		putDatasetInfo(e.type, entry, e.objectId, repo, mode);
		return entry;
	}

	public static ObjectMap forBrowse(Reference r, Repository repo) {
		return toDatasetInfo(r, repo, Mode.BROWSE);
	}

	public static ObjectMap forSearch(Reference r, Repository repo) {
		return toDatasetInfo(r, repo, Mode.SEARCH);
	}

	private static ObjectMap toDatasetInfo(Reference r, Repository repo, Mode mode) {
		ObjectMap ref = ObjectMap.fromObject(r);
		ref.remove("objectId");
		putDatasetInfo(r.type, ref, r.objectId, repo, mode);
		return ref;
	}

	public static ObjectMap forBrowse(DiffReference d, Repository repo) {
		return putDatasetInfo(d, repo, Mode.BROWSE);
	}

	public static ObjectMap forSearch(DiffReference d, Repository repo) {
		return putDatasetInfo(d, repo, Mode.SEARCH);
	}

	private static ObjectMap putDatasetInfo(DiffReference d, Repository repo, Mode mode) {
		ObjectMap ref = toDatasetInfo(d.ref(), repo, mode);
		ref.put("diffType", d.type);
		return ref;
	}

	private static void putDatasetInfo(ModelType type, ObjectMap entry, ObjectId oId, Repository repo, Mode mode) {
		ObjectStream dataset = repo.datasets.stream(oId);
		ObjectMap info = parse(type, dataset, mode);
		if (Strings.nullOrEmpty(info.getString("location"))) {
			entry.put("name", info.getString("name"));
		} else {
			entry.put("name", info.getString("name") + " - " + info.getString("location"));
			entry.put("location", info.getString("location"));
		}
		entry.put("flowType", info.get("flowType"));
		entry.put("processType", info.get("processType"));
		entry.put("contact", info.get("contact"));
		entry.put("validFromYear", info.get("validFromYear"));
		entry.put("validUntilYear", info.get("validUntilYear"));
		entry.put("modellingApproach", info.get("modellingApproach"));
	}

	public static void sortByName(List<ObjectMap> data) {
		Collections.sort(data, (m1, m2) -> {
			return m1.getString("name").toLowerCase().compareTo(m2.getString("name").toLowerCase());
		});
	}

	public static void sortByType(List<ObjectMap> data, List<String> typesOrder) {
		Collections.sort(data, (m1, m2) -> {
			String t1 = m1.getString("type");
			String t2 = m2.getString("type");
			return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
		});
	}

	public static void sortByTypeAndName(List<ObjectMap> data, List<String> typesOrder) {
		Collections.sort(data, (m1, m2) -> {
			String t1 = m1.getString("type");
			String t2 = m2.getString("type");
			if (!t1.equals(t2))
				return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
			return m1.getString("name").toLowerCase().compareTo(m2.getString("name").toLowerCase());
		});
	}

	private static ObjectMap parse(ModelType type, InputStream json, Mode mode) {
		try {
			JsonFactory factory = new JsonFactory();
			JsonParser parser = factory.createParser(json);
			int objectDepth = 0;
			List<String> parents = new ArrayList<>();
			String fieldName = null;
			ObjectMap map = new ObjectMap();
			outer: while (!parser.isClosed()) {
				JsonToken jsonToken = parser.nextToken();
				if (JsonToken.START_OBJECT.equals(jsonToken)) {
					objectDepth++;
					if (fieldName != null) {
						parents.add(fieldName);
					}
					continue;
				}
				if (JsonToken.END_OBJECT.equals(jsonToken)) {
					objectDepth--;
					if (!parents.isEmpty()) {
						fieldName = parents.remove(parents.size() - 1);
					}
					continue;
				}
				if (!JsonToken.FIELD_NAME.equals(jsonToken))
					continue;
				fieldName = parser.getCurrentName();
				if (objectDepth == 3 && parents.get(1).equals("dataSetOwner")
						&& parents.get(0).equals("processDocumentation") && fieldName.equals("name")) {
					jsonToken = parser.nextToken();
					map.put("contact", parser.getValueAsString());
				} else if (objectDepth == 2) {
					if (parents.get(0).equals("location") && fieldName.equals("name")) {
						jsonToken = parser.nextToken();
						map.put("location", parser.getValueAsString());
					} else if (parents.get(0).equals("processDocumentation")) {
						switch (fieldName) {
						case "validFrom":
							jsonToken = parser.nextToken();
							map.put("validFromYear", getYear(parser.getValueAsString()));
							break;
						case "validUntil":
							jsonToken = parser.nextToken();
							map.put("validUntilYear", getYear(parser.getValueAsString()));
							break;
						}
					}
				} else if (objectDepth == 1) {
					switch (fieldName) {
					case "name":
						jsonToken = parser.nextToken();
						map.put("name", parser.getValueAsString());
						break;
					case "flowType":
						jsonToken = parser.nextToken();
						map.put("flowType", FlowType.valueOf(parser.getValueAsString()));
						break;
					case "processType":
						jsonToken = parser.nextToken();
						map.put("processType", ProcessType.valueOf(parser.getValueAsString()));
						break;
					case "defaultAllocationMethod":
						jsonToken = parser.nextToken();
						map.put("modellingApproach", getModellingApproach(parser.getValueAsString()));
					}
				}
				if (!map.containsKey("name"))
					continue;
				if (type != null && type != ModelType.FLOW && type != ModelType.PROCESS)
					break;
				if (map.containsKey("flowType") && map.containsKey("location"))
					break;
				// now type is always Process
				if (!map.containsKey("processType"))
					continue;
				if (mode == Mode.BROWSE && map.containsKey("location"))
					break;
				if (mode == Mode.SEARCH) {
					for (String field : new String[] { "location", "validFromYear", "validUntilYear",
							"modellingApproach", "contact" })
						if (!map.containsKey(field))
							continue outer;
					break;
				}
			}
			return map;
		} catch (IOException e) {
			log.error("Error parsing dataset", e);
			return new ObjectMap();
		}
	}

	private static Integer getYear(String value) {
		long time = Dates.getTime(value);
		if (time == 0l)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.YEAR);
	}

	private static ModellingApproach getModellingApproach(String value) {
		if (value == null)
			return ModellingApproach.UNKNOWN;
		if (value.equals(Enums.getLabel(AllocationMethod.PHYSICAL)))
			return ModellingApproach.PHYSICAL;
		if (value.equals(Enums.getLabel(AllocationMethod.ECONOMIC)))
			return ModellingApproach.ECONOMIC;
		if (value.equals(Enums.getLabel(AllocationMethod.CAUSAL)))
			return ModellingApproach.CAUSAL;
		if (value.equals(Enums.getLabel(AllocationMethod.NONE)))
			return ModellingApproach.NONE;
		return ModellingApproach.UNKNOWN;
	}

	private static enum Mode {

		BROWSE, SEARCH;

	}

}
