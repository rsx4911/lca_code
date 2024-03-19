package com.greendelta.collaboration.service.search;

import java.util.Map;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.ModelTypes;

class DsEntryParser {

	@SuppressWarnings("unchecked")
	DsEntry parse(Map<String, Object> entry) {
		if (entry == null)
			return null;
		var type = ModelTypes.from(entry);
		var refId = Maps.getString(entry, "refId");
		var e = new DsEntry(type, refId);
		for (var vMap : Maps.getAll(entry, "versions", Map.class)) {
			var version = (Map<String, Object>) vMap;
			var v = new DsVersion();
			if (e.type == ModelType.PROCESS) {
				v = parseProcessSpecific(version);
			} else if (e.type == ModelType.FLOW) {
				v = parseFlowSpecific(version);
			}
			v.objectId = Maps.get(version, "objectId");
			v.category = Maps.get(version, "category");
			v.categoryPaths = Maps.getAll(version, "categoryPaths", String.class);
			v.setName(Maps.get(version, "name"));
			v.tags = Maps.getAll(version, "tags", String.class);
			for (var cMap : Maps.getAll(version, "repos", Map.class)) {
				var commit = (Map<String, Object>) cMap;
				var r = new DsRepo();
				r.path = Maps.get(commit, "path");
				r.group = Maps.get(commit, "group");
				r.tags = Maps.getAll(commit, "tags", String.class);
				r.commitId = Maps.get(commit, "commitId");
				r.commitMessage = Maps.get(commit, "commitMessage");
				v.repos.add(r);
			}
			e.versions.add(v);
		}
		return e;
	}

	private DsVersion parseProcessSpecific(Map<String, Object> version) {
		var v = new DsVersion();
		v.processType = ModelTypes.processType(version);
		v.flowType = ModelTypes.flowType(version);
		v.validFromYear = Maps.get(version, "validFromYear");
		v.validUntilYear = Maps.get(version, "validUntilYear");
		v.location = Maps.get(version, "location");
		v.modellingApproach = ModellingApproach.from(version);
		v.contact = Maps.get(version, "contact");
		v.reviewTypes = Maps.getAll(version, "reviewTypes", String.class);
		v.complianceDeclarations = Maps.getAll(version, "complianceDeclarations", String.class);
		v.setIntendedApplication(Maps.get(version, "intendedApplication"));
		return v;
	}

	private DsVersion parseFlowSpecific(Map<String, Object> version) {
		var v = new DsVersion();
		v.flowType = ModelTypes.flowType(version);
		return v;
	}

}
