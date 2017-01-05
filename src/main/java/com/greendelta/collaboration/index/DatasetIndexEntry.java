package com.greendelta.collaboration.index;

import org.openlca.core.model.ModelType;

public class DatasetIndexEntry {

	public final ModelType type;
	public final String refId;
	public final String name;
	public final ModelType categoryType;
	public final String categoryRefId;
	public final String commitId;
	public final String commitMessage;
	public final String fullPath;
	public final long lastUpdate;
	public final String repositoryId;

	DatasetIndexEntry(ModelType type, String refId, String name,
			ModelType categoryType, String categoryRefId, String commitId,
			String commitMessage, String fullPath, long lastUpdate, String repositoryId) {
		this.type = type;
		this.refId = refId;
		this.name = name;
		this.categoryType = categoryType;
		this.categoryRefId = categoryRefId;
		this.commitId = commitId;
		this.commitMessage = commitMessage;
		this.fullPath = fullPath;
		this.lastUpdate = lastUpdate;
		this.repositoryId = repositoryId;
	}

}
