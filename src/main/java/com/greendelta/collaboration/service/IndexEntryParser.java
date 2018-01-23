package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.index.FlowIndexEntry;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.AggregationType;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.LicenseType;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ModellingApproach;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ModellingPrinciple;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.Nomenclature;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.search.wrapper.SearchResult;

class IndexEntryParser {

	List<IndexEntry> parse(SearchResult<Map<String, Object>> result) {
		List<IndexEntry> parsed = new ArrayList<>();
		for (Map<String, Object> entry : result.data) {
			parsed.add(parse(convert(entry)));
		}
		return parsed;
	}

	IndexEntry parse(Map<String, Object> entry) {
		return parse(convert(entry));
	}

	ObjectMap convert(Map<String, Object> entry) {
		if (entry == null)
			return null;
		ObjectMap map = ObjectMap.fromMap(entry);
		unsetDummyCategoryId(map);
		ModelType type = ModelTypes.from(entry, "type");
		map.put("type", type);
		map.put("categoryType", ModelTypes.from(entry, "categoryType"));
		if (type == ModelType.PROCESS) {
			map.put("processType", ProcessType.from(entry));
			map.put("validFrom", map.getLong("validFrom"));
			map.put("validUntil", map.getLong("validUntil"));
			map.put("supportedNomenclatures", Nomenclature.from(entry));
			map.put("modellingPrinciple", ModellingPrinciple.from(entry));
			map.put("modellingApproach", ModellingApproach.from(entry));
			map.put("reviewed", map.getBoolean("reviewed"));
			map.put("aggregationType", AggregationType.from(entry));
			map.put("copyrightProtected", map.getBoolean("copyrightProtected"));
			map.put("licenseType", LicenseType.from(entry));
		} else if (type == ModelType.FLOW) {
			map.put("flowType", ModelTypes.flowType(entry));
		}
		map.put("lastChange", map.getLong("lastChange"));
		map.put("commitTimestamp", map.getLong("commitTimestamp"));
		map.put("action", IndexAction.from(map));
		return map;
	}

	private IndexEntry parse(ObjectMap entry) {
		if (entry == null)
			return null;
		IndexEntry e = new IndexEntry();
		ModelType type = entry.get("type");
		if (type == ModelType.PROCESS) {
			e = parseProcessSpecific(entry);
		} else if (type == ModelType.FLOW) {
			e = parseFlowSpecific(entry);
		}
		e.categoryRefId = entry.get("categoryRefId");
		e.categoryType = entry.get("categoryType");
		e.commitId = entry.get("commitId");
		e.commitMessage = entry.get("commitMessage");
		e.fullPath = entry.get("fullPath");
		e.lastChange = entry.get("lastChange");
		e.name = entry.get("name");
		e.refId = entry.get("refId");
		e.repositoryId = entry.get("repositoryId");
		e.version = entry.get("version");
		e.commitTimestamp = entry.get("commitTimestamp");
		e.action = entry.get("action");
		e.type = type;
		return e;
	}

	// for indexing we have to set a value, this is unset here
	private void unsetDummyCategoryId(ObjectMap entry) {
		if (entry.get("categoryRefId") == null)
			return;
		if (entry.get("type").equals(ModelType.CATEGORY.name())) {
			if (entry.get("categoryRefId").equals(entry.get("categoryType"))) {
				entry.put("categoryRefId", null);
			}
		} else if (entry.get("categoryRefId").equals(entry.get("type"))) {
			entry.put("categoryRefId", null);
		}
	}

	private ProcessIndexEntry parseProcessSpecific(ObjectMap entry) {
		ProcessIndexEntry e = new ProcessIndexEntry();
		e.processType = entry.get("processType");
		e.completeness = entry.get("completeness");
		e.sampleRepresentativeness = entry.get("sampleRepresentativeness");
		e.samplingProcedure = entry.get("samplingProcedure");
		e.validFrom = entry.get("validFrom");
		e.validUntil = entry.get("validUntil");
		e.location = entry.get("location");
		e.technology = entry.get("technology");
		e.supportedNomenclatures = entry.get("supportedNomenclatures");
		e.representativeness = entry.get("representativeness");
		e.modellingPrinciple = entry.get("modellingPrinciple");
		e.modellingApproach = entry.get("modellingApproach");
		e.biogenicCarbon = entry.get("biogenicCarbon");
		e.reviewer = entry.get("reviewer");
		e.reviewed = entry.get("reviewed");
		e.aggregationType = entry.get("aggregationType");
		e.copyrightProtected = entry.get("copyrightProtected");
		e.copyrightHolder = entry.get("copyrightHolder");
		e.licenseType = entry.get("licenseType");
		e.license = entry.get("license");
		e.contact = entry.get("contact");
		e.description = entry.get("description");
		return e;
	}

	private FlowIndexEntry parseFlowSpecific(ObjectMap entry) {
		FlowIndexEntry e = new FlowIndexEntry();
		e.flowType = ModelTypes.flowType(entry);
		return e;
	}

}
