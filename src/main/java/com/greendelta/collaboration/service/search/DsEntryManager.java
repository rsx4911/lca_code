package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jgit.lib.ObjectId;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.ObjectMap;

class DsEntryManager {

	private final Repository repo;
	private final Commit commit;

	DsEntryManager(Repository repo, Commit commit) {
		this.repo = repo;
		this.commit = commit;
	}

	DsEntry createOrUpdate(DsEntry e, Reference ref) {
		if (e == null) {
			e = new DsEntry();
			e.type = ref.type;
			e.refId = ref.refId;
		}
		var v = getVersion(e, ref);
		if (v == null) {
			v = createVersion(ref);
			e.versions.add(v);
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

	private DsVersion genericVersion(Reference ref, ObjectMap metaData) {
		var v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		return v;
	}

	private void fillGenericVersion(DsVersion v, Reference ref, ObjectMap metaData) {
		v.objectId = ref.objectId.name();
		v.name = metaData.getString("name");
		var tags = metaData.getString("tags");
		v.tags = tags != null ? Arrays.asList(tags.split("/")) : new ArrayList<>();
		v.category = !Strings.nullOrEmpty(ref.category) ? ref.category : null;
		v.completeData();
	}

	private DsVersion flowVersion(Reference ref, ObjectMap metaData) {
		var v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		v.flowType = metaData.get("flowType");
		return v;
	}

	private DsVersion processVersion(Reference ref, ObjectMap metaData) {
		var v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		v.location = metaData.get("location");
		v.processType = metaData.get("processType");
		v.contact = metaData.get("contact");
		v.modellingApproach = metaData.get("modellingApproach");
		v.validFromYear = metaData.get("validFromYear");
		v.validUntilYear = metaData.get("validUntilYear");
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
