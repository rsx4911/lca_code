package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.model.index.FlowIndexEntry;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.search.wrapper.SearchResult;

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
			map.put("commitId", descriptor.commitId);
			return map;
		}
		ObjectMap map = ObjectMap.fromMap(entry);
		unsetDummyCategoryId(map);
		ModelType type = ModelTypes.from(entry, "type");
		map.put("type", type);
		map.put("categoryType", ModelTypes.from(entry, "categoryType"));
		putCategoryInfo(map);
		if (type == ModelType.PROCESS) {
			map.put("processType", ModelTypes.processType(entry));
			map.put("modellingApproach", ModellingApproach.from(entry));
		} else if (type == ModelType.FLOW) {
			map.put("flowType", ModelTypes.flowType(entry));
		}
		map.put("lastChange", map.getLong("lastChange"));
		map.put("commitTimestamp", map.getLong("commitTimestamp"));
		map.put("action", IndexAction.from(map));
		return map;
	}

	private void putCategoryInfo(ObjectMap map) {
		if (!map.containsKey("fullPath"))
			return;
		String[] path = map.getString("fullPath").split("/");
		if (path.length <= 1)
			return;
		List<String> categories = Arrays.asList(Arrays.copyOfRange(path, 0, path.length - 1));
		DataFill.categories(map, categories);
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
		parseCategoryInfo(e, entry);
		e.lastChange = entry.get("lastChange");
		e.name = entry.get("name");
		e.refId = entry.get("refId");
		e.repositoryId = entry.get("repositoryId");
		e.group = entry.get("group");
		e.version = entry.get("version");
		e.commitTimestamp = entry.get("commitTimestamp");
		e.action = entry.get("action");
		e.mostRecent = entry.getBoolean("mostRecent");
		e.commits = entry.get("commits");
		if (e.commits == null) {
			e.commits = new ArrayList<>();
		}
		e.repositoryTags = entry.get("repositoryTags");
		if (e.repositoryTags == null) {
			e.repositoryTags = new ArrayList<>();
		}
		e.datasetTags = entry.get("datasetTags");
		if (e.datasetTags == null) {
			e.datasetTags = new ArrayList<>();
		}
		e.type = type;
		return e;
	}

	private void parseCategoryInfo(IndexEntry e, ObjectMap entry) {
		if (entry.containsKey("categories")) {
			e.categories = entry.get("categories");
		}
		if (entry.containsKey("categoryPaths")) {
			e.categoryPaths = entry.get("categoryPaths");
		}
		e.category = entry.get("category");
		if (e.fullPath == null)
			return;
		String[] path = e.fullPath.split("/");
		if (path.length <= 1)
			return;
		List<String> categories = Arrays.asList(Arrays.copyOfRange(path, 0, path.length - 1));
		DataFill.categories(e, categories);
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
		e.processType = ModelTypes.processType(entry);
		e.validFromYear = entry.get("validFromYear");
		e.validUntilYear = entry.get("validUntilYear");
		e.location = entry.get("location");
		e.modellingApproach = entry.get("modellingApproach");
		e.contact = entry.get("contact");
		return e;
	}

	private FlowIndexEntry parseFlowSpecific(ObjectMap entry) {
		FlowIndexEntry e = new FlowIndexEntry();
		e.flowType = ModelTypes.flowType(entry);
		return e;
	}

}
