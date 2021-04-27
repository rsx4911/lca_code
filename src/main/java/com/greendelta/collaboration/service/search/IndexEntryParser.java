package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.search.wrapper.SearchResult;

// TODO check
class IndexEntryParser {

	List<IndexEntry> parse(SearchResult<Map<String, Object>> result) {
		return parse(result.data);
	}

	List<IndexEntry> parse(List<Map<String, Object>> entries) {
		List<IndexEntry> parsed = new ArrayList<>();
		for (Map<String, Object> entry : entries) {
			parsed.add(parse(entry));
		}
		return parsed;
	}

	IndexEntry parse(Map<String, Object> entry) {
		return parse(convert(entry));
	}

	ObjectMap convert(Map<String, Object> entry) {
		if (entry == null)
			return null;
		if (entry.containsKey("documentId")) {
			ObjectMap map = new ObjectMap();
			IndexEntry descriptor = IndexEntry.descriptor(entry.get("documentId").toString());
			map.put("repositoryId", descriptor.repositoryId);
			map.put("type", descriptor.type);
			map.put("refId", descriptor.refId);
			return map;
		}
		ObjectMap map = ObjectMap.fromMap(entry);
		ModelType type = ModelTypes.from(entry, "type");
		map.put("type", type);
		if (type == ModelType.PROCESS) {
			map.put("processType", ModelTypes.processType(entry));
			map.put("modellingApproach", ModellingApproach.from(entry));
		} else if (type == ModelType.FLOW) {
			map.put("flowType", ModelTypes.flowType(entry));
		}
		map.put("lastChange", map.getLong("lastChange"));
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
		e.category = entry.get("category");
		e.name = entry.get("name");
		e.refId = entry.get("refId");
		e.repositoryId = entry.get("repositoryId");
		e.group = entry.get("group");
		e.repositoryTags = entry.get("repositoryTags");
		if (e.repositoryTags == null) {
			e.repositoryTags = new ArrayList<>();
		}
		e.tags = entry.get("tags");
		if (e.tags == null) {
			e.tags = new ArrayList<>();
		}
		e.type = type;
		return e;
	}

	private IndexEntry parseProcessSpecific(ObjectMap entry) {
		IndexEntry e = new IndexEntry();
		e.processType = ModelTypes.processType(entry);
		e.validFromYear = entry.get("validFromYear");
		e.validUntilYear = entry.get("validUntilYear");
		e.location = entry.get("location");
		e.modellingApproach = entry.get("modellingApproach");
		e.contact = entry.get("contact");
		return e;
	}

	private IndexEntry parseFlowSpecific(ObjectMap entry) {
		IndexEntry e = new IndexEntry();
		e.flowType = ModelTypes.flowType(entry);
		return e;
	}

}
