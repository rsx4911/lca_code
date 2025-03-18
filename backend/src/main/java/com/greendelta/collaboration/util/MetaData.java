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
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.git.util.FieldDefinition;
import org.openlca.util.Strings;

import com.greendelta.collaboration.service.LibraryService;

public class MetaData {

	public static Map<String, Object> get(Reference e, OlcaRepository repo, LibraryService libraryService) {
		var entry = Maps.of(e);
		entry.remove("objectId");
		putDatasetInfo(entry, e, repo, libraryService);
		return entry;
	}

	public static Map<String, Object> get(Diff diff, OlcaRepository repo, LibraryService libraryService) {
		var ref = diff.diffType == DiffType.DELETED ? diff.oldRef : diff.newRef;
		var meta = Maps.of(ref);
		meta.remove("objectId");
		putDatasetInfo(meta, ref, repo, libraryService);
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

	private static void putDatasetInfo(Map<String, Object> entry, Reference ref, OlcaRepository repo,
			LibraryService libraryService) {
		entry.put("typeOfEntry", getTypeOfEntry(ref));
		if (ref.isRepositoryInfo || ref.isLibrary) {
			putLibraryInfo(entry, ref, libraryService);
		}
		if (!ref.isDataset)
			return;
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

	private static void putLibraryInfo(Map<String, Object> entry, Reference ref, LibraryService libraryService) {
		entry.put("type", "LIBRARY");
		if (!ref.isLibrary)
			return;
		var refId = Maps.getString(entry, "refId");
		var available = libraryService.get(refId) != null;
		entry.put("isAvailable", available);
		if (available) {
			entry.put("isPublic", libraryService.isPublic(refId));
		}
	}

	private static String getTypeOfEntry(Reference entry) {
		if (entry.isModelType || entry.isRepositoryInfo)
			return "MODEL_TYPE";
		if (entry.isCategory)
			return "CATEGORY";
		if (entry.isDataset)
			return "DATASET";
		if (entry.isLibrary)
			return "LIBRARY";
		return null;
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
			var i1 = !"LIBRARY".equals(t1) ? typesOrder.indexOf(t1) : typesOrder.size();
			var i2 = !"LIBRARY".equals(t1) ? typesOrder.indexOf(t2) : typesOrder.size();
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
