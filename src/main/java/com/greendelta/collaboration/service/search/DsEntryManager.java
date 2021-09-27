package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jgit.lib.ObjectId;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.Reference;
import org.openlca.core.model.ModelType;
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
		DsVersion v = getVersion(e, ref);
		if (v == null) {
			v = createVersion(ref);
			e.versions.add(v);
		}
		DsRepo r = getRepo(v);
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
		for (DsVersion v : e.versions)
			if (ObjectId.fromString(v.objectId).equals(ref.objectId))
				return v;
		return null;
	}

	private DsVersion createVersion(Reference ref) {
		ObjectMap metaData = MetaData.forSearch(ref, repo);
		if (ref.type == ModelType.PROCESS)
			return processVersion(ref, metaData);
		if (ref.type == ModelType.FLOW)
			return flowVersion(ref, metaData);
		return genericVersion(ref, metaData);
	}

	private DsVersion genericVersion(Reference ref, ObjectMap metaData) {
		DsVersion v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		return v;
	}

	private void fillGenericVersion(DsVersion v, Reference ref, ObjectMap metaData) {
		v.objectId = ref.objectId.name();
		v.name = metaData.getString("name");
		String tags = metaData.getString("tags");
		v.tags = tags != null ? Arrays.asList(tags.split("/")) : new ArrayList<>();
		v.category = !Strings.nullOrEmpty(ref.category) ? ref.category : null;
		v.completeData();
	}

	private DsVersion flowVersion(Reference ref, ObjectMap metaData) {
		DsVersion v = new DsVersion();
		fillGenericVersion(v, ref, metaData);
		v.flowType = metaData.get("flowType");
		return v;
	}

	private DsVersion processVersion(Reference ref, ObjectMap metaData) {
		DsVersion v = new DsVersion();
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
		DsRepo r = new DsRepo();
		r.id = repo.toId();
		r.group = repo.group;
		r.tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
		r.commitId = commit.id;
		r.commitMessage = commit.message;
		return r;
	}

	void remove(DsEntry e, Reference ref) {
		if (e == null)
			return;
		DsVersion v = getVersion(e, ref);
		if (v == null)
			return;
		DsRepo r = getRepo(v);
		if (r == null)
			return;
		v.repos.remove(r);
		if (v.repos.isEmpty()) {
			e.versions.remove(v);
		}
	}

	private DsRepo getRepo(DsVersion v) {
		for (DsRepo r : v.repos)
			if (r.id.equals(repo.toId()))
				return r;
		return null;
	}

}
