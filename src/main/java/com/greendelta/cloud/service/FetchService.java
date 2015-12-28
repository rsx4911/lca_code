package com.greendelta.cloud.service;

import java.io.File;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;

public class FetchService {

	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public FetchService(RepositoryService repoService,
			HistoryService historyService) {
		this.repoService = repoService;
		this.historyService = historyService;
	}

	public FetchRequestData toRequestData(String repoId, String commitId,
			Dataset dataset) {
		FetchRequestData value = new FetchRequestData(dataset);
		ModelType type = dataset.type;
		String refId = dataset.refId;
		value.setDeleted(wasDeleted(repoId, type, refId, commitId));
		value.setAdded(wasAdded(repoId, type, refId, commitId));
		return value;
	}

	private boolean wasDeleted(String repoId, ModelType type, String refId,
			String commitId) {
		String data = getDataset(repoId, type, refId, commitId);
		if (data == null)
			return true;
		return data.isEmpty();
	}

	private boolean wasAdded(String repoId, ModelType type, String refId,
			String commitId) {
		List<Commit> previous = historyService.getCommits(repoId, type, refId,
				commitId);
		if (previous.isEmpty())
			return true;
		Commit commit = previous.get(previous.size() - 1);
		String previousData = getDataset(repoId, type, refId, commit.id);
		if (previousData == null)
			return true;
		return previousData.isEmpty();
	}

	public String getDataset(String repoId, ModelType type, String refId,
			String commitId) {
		Repository repo = repoService.getForId(repoId);
		File file = repo.getDatasetFile(type, refId, commitId, false);
		return dataAccessor.readDataset(file);
	}

	public File getBinDir(String repoId, ModelType type, String refId,
			String commitId) {
		Repository repo = repoService.getForId(repoId);
		return repo.getBinDir(type, refId, commitId, false);
	}

}
