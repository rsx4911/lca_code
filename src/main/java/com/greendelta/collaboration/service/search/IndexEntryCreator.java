package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import org.openlca.cloud.api.git.Reference;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.jsonld.Enums;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.ObjectMap;

//TODO check
public class IndexEntryCreator {

	private final Repository repo;

	public IndexEntryCreator(Repository repo) {
		this.repo = repo;
	}

	public IndexEntry create(Reference ref, Map<String, Object> data) {
		if (ref.type == ModelType.PROCESS)
			return process(ref, data);
		if (ref.type == ModelType.FLOW)
			return flow(ref, data);
		return generic(ref, data);
	}

	private IndexEntry generic(Reference ref, Map<String, Object> data) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, ref, data);
		return entry;
	}

	private void fillGeneric(IndexEntry entry, Reference ref, Map<String, Object> d) {
		ObjectMap data = ObjectMap.fromMap(d);
		entry.repositoryId = repo.toId();
		entry.group = repo.group;
		entry.type = ref.type;
		entry.refId = ref.refId;
		entry.name = data.getString("name");
		entry.repositoryTags = repo.settings.get(RepositorySetting.TAGS);
		String tags = data.getString("tags");
		entry.tags = tags != null ? Arrays.asList(tags.split("/")) : new ArrayList<>();
		entry.category = ref.category;
		entry.completeData();
	}

	private IndexEntry flow(Reference ref, Map<String, Object> data) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, ref, data);
		fillFlow(entry, data);
		return entry;
	}

	static void fillFlow(IndexEntry entry, Map<String, Object> map) {
		ObjectMap data = ObjectMap.fromMap(map);
		entry.flowType = Enums.getValue(data.getString("flowType"), FlowType.class);
	}

	private IndexEntry process(Reference ref, Map<String, Object> data) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, ref, data);
		fillProcess(entry, data);
		return entry;
	}

	static void fillProcess(IndexEntry entry, Map<String, Object> map) {
		ObjectMap data = ObjectMap.fromMap(map);
		entry.processType = getProcessType(data.getString("processType"));
		long validFrom = Dates.getTime(data.get("processDocumentation.validFrom"));
		entry.validFromYear = getYear(validFrom);
		long validUntil = Dates.getTime(data.get("processDocumentation.validUntil"));
		entry.validUntilYear = getYear(validUntil);
		entry.location = data.getString("location.name");
		entry.modellingApproach = getModellingApproach(data.getString("defaultAllocationMethod"));
		entry.contact = data.getString("processDocumentation.dataSetOwner.name");
	}

	private static Integer getYear(long time) {
		if (time == 0l)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.YEAR);
	}

	private static ProcessType getProcessType(String value) {
		if (value != null && value.equals(Enums.getLabel(ProcessType.LCI_RESULT)))
			return ProcessType.LCI_RESULT;
		return ProcessType.UNIT_PROCESS;
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

}
