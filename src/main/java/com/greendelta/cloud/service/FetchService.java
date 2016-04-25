package com.greendelta.cloud.service;

import java.io.File;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;

public class FetchService {

	private final HistoryService historyService;
	private final DataAccessor dataAccessor;

	@Inject
	public FetchService(HistoryService historyService, DataAccessor dataAccessor) {
		this.historyService = historyService;
		this.dataAccessor = dataAccessor;
	}

	public FetchRequestData toRequestData(Repository repo, String commitId,
			Dataset dataset) {
		FetchRequestData value = new FetchRequestData(dataset);
		ModelType type = dataset.type;
		String refId = dataset.refId;
		value.setDeleted(wasDeleted(repo, type, refId, commitId));
		value.setAdded(wasAdded(repo, type, refId, commitId));
		return value;
	}

	private boolean wasDeleted(Repository repo, ModelType type, String refId,
			String commitId) {
		String data = getDataset(repo, type, refId, commitId);
		if (data == null)
			return true;
		return data.isEmpty();
	}

	private boolean wasAdded(Repository repo, ModelType type, String refId,
			String commitId) {
		List<Commit> previous = historyService.getCommitsBefore(repo, type, refId,
				commitId);
		if (previous.isEmpty())
			return true;
		Commit commit = previous.get(previous.size() - 1);
		String previousData = getDataset(repo, type, refId, commit.id);
		if (previousData == null)
			return true;
		return previousData.isEmpty();
	}

	public String getDataset(Repository repo, ModelType type, String refId,
			String commitId) {
		File file = repo.getDatasetFile(type, refId, commitId, false);
		return dataAccessor.readDataset(file);
	}

	public File getBinDir(Repository repo, ModelType type, String refId,
			String commitId) {
		return repo.getBinDir(type, refId, commitId, false);
	}

}
