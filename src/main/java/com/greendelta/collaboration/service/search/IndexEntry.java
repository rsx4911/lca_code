package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.cloud.model.Dataset;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.glad.ModellingApproach;

public class IndexEntry extends Dataset implements Cloneable {

	public String group;
	public List<String> categoryPaths;
	public List<String> repositoryTags;
	public String commitId;
	public String commitMessage;
	public ModellingApproach modellingApproach = ModellingApproach.UNKNOWN;

	public String toIndexId() {
		return toIndexId(repositoryId, type, refId);
	}

	@Override
	public IndexEntry clone() {
		IndexEntry e = new IndexEntry();
		e.refId = refId;
		e.type = type;
		e.name = name;
		e.category = category;
		e.commitId = commitId;
		e.commitMessage = commitMessage;
		e.categoryPaths = categoryPaths != null ? new ArrayList<>(categoryPaths) : null;
		e.tags = tags != null ? new ArrayList<>(tags) : null;
		e.repositoryTags = repositoryTags != null ? new ArrayList<>(repositoryTags) : null;
		return e;
	}

	void completeData() {
		if (Strings.nullOrEmpty(category)) {
			this.categoryPaths = new ArrayList<>();
			return;
		}
		List<String> categories = category != null ? Arrays.asList(category.split("/")) : new ArrayList<>();
		this.categoryPaths = new ArrayList<>();
		String path = null;
		for (String category : categories) {
			if (path == null) {
				path = category;
			} else {
				path += "/" + category;
			}
			this.categoryPaths.add(path);
		}
	}

	public static String toIndexId(String repositoryId, ModelType type, String refId) {
		return repositoryId + "/" + type.name() + "/" + refId;
	}

	public static IndexEntry descriptor(String indexId) {
		String[] ids = indexId.split("/");
		IndexEntry entry = new IndexEntry();
		entry.repositoryId = ids[0] + ids[1];
		entry.group = ids[0];
		entry.type = ModelType.valueOf(ids[2]);
		entry.refId = ids[3];
		return entry;
	}
}
