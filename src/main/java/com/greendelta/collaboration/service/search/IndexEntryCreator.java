package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.jsonld.Enums;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.model.index.FlowIndexEntry;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.ObjectMap;

public class IndexEntryCreator {

	private final Repository repo;
	private final Commit commit;

	public IndexEntryCreator(Repository repo, Commit commit) {
		this.repo = repo;
		this.commit = commit;
	}

	public IndexEntry create(Dataset dataset) {
		return create(dataset, null, null, true);
	}

	public IndexEntry create(Dataset dataset, IndexAction previousAction) {
		return create(dataset, previousAction, null, false);
	}

	public IndexEntry create(Dataset dataset, IndexAction previousAction, Map<String, Object> data) {
		return create(dataset, previousAction, data, true);
	}

	public IndexEntry create(Dataset dataset, IndexAction previousAction, Map<String, Object> data, boolean withData) {
		if (withData && (data == null || data.isEmpty())) {
			IndexEntry entry = generic(dataset);
			entry.action = IndexAction.DELETE;
			return entry;
		}
		IndexEntry entry = null;
		if (dataset.type == ModelType.PROCESS) {
			entry = process(dataset, data);
		} else if (dataset.type == ModelType.FLOW) {
			entry = flow(dataset, data);
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
		entry.group = repo.group;
		entry.type = dataset.type;
		entry.refId = dataset.refId;
		entry.name = dataset.name;
		entry.categoryRefId = dataset.categoryRefId;
		entry.categoryType = dataset.categoryType;
		entry.commitId = commit.id;
		entry.commitMessage = commit.message;
		entry.commitTimestamp = commit.timestamp;
		entry.lastChange = dataset.lastChange;
		entry.version = dataset.version;
		entry.commits = new ArrayList<>(Collections.singletonList(commit.id));
		entry.repositoryTags = repo.settings.tags != null ? new ArrayList<>(repo.settings.tags) : new ArrayList<>();
//		entry.datasetTags = dataset.tags != null ? new ArrayList<>(dataset.tags) : new ArrayList<>();
		DataFill.categories(entry, dataset.categories);
		if (entry.categories != null && !entry.categories.isEmpty()) {
			entry.fullPath = entry.category + '/' + dataset.name;
		} else {
			entry.fullPath = dataset.name;
		}
	}

	private FlowIndexEntry flow(Dataset dataset, Map<String, Object> data) {
		FlowIndexEntry entry = new FlowIndexEntry();
		fillGeneric(entry, dataset);
		fillFlow(entry, data);
		return entry;
	}

	static void fillFlow(FlowIndexEntry entry, Map<String, Object> map) {
		ObjectMap data = ObjectMap.fromMap(map);
		entry.flowType = Enums.getValue(data.getString("flowType"), FlowType.class);
	}

	private ProcessIndexEntry process(Dataset dataset, Map<String, Object> data) {
		ProcessIndexEntry entry = new ProcessIndexEntry();
		fillGeneric(entry, dataset);
		fillProcess(entry, data);
		return entry;
	}

	static void fillProcess(ProcessIndexEntry entry, Map<String, Object> map) {
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
