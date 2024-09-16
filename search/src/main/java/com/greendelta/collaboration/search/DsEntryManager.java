package com.greendelta.collaboration.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.util.Strings;

class DsEntryManager {

	private final String path;
	private final OlcaRepository repo;
	private final Commit commit;

	DsEntryManager(String path, OlcaRepository repo, Commit commit) {
		this.path = path;
		this.repo = repo;
		this.commit = commit;
	}

	DsEntry createOrUpdate(DsEntry e, Reference ref, List<String> repoTags) {
		if (e == null) {
			e = new DsEntry(ref.type, ref.refId);
		}
		var v = getVersion(e, ref);
		if (v == null) {
			v = createVersion(ref);
			e.versions.add(v);
		}
		for (var other : new ArrayList<>(e.versions)) {
			if (other == v)
				continue;
			var r = getRepo(other);
			other.repos.remove(r);
			if (other.repos.isEmpty()) {
				e.versions.remove(other);
			}
		}
		var r = getRepo(v);
		if (r == null) {
			r = createRepo(ref, repoTags);
			v.repos.add(r);
		} else {
			r.commitId = commit.id;
			r.tags = repoTags;
		}
		return e;
	}

	private DsVersion getVersion(DsEntry e, Reference ref) {
		for (var v : e.versions)
			if (ObjectId.fromString(v.objectId).equals(ref.objectId))
				return v;
		return null;
	}

	private DsVersion createVersion(Reference ref) {
		var metaData = MetaData.get(ref, repo);
		var version = new DsVersion();
		fillGeneric(version, ref, metaData);
		if (ref.type == ModelType.FLOW) {
			fillFlowSpecific(version, metaData);
		} else if (ref.type == ModelType.EPD) {
			fillEpdSpecific(version, metaData);
		} else if (ref.type == ModelType.PROCESS) {
			fillProcessSpecific(version, metaData);
		}
		return version;
	}

	private void fillGeneric(DsVersion v, Reference ref, Map<String, Object> metaData) {
		v.objectId = ref.objectId.name();
		v.name = Maps.getString(metaData, "name");
		v.category = !Strings.nullOrEmpty(ref.category) ? ref.category : null;
		var tags = Maps.getStringArray(metaData, "tags");
		v.tags = tags != null ? Arrays.asList(tags) : new ArrayList<>();
		v.completeData();
	}

	private void fillFlowSpecific(DsVersion v, Map<String, Object> metaData) {
		v.flowType = Maps.get(metaData, "flowType");
	}

	private void fillEpdSpecific(DsVersion v, Map<String, Object> metaData) {
		v.validFromYear = Maps.get(metaData, "validFromYear");
		v.validUntilYear = Maps.get(metaData, "validUntilYear");
	}

	private void fillProcessSpecific(DsVersion v, Map<String, Object> metaData) {
		v.flowType = Maps.get(metaData, "flowType");
		v.validFromYear = Maps.get(metaData, "validFromYear");
		v.validUntilYear = Maps.get(metaData, "validUntilYear");
		v.processType = Maps.get(metaData, "processType");
		v.modellingApproach = Maps.get(metaData, "modellingApproach");
		v.contact = Maps.get(metaData, "contact");
		v.location = Maps.get(metaData, "location");
		v.reviewTypes = Maps.getAll(metaData, "reviewTypes", String.class);
		v.complianceDeclarations = Maps.getAll(metaData, "complianceDeclarations", String.class);
		v.flowCompleteness = Maps.getAll(metaData, "flowCompleteness", String.class);
	}

	private DsRepo createRepo(Reference ref, List<String> tags) {
		var r = new DsRepo();
		r.path = path;
		r.group = path.substring(0, path.indexOf("/"));
		r.tags = tags;
		r.commitId = commit.id;
		return r;
	}

	void remove(DsEntry e, Reference ref) {
		if (e == null)
			return;
		var v = getVersion(e, ref);
		if (v == null)
			return;
		var r = getRepo(v);
		if (r == null)
			return;
		v.repos.remove(r);
		if (v.repos.isEmpty()) {
			e.versions.remove(v);
		}
	}

	private DsRepo getRepo(DsVersion v) {
		for (var r : v.repos)
			if (r.path.equals(path))
				return r;
		return null;
	}

}
