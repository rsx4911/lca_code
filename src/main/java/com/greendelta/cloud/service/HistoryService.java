package com.greendelta.cloud.service;

import java.io.File;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;

public class HistoryService {

	private final RepositoryService repoService;
	private final FetchService fetchService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public HistoryService(RepositoryService repoService,
			FetchService fetchService) {
		this.repoService = repoService;
		this.fetchService = fetchService;
	}

	public Commit getLastCommit(String repoId) {
		List<Commit> commits = getCommits(repoId);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommit(String repoId, ModelType type, String refId) {
		List<Commit> commits = getCommits(repoId, type, refId);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public List<Commit> getCommits(String repoId) {
		return getCommits(repoId, null);
	}

	public List<Commit> getCommits(String repoId, String afterCommitId) {
		File file = repoService.getForId(repoId).getHistoryFile(false);
		MutableBoolean reachedId = new MutableBoolean();
		reachedId.value = afterCommitId == null;
		return dataAccessor.readHistory(file, (element) -> {
			if (element.getId().equals(afterCommitId)) {
				reachedId.value = true;
				return true;
			}
			if (!reachedId.value)
				return true;
			return false;
		});
	}

	public List<Commit> getCommits(String repoId, ModelType type, String refId) {
		return getCommits(repoId, type, refId, null);
	}

	public List<Commit> getCommits(String repoId, ModelType type, String refId,
			String beforeCommitId) {
		File historyFile = repoService.getForId(repoId).getHistoryFile(false);
		MutableBoolean reachedId = new MutableBoolean();
		return dataAccessor.readHistory(historyFile, (commit) -> {
			if (commit.getId().equals(beforeCommitId))
				reachedId.value = true;
			if (reachedId.value)
				return true;
			for (Dataset dataset : fetchService.getReferences(repoId,
					commit.getId())) {
				if (dataset.getType() != type)
					continue;
				if (!dataset.getRefId().equals(refId))
					continue;
				return false;
			}
			return true;
		});
	}

	private class MutableBoolean {

		private boolean value;

	}

}
