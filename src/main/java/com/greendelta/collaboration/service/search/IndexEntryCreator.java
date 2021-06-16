package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.openlca.cloud.api.git.Reference;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.ObjectMap;

//TODO check
public class IndexEntryCreator {

	private final Repository repo;

	public IndexEntryCreator(Repository repo) {
		this.repo = repo;
	}

	public IndexEntry create(Reference ref, Map<String, Object> data) {
		if (ref.type == ModelType.PROCESS)
			return process(ref, data);
		if (ref.type == ModelType.FLOW)
			return flow(ref, data);
		return generic(ref, data);
	}

	private IndexEntry generic(Reference ref, Map<String, Object> data) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, ref, data);
		return entry;
	}

	private void fillGeneric(IndexEntry entry, Reference ref, Map<String, Object> d) {
		ObjectMap data = ObjectMap.fromMap(d);
		entry.repositoryId = repo.toId();
		entry.group = repo.group;
		entry.type = ref.type;
		entry.refId = ref.refId;
		entry.name = data.getString("name");
		entry.repositoryTags = repo.settings.get(RepositorySetting.TAGS);
		String tags = data.getString("tags");
		entry.tags = tags != null ? Arrays.asList(tags.split("/")) : new ArrayList<>();
		entry.category = ref.category;
		entry.commitId = ref.commitId;
		entry.commitMessage = repo.commits.get(ref.commitId).message;
		entry.completeData();
	}

	private IndexEntry flow(Reference ref, Map<String, Object> data) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, ref, data);
		ObjectMap metaData = MetaData.forSearch(ref, repo);
		entry.flowType = metaData.get("flowType");
		return entry;
	}

	private IndexEntry process(Reference ref, Map<String, Object> data) {
		IndexEntry entry = new IndexEntry();
		fillGeneric(entry, ref, data);
		ObjectMap metaData = MetaData.forSearch(ref, repo);
		entry.location = metaData.get("location");
		entry.processType = metaData.get("processType");
		entry.contact = metaData.get("contact");
		entry.modellingApproach = metaData.get("modellingApproach");
		entry.validFromYear = metaData.get("validFromYear");
		entry.validUntilYear = metaData.get("validUntilYear");
		return entry;
	}

}
