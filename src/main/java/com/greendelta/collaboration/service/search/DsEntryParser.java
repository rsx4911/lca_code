package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Map;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;

class DsEntryParser {

	@SuppressWarnings("unchecked")
	DsEntry parse(Map<String, Object> map) {
		if (map == null)
			return null;
		ObjectMap entry = ObjectMap.fromMap(map);
		DsEntry e = new DsEntry();
		e.refId = entry.get("refId");
		ModelType type = ModelType.valueOf(entry.get("type"));
		e.type = type;
		for (Map<String, Object> vMap : entry.getAll("versions", Map.class)) {
			ObjectMap version = ObjectMap.fromMap(vMap);
			DsVersion v = new DsVersion();
			if (type == ModelType.PROCESS) {
				v = parseProcessSpecific(entry);
			} else if (type == ModelType.FLOW) {
				v = parseFlowSpecific(entry);
			}
			v.objectId = version.get("objectId");
			v.category = version.get("category");
			v.name = version.get("name");
			v.tags = version.get("tags");
			if (v.tags == null) {
				v.tags = new ArrayList<>();
			}
			for (Map<String, Object> cMap : version.getAll("repos", Map.class)) {
				ObjectMap commit = ObjectMap.fromMap(cMap);
				DsRepo r = new DsRepo();
				r.id = commit.get("id");
				r.group = commit.get("group");
				if (r.tags == null) {
					r.tags = new ArrayList<>();
				}
				r.tags = commit.get("tags");
				r.commitId = commit.get("commitId");
				r.commitMessage = commit.get("commitMessage");
				v.repos.add(r);
			}
			e.versions.add(v);
		}
		return e;
	}

	private DsVersion parseProcessSpecific(ObjectMap version) {
		DsVersion e = new DsVersion();
		e.processType = ModelTypes.processType(version);
		e.validFromYear = version.get("validFromYear");
		e.validUntilYear = version.get("validUntilYear");
		e.location = version.get("location");
		e.modellingApproach = ModellingApproach.from(version);
		e.contact = version.get("contact");
		return e;
	}

	private DsVersion parseFlowSpecific(ObjectMap version) {
		DsVersion e = new DsVersion();
		e.flowType = ModelTypes.flowType(version);
		return e;
	}

}
