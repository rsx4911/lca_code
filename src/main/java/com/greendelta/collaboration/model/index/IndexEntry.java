package com.greendelta.collaboration.model.index;

import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;

public class IndexEntry extends Dataset {

	private static final long serialVersionUID = 1982606052315691498L;
	public String repositoryId;
	public String commitId;
	public String commitMessage;
	public long commitTimestamp;
	public IndexAction action;
	public int typeOrdinal;

	public String toIndexId() {
		return repositoryId + "/" + refId + "/" + commitId;
	}

	public Dataset asDataset() {
		Dataset d = new Dataset();
		d.refId = refId;
		d.type = type;
		d.version = version;
		d.lastChange = lastChange;
		d.name = name;
		d.fullPath = fullPath;
		d.categoryRefId = categoryRefId;
		d.categoryType = categoryType;
		return d;
	}

	public FetchRequestData asFetchRequestData() {
		FetchRequestData d = new FetchRequestData();
		d.refId = refId;
		d.type = type;
		d.version = version;
		d.lastChange = lastChange;
		d.name = name;
		d.fullPath = fullPath;
		d.categoryRefId = categoryRefId;
		d.categoryType = categoryType;
		if (action == IndexAction.ADD) {
			d.setAdded(true);
		} else if (action == IndexAction.DELETE) {
			d.setDeleted(true);
		}
		return d;
	}

}
