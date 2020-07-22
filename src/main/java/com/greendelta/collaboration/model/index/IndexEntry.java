package com.greendelta.collaboration.model.index;

import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.core.model.ModelType;

public class IndexEntry extends Dataset implements Cloneable {

	private static final long serialVersionUID = 1982606052315691498L;
	public String repositoryId;
	public String group;
	public String commitId;
	public String commitMessage;
	public long commitTimestamp;
	public IndexAction action;
	public boolean mostRecent;
	// if other commits afterwards did not change this data set, add the commit
	// to commits (so all data sets, part of a commit can be identified
	// without furhter aggregation)
	public List<String> commits;
	public String fullPath;
	public String category;
	public List<String> categoryPaths;
	public List<String> repositoryTags;
	public List<String> datasetTags;

	public String toIndexId() {
		return toIndexId(repositoryId, type, refId, commitId);
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
		d.categories = categories != null ? new ArrayList<>(categories) : null;
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
		e.group = group;
		e.commitId = commitId;
		e.commitMessage = commitMessage;
		e.commitTimestamp = commitTimestamp;
		e.action = action;
		e.fullPath = fullPath;
		e.category = category;
		e.categoryPaths = categoryPaths != null ? new ArrayList<>(categoryPaths) : null;
		e.mostRecent = mostRecent;
		e.commits = commits != null ? new ArrayList<>(commits) : null;
		e.repositoryTags = repositoryTags != null ? new ArrayList<>(repositoryTags) : null;
		e.datasetTags = datasetTags != null ? new ArrayList<>(datasetTags) : null;
	}

	public static String toIndexId(String repositoryId, ModelType type, String refId, String commitId) {
		return repositoryId + "/" + type.name() + "/" + refId + "/" + commitId;
	}

	public static IndexEntry descriptor(String indexId) {
		String[] ids = indexId.split("/");
		IndexEntry entry = new IndexEntry();
		entry.repositoryId = ids[0] + "/" + ids[1];
		entry.type = ModelType.valueOf(ids[2]);
		entry.refId = ids[3];
		entry.commitId = ids[4];
		return entry;
	}

}
