package com.greendelta.collaboration.model.index;

import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;

public class IndexEntry extends Dataset implements Cloneable {

	private static final long serialVersionUID = 1982606052315691498L;
	public String repositoryId;
	public String commitId;
	public String commitMessage;
	public long commitTimestamp;
	public IndexAction action;
	public boolean mostRecent;

	public String toIndexId() {
		return repositoryId + "/" + refId + "/" + commitId;
	}

	public Dataset asDataset() {
		Dataset d = new Dataset();
		fillDatasetInfo(d);
		return d;
	}

	protected void fillDatasetInfo(Dataset d) {
		d.refId = refId;
		d.type = type;
		d.version = version;
		d.lastChange = lastChange;
		d.name = name;
		d.fullPath = fullPath;
		d.categoryRefId = categoryRefId;
		d.categoryType = categoryType;
	}

	public FetchRequestData asFetchRequestData() {
		FetchRequestData d = new FetchRequestData();
		fillDatasetInfo(d);
		if (action == IndexAction.ADD) {
			d.setAdded(true);
		} else if (action == IndexAction.DELETE) {
			d.setDeleted(true);
		}
		return d;
	}

	@Override
	public IndexEntry clone() {
		IndexEntry entry = new IndexEntry();
		fillIndexEntryInfo(entry);
		return entry;
	}

	protected void fillIndexEntryInfo(IndexEntry e) {
		fillDatasetInfo(e);
		e.repositoryId = repositoryId;
		e.commitId = commitId;
		e.commitMessage = commitMessage;
		e.commitTimestamp = commitTimestamp;
		e.action = action;
	}
}
