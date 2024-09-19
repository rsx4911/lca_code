package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Entry;
import org.openlca.git.model.Entry.EntryType;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.git.util.FieldDefinition;
import org.openlca.util.Strings;

public class MetaData {

	public static Map<String, Object> get(Map<String, Object> e, Reference ref, OlcaRepository repo) {
		putDatasetInfo(e, ref, repo);
		return e;
	}

	public static Map<String, Object> get(Entry e, OlcaRepository repo) {
		var entry = Maps.of(e);
		entry.remove("objectId");
		if (e.typeOfEntry != EntryType.DATASET)
			return entry;
		putDatasetInfo(entry, e, repo);
		return entry;
	}

	public static Map<String, Object> get(Diff diff, OlcaRepository repo) {
		var ref = diff.diffType == DiffType.DELETED ? diff.oldRef : diff.newRef;
		var meta = Maps.of(ref);
		meta.remove("objectId");
		putDatasetInfo(meta, ref, repo);
		meta.put("diffType", diff.diffType);
		var commitId = diff.newRef != null ? diff.newRef.commitId : diff.oldRef.commitId;
		meta.put("commitId", commitId);
		return meta;
	}

	public static String getName(OlcaRepository repo, ModelType type, String refId, String commitId) {
		var ref = repo.references.get(type, refId, commitId);
		return getName(repo, ref);
	}

	public static String getName(OlcaRepository repo, Reference ref) {
		var info = repo.datasets.parse(ref, "name");
		var name = info.get("name");
		return name != null ? name.toString() : "";
	}

	private static void putDatasetInfo(Map<String, Object> entry, Reference ref, OlcaRepository repo) {
		if (ref.isCategory) {
			entry.put("name", ref.path.substring(ref.path.lastIndexOf("/") + 1));
			return;
		}
		var defs = new ArrayList<FieldDefinition>();
		defs.add(FieldDefinition.firstOf("name"));
		if (ref.type == ModelType.FLOW || ref.type == ModelType.PROCESS || ref.type == ModelType.EPD) {
			defs.add(FieldDefinition.firstOf("location.name"));
		}
		if (ref.type == ModelType.FLOW) {
			defs.add(FieldDefinition.firstOf("flowType", FlowType::valueOf));
		} else if (ref.type == ModelType.EPD) {
			defs.add(FieldDefinition.firstOf("validFrom", MetaData::getYear));
			defs.add(FieldDefinition.firstOf("validUntil", MetaData::getYear));
		} else if (ref.type == ModelType.PROCESS) {
			defs.add(FieldDefinition.firstOf("location.name"));
			defs.add(FieldDefinition.firstOf("processType", ProcessType::valueOf));
			defs.add(FieldDefinition.firstOf("exchanges.flow.flowType", FlowType::valueOf)
					.ifIs("exchanges.isQuantitativeReference")
					.name("flowType"));
		}
		var info = repo.datasets.parse(ref, defs);
		var location = info.get("location.name");
		if (location == null || location.toString().isEmpty()) {
			entry.put("name", info.get("name"));
		} else {
			entry.put("name", info.get("name") + " - " + location);
			entry.put("location", location);
		}
		if (ref.type == ModelType.FLOW) {
			entry.put("flowType", info.get("flowType"));
		} else if (ref.type == ModelType.PROCESS) {
			entry.put("processType", info.get("processType"));
			entry.put("flowType", info.get("flowType"));
		}
	}

	public static Stream<Map<String, Object>> sortByName(Stream<Map<String, Object>> data) {
		return data.sorted((m1, m2) -> {
			var t1 = Maps.getString(m1, "typeOfEntry");
			var t2 = Maps.getString(m2, "typeOfEntry");
			if (!t1.equals(t2))
				return t1.equals("CATEGORY") ? -1 : 1;
			return Strings.compare(Maps.getString(m1, "name"), Maps.getString(m2, "name"));
		});
	}

	public static Stream<Map<String, Object>> sortByType(Stream<Map<String, Object>> data, List<String> typesOrder) {
		return data.sorted((m1, m2) -> {
			var t1 = Maps.getString(m1, "type");
			var t2 = Maps.getString(m2, "type");
			var i1 = t1 != null ? typesOrder.indexOf(t1) : typesOrder.size();
			var i2 = t2 != null ? typesOrder.indexOf(t2) : typesOrder.size();
			return Integer.compare(i1, i2);
		});
	}

	public static Stream<Map<String, Object>> sortByTypeAndName(Stream<Map<String, Object>> data,
			List<String> typesOrder) {
		return data.sorted((m1, m2) -> {
			var t1 = Maps.getString(m1, "type");
			var t2 = Maps.getString(m2, "type");
			if (!Strings.nullOrEqual(t1, t2))
				return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
			return Strings.compare(Maps.getString(m1, "name"), Maps.getString(m2, "name"));
		});
	}

	private static Integer getYear(String value) {
		var time = Dates.getTime(value);
		if (time == 0l)
			return null;
		var cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.YEAR);
	}

}
