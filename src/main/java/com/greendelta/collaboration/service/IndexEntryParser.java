package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.slf4j.LoggerFactory;

import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.AggregationType;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.LicenseType;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ModellingApproach;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ModellingPrinciple;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.Nomenclature;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;
import com.greendelta.lca.search.SearchResult;

class IndexEntryParser {

	List<IndexEntry> parse(SearchResult<Map<String, Object>> result) {
		List<IndexEntry> parsed = new ArrayList<>();
		for (Map<String, Object> entry : result.data) {
			parsed.add(parse(entry));
		}
		return parsed;
	}

	IndexEntry parse(Map<String, Object> entry) {
		if (entry == null)
			return null;
		IndexEntry e = new IndexEntry();
		ModelType type = toModelType(entry.get("type"));
		if (type == ModelType.PROCESS) {
			e = parseProcessSpecific(entry);
		}
		e.categoryRefId = toString(entry.get("categoryRefId"));
		e.categoryType = toModelType(entry.get("categoryType"));
		e.commitId = toString(entry.get("commitId"));
		e.commitMessage = toString(entry.get("commitMessage"));
		e.fullPath = toString(entry.get("fullPath"));
		e.lastChange = toLong(entry.get("lastChange"));
		e.name = toString(entry.get("name"));
		e.refId = toString(entry.get("refId"));
		e.repositoryId = toString(entry.get("repositoryId"));
		e.version = toString(entry.get("version"));
		e.commitTimestamp = toLong(entry.get("commitTimestamp"));
		e.action = toAction(entry.get("action"));
		e.type = type;
		unsetDummyCategoryId(e);
		return e;
	}

	// for indexing we have to set a value, this is unset here
	private void unsetDummyCategoryId(IndexEntry entry) {
		if (entry.type == ModelType.CATEGORY) {
			if (entry.categoryRefId.equals(entry.categoryType.name())) {
				entry.categoryRefId = null;
			}
		} else if (entry.categoryRefId.equals(entry.type.name())) {
			entry.categoryRefId = null;
		}
	}

	private ProcessIndexEntry parseProcessSpecific(Map<String, Object> entry) {
		ProcessIndexEntry e = new ProcessIndexEntry();
		e.processType = getProcessType(toString(entry.get("processType")));
		e.completeness = toString(entry.get("completeness"));
		e.sampleRepresentativeness = toString(entry.get("sampleRepresentativeness"));
		e.samplingProcedure = toString(entry.get("samplingProcedure"));
		e.validFrom = toLong(entry.get("validFrom"));
		e.validUntil = toLong(entry.get("validUntil"));
		e.location = toString(entry.get("location"));
		e.technology = toString(entry.get("technology"));
		e.supportedNomenclatures = getNomenclatures(entry.get("supportedNomenclatures"));
		e.representativeness = toString(entry.get("representativeness"));
		e.modellingPrinciple = getModellingPrinciple(toString(entry.get("modellingPrinciple")));
		e.modellingApproach = getModellingApproach(toString(entry.get("modellingApproach")));
		e.biogenicCarbon = toString(entry.get("biogenicCarbon"));
		e.reviewer = toString(entry.get("reviewer"));
		e.reviewed = toBoolean(entry.get("reviewed"));
		e.aggregationType = getAggregationType(toString(entry.get("aggregationType")));
		e.copyrightProtected = toBoolean(entry.get("copyrightProtected"));
		e.copyrightHolder = toString(entry.get("copyrightHolder"));
		e.licenseType = getLicenseType(toString(entry.get("licenseType")));
		e.license = toString(entry.get("license"));
		e.contact = toString(entry.get("contact"));
		e.description = toString(entry.get("description"));
		return e;
	}

	private ProcessType getProcessType(String value) {
		if (value == null)
			return ProcessType.UNKNOWN;
		return ProcessType.valueOf(value);
	}

	private Nomenclature[] getNomenclatures(Object value) {
		if (value == null)
			return new Nomenclature[0];
		String[] values = null;
		if (value instanceof String[]) {
			values = (String[]) value;
		} else if (value instanceof Collection) {
			try {
				@SuppressWarnings("unchecked")
				Collection<String> collection = (Collection<String>) value;
				values = collection.toArray(new String[collection.size()]);
			} catch (Exception e) {
				LoggerFactory.getLogger(getClass()).warn("Could not parse supported nomenclatures", e);
			}
		}
		if (values == null)
			return new Nomenclature[0];
		Nomenclature[] result = new Nomenclature[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = Nomenclature.valueOf(values[i]);
		}
		return result;
	}

	private ModellingPrinciple getModellingPrinciple(String value) {
		if (value == null)
			return ModellingPrinciple.UNKNOWN;
		return ModellingPrinciple.valueOf(value);
	}

	private ModellingApproach getModellingApproach(String value) {
		if (value == null)
			return ModellingApproach.UNKNOWN;
		return ModellingApproach.valueOf(value);
	}

	private AggregationType getAggregationType(String value) {
		if (value == null)
			return AggregationType.UNKNOWN;
		return AggregationType.valueOf(value);
	}

	private LicenseType getLicenseType(String value) {
		if (value == null)
			return LicenseType.UNKNOWN;
		return LicenseType.valueOf(value);
	}

	private String toString(Object o) {
		if (o == null)
			return null;
		if ("".equals(o.toString()))
			return null;
		return o.toString();
	}

	private long toLong(Object o) {
		if (o == null)
			return 0;
		return Long.parseLong(o.toString());
	}

	private boolean toBoolean(Object o) {
		if (o == null)
			return false;
		return Boolean.parseBoolean(o.toString());
	}

	private ModelType toModelType(Object o) {
		if (o == null)
			return null;
		return ModelType.valueOf(o.toString().toUpperCase());
	}

	private IndexAction toAction(Object o) {
		if (o == null)
			return null;
		return IndexAction.valueOf(o.toString().toUpperCase());
	}

}
