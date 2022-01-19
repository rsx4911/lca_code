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
		var entry = ObjectMap.fromMap(map);
		var e = new DsEntry();
		e.refId = entry.get("refId");
		e.type = ModelTypes.from(entry);
		for (var vMap : entry.getAll("versions", Map.class)) {
			var version = ObjectMap.fromMap(vMap);
			var v = new DsVersion();
			if (e.type == ModelType.PROCESS) {
				v = parseProcessSpecific(version);
			} else if (e.type == ModelType.FLOW) {
				v = parseFlowSpecific(version);
			}
			v.objectId = version.get("objectId");
			v.category = version.get("category");
			v.categoryPaths = version.getAll("category", String.class);
			v.name = version.get("name");
			v.tags = version.get("tags");
			if (v.tags == null) {
				v.tags = new ArrayList<>();
			}
			for (var cMap : version.getAll("repos", Map.class)) {
				var commit = ObjectMap.fromMap(cMap);
				var r = new DsRepo();
				r.path = commit.get("id");
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
		var v = new DsVersion();
		v.processType = ModelTypes.processType(version);
		v.validFromYear = version.get("validFromYear");
		v.validUntilYear = version.get("validUntilYear");
		v.location = version.get("location");
		v.modellingApproach = ModellingApproach.from(version);
		v.contact = version.get("contact");
		return v;
	}

	private DsVersion parseFlowSpecific(ObjectMap version) {
		var v = new DsVersion();
		v.flowType = ModelTypes.flowType(version);
		return v;
	}

}
