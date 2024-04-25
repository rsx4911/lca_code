package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;

class DsEntryManager {

	private final Repository repo;
	private final Commit commit;

	DsEntryManager(Repository repo, Commit commit) {
		this.repo = repo;
		this.commit = commit;
	}

	DsEntry createOrUpdate(DsEntry e, Reference ref) {
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
			r = createRepo(ref);
			v.repos.add(r);
		} else {
			r.commitId = commit.id;
			r.commitMessage = commit.message;
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
		var metaData = MetaData.forSearch(ref, repo);
		if (ref.type == ModelType.PROCESS)
			return processVersion(ref, metaData);
		if (ref.type == ModelType.FLOW)
			return flowVersion(ref, metaData);
		return genericVersion(ref, metaData);
	}

	private DsVersion genericVersion(Reference ref, Map<String, Object> metaData) {
		var v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		return v;
	}

	private void fillGenericVersion(DsVersion v, Reference ref, Map<String, Object> metaData) {
		v.objectId = ref.objectId.name();
		v.name = Maps.getString(metaData, "name");
		var tags = Maps.getStringArray(metaData, "tags");
		v.tags = tags != null ? Arrays.asList(tags) : new ArrayList<>();
		v.category = !Strings.nullOrEmpty(ref.category) ? ref.category : null;
		v.completeData();
	}

	private DsVersion flowVersion(Reference ref, Map<String, Object> metaData) {
		var v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		v.flowType = Maps.get(metaData, "flowType");
		return v;
	}

	private DsVersion processVersion(Reference ref, Map<String, Object> metaData) {
		var v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		v.location = Maps.get(metaData, "location");
		v.processType = Maps.get(metaData, "processType");
		v.contact = Maps.get(metaData, "contact");
		v.modellingApproach = Maps.get(metaData, "modellingApproach");
		v.validFromYear = Maps.get(metaData, "validFromYear");
		v.validUntilYear = Maps.get(metaData, "validUntilYear");
		v.flowType = Maps.get(metaData, "flowType");
		v.reviewTypes = Maps.getAll(metaData, "reviewTypes", String.class);
		v.complianceDeclarations = Maps.getAll(metaData, "complianceDeclarations", String.class);
		v.flowCompleteness = Maps.getAll(metaData, "flowCompleteness", String.class);
		return v;
	}

	private DsRepo createRepo(Reference ref) {
		var r = new DsRepo();
		r.path = repo.path();
		r.group = repo.group;
		r.tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
		r.commitId = commit.id;
		r.commitMessage = commit.message;
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
			if (r.path.equals(repo.path()))
				return r;
		return null;
	}

}
