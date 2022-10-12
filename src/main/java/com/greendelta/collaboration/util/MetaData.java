package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.jgit.diff.DiffEntry.Side;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Entry;
import org.openlca.git.model.Entry.EntryType;
import org.openlca.git.model.Reference;
import org.openlca.git.util.FieldDefinition;
import org.openlca.jsonld.Enums;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.service.Repository;

public class MetaData {

	public static Map<String, Object> forBrowse(Map<String, Object> e, Reference ref, Repository repo) {
		putDatasetInfo(e, ref, repo, Mode.BROWSE);
		return e;
	}

	public static Map<String, Object> forBrowse(Entry e, Repository repo) {
		return toDatasetInfo(e, repo, Mode.BROWSE);
	}

	private static Map<String, Object> toDatasetInfo(Entry e, Repository repo, Mode mode) {
		var entry = Maps.of(e);
		entry.remove("objectId");
		if (e.typeOfEntry != EntryType.DATASET)
			return entry;
		putDatasetInfo(entry, e, repo, mode);
		return entry;
	}

	public static Map<String, Object> forSearch(Reference r, Repository repo) {
		return toDatasetInfo(r, repo, Mode.SEARCH);
	}

	private static Map<String, Object> toDatasetInfo(Reference r, Repository repo, Mode mode) {
		var ref = Maps.of(r);
		ref.remove("objectId");
		putDatasetInfo(ref, r, repo, mode);
		return ref;
	}

	public static Map<String, Object> forBrowse(Diff diff, Repository repo) {
		var ref = diff.diffType == DiffType.DELETED ? diff.toReference(Side.OLD) : diff.toReference(Side.NEW);
		var meta = putDatasetInfo(ref, diff.diffType, repo, Mode.BROWSE);
		var commitId = diff.newCommitId != null ? diff.newCommitId : diff.oldCommitId;
		meta.put("commitId", commitId);
		return meta;
	}

	public static String getName(Repository repo, ModelType type, String refId, String commitId) {
		var ref = repo.references().get(type, refId, commitId);
		var info = repo.datasets().parse(ref, "name");
		var name = info.get("name");
		return name != null ? name.toString() : "";
	}

	private static Map<String, Object> putDatasetInfo(Reference ref, DiffType diffType, Repository repo, Mode mode) {
		var map = toDatasetInfo(ref, repo, mode);
		map.put("diffType", diffType);
		return map;
	}

	private static void putDatasetInfo(Map<String, Object> entry, Reference ref, Repository repo,
			Mode mode) {
		var defs = new ArrayList<FieldDefinition>();
		defs.add(FieldDefinition.firstOf("name"));
		if (ref.type == ModelType.FLOW || ref.type == ModelType.PROCESS) {
			defs.add(FieldDefinition.firstOf("location.name"));
		}
		if (ref.type == ModelType.FLOW) {
			defs.add(FieldDefinition.firstOf("flowType", FlowType::valueOf));
		} else if (ref.type == ModelType.PROCESS) {
			defs.add(FieldDefinition.firstOf("location.name"));
			defs.add(FieldDefinition.firstOf("processType", ProcessType::valueOf));
			defs.add(FieldDefinition.firstOf("exchanges.flow.flowType", FlowType::valueOf)
					.ifIs("exchanges.isQuantitativeReference")
					.name("flowType"));
			if (mode == Mode.SEARCH) {
				defs.add(FieldDefinition.firstOf("processDocumentation.dataSetOwner.name"));
				defs.add(FieldDefinition.firstOf("processDocumentation.validFrom", MetaData::getYear));
				defs.add(FieldDefinition.firstOf("processDocumentation.validUntil", MetaData::getYear));
				defs.add(FieldDefinition.firstOf("defaultAllocationMethod", MetaData::getModellingApproach));
			}
		}
		var info = repo.datasets().parse(ref, defs);
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
		if (mode == Mode.SEARCH) {
			entry.put("contact", info.get("processDocumentation.dataSetOwner.name"));
			entry.put("validFromYear", info.get("processDocumentation.validFrom"));
			entry.put("validUntilYear", info.get("processDocumentation.validUntil"));
			entry.put("modellingApproach", info.get("defaultAllocationMethod"));
		}
	}

	public static Stream<Map<String, Object>> sortByName(Stream<Map<String, Object>> data) {
		return data.sorted((m1, m2) -> {
			return Maps.getString(m1, "name").toLowerCase().compareTo(Maps.getString(m2, "name").toLowerCase());
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
			if (!t1.equals(t2))
				return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
			return Maps.getString(m1, "name").toLowerCase().compareTo(Maps.getString(m2, "name").toLowerCase());
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
