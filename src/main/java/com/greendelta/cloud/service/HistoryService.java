package com.greendelta.cloud.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.greendelta.cloud.service.DataAccessor.Filter;

public class HistoryService {

	private final static Logger log = LoggerFactory
			.getLogger(HistoryService.class);
	private final static Charset charset = Charset.forName("utf-8");
	private final RepositoryService repoService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public HistoryService(RepositoryService repoService) {
		this.repoService = repoService;
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
		return dataAccessor.readHistory(file, new AfterCommitFilter(
				afterCommitId));
	}

	public List<Commit> getCommits(String repoId, ModelType type, String refId) {
		return getCommits(repoId, type, refId, null);
	}

	public List<Commit> getCommits(String repoId, ModelType type, String refId,
			String beforeCommitId) {
		File historyFile = repoService.getForId(repoId).getHistoryFile(false);
		return dataAccessor.readHistory(historyFile, new BeforeCommitFilter(
				beforeCommitId, repoId, type, refId));
	}

	public List<Dataset> getReferences(String repoId, String commitId) {
		Repository repo = repoService.getForId(repoId);
		File file = repo.getCommitFile(commitId, false);
		try {
			String json = new String(Files.readAllBytes(file.toPath()), charset);
			return new Gson().fromJson(json, new TypeToken<List<Dataset>>() {
			}.getType());
		} catch (IOException e) {
			log.error("Unexpected error while parsing commit history entry", e);
			return Collections.emptyList();
		}
	}

	private class AfterCommitFilter implements Filter<Commit> {

		private String commitId;
		private boolean reachedId;

		private AfterCommitFilter(String commitId) {
			this.commitId = commitId;
			this.reachedId = commitId == null;
		}

		@Override
		public boolean filter(Commit element) {
			if (reachedId)
				return false;
			if (element.id.equals(commitId))
				reachedId = true;
			return true;
		}

	}

	private class BeforeCommitFilter implements Filter<Commit> {

		private final String commitId;
		private final String repoId;
		private final ModelType type;
		private final String refId;
		private boolean reachedId;

		private BeforeCommitFilter(String commitId, String repoId,
				ModelType type, String refId) {
			this.commitId = commitId;
			this.repoId = repoId;
			this.type = type;
			this.refId = refId;
		}

		@Override
		public boolean filter(Commit element) {
			if (reachedId)
				return true;
			if (element.id.equals(commitId)) {
				reachedId = true;
				return true;
			}
			for (Dataset dataset : getReferences(repoId, element.id)) {
				if (dataset.type != type)
					continue;
				if (!dataset.refId.equals(refId))
					continue;
				return false;
			}
			return true;

		}

	}

}
