package com.greendelta.collaboration.model;

import org.openlca.core.model.ModelType;

public class DatasetIndexEntry {

	public ModelType type;
	public String refId;
	public String name;
	public ModelType categoryType;
	public String categoryRefId;
	public String commitId;
	public String commitMessage;
	public String fullPath;
	public long lastUpdate;
	public String repositoryId;

}
