package com.greendelta.collaboration.search;

import java.util.Map;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

public class DsEntryParser {

	@SuppressWarnings("unchecked")
	public DsEntry parse(Map<String, Object> entry) {
		if (entry == null)
			return null;
		var type = getType(entry, "type", ModelType.class);
		var refId = Maps.getString(entry, "refId");
		var e = new DsEntry(type, refId);
		for (var vMap : Maps.getAll(entry, "versions", Map.class)) {
			var version = (Map<String, Object>) vMap;
			var v = new DsVersion();
			parseGeneric(v, version);
			if (e.type == ModelType.FLOW) {
				parseFlowSpecific(v, version);
			} else if (e.type == ModelType.EPD) {
				parseEpdSpecific(v, version);
			} else if (e.type == ModelType.PROCESS) {
				parseProcessSpecific(v, version);
			}
			for (var cMap : Maps.getAll(version, "repos", Map.class)) {
				var commit = (Map<String, Object>) cMap;
				var r = createRepo(commit);
				v.repos.add(r);
			}
			e.versions.add(v);
		}
		return e;

	}

	private void parseGeneric(DsVersion v, Map<String, Object> version) {
		v.objectId = Maps.get(version, "objectId");
		v.category = Maps.get(version, "category");
		v.categoryPaths = Maps.getAll(version, "categoryPaths", String.class);
		v.name = Maps.get(version, "name");
		v.tags = Maps.getAll(version, "tags", String.class);
	}

	private void parseFlowSpecific(DsVersion v, Map<String, Object> version) {
		v.flowType = getType(version, "flowType", FlowType.class);
	}

	private void parseEpdSpecific(DsVersion v, Map<String, Object> version) {
		v.validFromYear = Maps.get(version, "validFromYear");
		v.validUntilYear = Maps.get(version, "validUntilYear");
	}

	private void parseProcessSpecific(DsVersion v, Map<String, Object> version) {
		v.flowType = getType(version, "flowType", FlowType.class);
		v.validFromYear = Maps.get(version, "validFromYear");
		v.validUntilYear = Maps.get(version, "validUntilYear");
		v.modellingApproach = ModellingApproach.from(version);
		v.processType = getType(version, "processType", ProcessType.class);
		v.contact = Maps.get(version, "contact");
		v.location = Maps.get(version, "location");
		v.reviewTypes = Maps.getAll(version, "reviewTypes", String.class);
		v.complianceDeclarations = Maps.getAll(version, "complianceDeclarations", String.class);
		v.flowCompleteness = Maps.getAll(version, "flowCompleteness", String.class);
	}

	private DsRepo createRepo(Map<String, Object> commit) {
		var r = new DsRepo();
		r.path = Maps.get(commit, "path");
		r.group = Maps.get(commit, "group");
		r.tags = Maps.getAll(commit, "tags", String.class);
		r.commitId = Maps.get(commit, "commitId");
		return r;
	}

	private <T extends Enum<T>> T getType(Map<String, Object> version, String field, Class<T> clazz) {
		var t = version.get(field);
		if (t == null)
			return null;
		var flowType = t.toString();
		for (var type : clazz.getEnumConstants())
			if (type.name().equals(flowType))
				return type;
		return null;
	}

}
