package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Enums;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greendelta.collaboration.model.index.FlowIndexEntry;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ModellingApproach;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;
import com.greendelta.collaboration.util.ObjectMap;

class IndexEntryCreator {

	private static final Gson gson = new Gson();
	private final Repository repo;
	private final Commit commit;

	IndexEntryCreator(Repository repo, Commit commit) {
		this.repo = repo;
		this.commit = commit;
	}

	IndexEntry create(Dataset dataset) {
		return create(dataset, null, null);
	}

	IndexEntry create(Dataset dataset, IndexAction previousAction, File file) {
		if (file == null) {
			IndexEntry entry = generic(dataset);
			entry.action = IndexAction.DELETE;
			return entry;
		}
		IndexEntry entry = null;
		if (dataset.type == ModelType.PROCESS) {
			entry = process(dataset, file);
		} else if (dataset.type == ModelType.FLOW) {
			entry = flow(dataset, file);
		} else {
			entry = generic(dataset);
		}
		if (previousAction == IndexAction.DELETE || previousAction == null) {
			entry.action = IndexAction.ADD;
		} else {
			entry.action = IndexAction.UPDATE;
		}
		return entry;
	}

	private IndexEntry generic(Dataset dataset) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, dataset);
		return entry;
	}

	private void fillGeneric(IndexEntry entry, Dataset dataset) {
		entry.repositoryId = repo.toId();
		entry.type = dataset.type;
		entry.refId = dataset.refId;
		entry.name = dataset.name;
		entry.categoryRefId = dataset.categoryRefId;
		entry.fullPath = dataset.fullPath;
		entry.categoryType = dataset.categoryType;
		entry.commitId = commit.id;
		entry.commitMessage = commit.message;
		entry.commitTimestamp = commit.timestamp;
		entry.lastChange = dataset.lastChange;
		entry.version = dataset.version;
	}

	private FlowIndexEntry flow(Dataset dataset, File dataFile) {
		FlowIndexEntry entry = new FlowIndexEntry();
		fillGeneric(entry, dataset);
		fillFlow(entry, readData(dataFile));
		return entry;
	}

	static void fillFlow(FlowIndexEntry entry, Map<String, Object> map) {
		ObjectMap data = ObjectMap.fromMap(map);		
		entry.flowType = Enums.getValue(data.getString("flowType"), FlowType.class);		
	}

	private ProcessIndexEntry process(Dataset dataset, File dataFile) {
		ProcessIndexEntry entry = new ProcessIndexEntry();
		fillGeneric(entry, dataset);
		fillProcess(entry, readData(dataFile));
		return entry;
	}

	static void fillProcess(ProcessIndexEntry entry, Map<String, Object> map) {
		ObjectMap data = ObjectMap.fromMap(map);
		entry.processType = getProcessType(data.getString("processType"));
		entry.completeness = data.getString("processDocumentation.completenessDescription");
		entry.samplingProcedure = data.getString("processDocumentation.samplingDescription");
		entry.validFrom = data.getLong("processDocumentation.validFrom");
		entry.validUntil = data.getLong("processDocumentation.validUntil");
		entry.location = data.getString("processDocumentation.location.code");
		entry.technology = data.getString("processDocumentation.technologyDescription");
		entry.modellingApproach = getModellingApproach(data.getString("defaultAllocationMethod"));
		entry.reviewer = data.getString("processDocumentation.reviewer.name");
		entry.reviewed = !Strings.isNullOrEmpty(entry.reviewer);
		entry.copyrightProtected = data.getBoolean("processDocumentation.copyright");
		entry.copyrightHolder = data.getString("processDocumentation.dataSetOwner.name");
		entry.contact = entry.copyrightHolder;
		entry.description = data.getString("description");
		putLinkedFlows(entry, data.get("exchanges"));
	}

	private static void putLinkedFlows(ProcessIndexEntry entry, List<Map<String, Object>> exchanges) {
		if (exchanges == null || exchanges.isEmpty())
			return;
		List<String> inputs = new ArrayList<>();
		List<String> outputs = new ArrayList<>();
		for (Map<String, Object> e : exchanges) {
			ObjectMap exchange = ObjectMap.fromMap(e);
			Map<String, Object> flow = exchange.get("flow");
			String flowRefId = ObjectMap.fromMap(flow).get("@id");
			if (exchange.getBoolean("input")) {
				inputs.add(flowRefId);
			} else {
				outputs.add(flowRefId);
			}
		}
		entry.inputs = inputs.toArray(new String[inputs.size()]);
		entry.outputs = outputs.toArray(new String[outputs.size()]);
	}

	private static ProcessType getProcessType(String value) {
		if (value == null)
			return ProcessType.UNKNOWN;
		if (value.equals(Enums.getLabel(org.openlca.core.model.ProcessType.UNIT_PROCESS)))
			return ProcessType.UNIT;
		if (value.equals(Enums.getLabel(org.openlca.core.model.ProcessType.LCI_RESULT)))
			return ProcessType.SYSTEM;
		return ProcessType.UNKNOWN;
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

	private static Map<String, Object> readData(File file) {
		try {
			return gson.fromJson(new FileReader(file), new TypeToken<Map<String, Object>>() {
			}.getType());
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

}
