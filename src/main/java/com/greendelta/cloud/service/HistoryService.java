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
import com.greendelta.cloud.service.DataAccessor.Filter;

public class HistoryService {

	private final static Logger log = LoggerFactory
			.getLogger(HistoryService.class);
	private final static Charset charset = Charset.forName("utf-8");
	private final DataAccessor dataAccessor = new DataAccessor();

	public Commit getLastCommit(Repository repo) {
		List<Commit> commits = getCommits(repo);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommit(Repository repo, ModelType type, String refId) {
		List<Commit> commits = getCommits(repo, type, refId);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public List<Commit> getCommits(Repository repo) {
		return getCommits(repo, null);
	}

	public List<Commit> getCommits(Repository repo, String afterCommitId) {
		File file = repo.getHistoryFile(false);
		return dataAccessor.readHistory(file, new AfterCommitFilter(
				afterCommitId));
	}

	public List<Commit> getCommits(Repository repo, ModelType type, String refId) {
		return getCommits(repo, type, refId, null);
	}

	public List<Commit> getCommits(Repository repo, ModelType type,
			String refId, String beforeCommitId) {
		File historyFile = repo.getHistoryFile(false);
		return dataAccessor.readHistory(historyFile, new BeforeCommitFilter(
				beforeCommitId, repo, type, refId));
	}

	public List<Dataset> getReferences(Repository repo, String commitId) {
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
		private final Repository repo;
		private final ModelType type;
		private final String refId;
		private boolean reachedId;

		private BeforeCommitFilter(String commitId, Repository repo,
				ModelType type, String refId) {
			this.commitId = commitId;
			this.repo = repo;
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
			for (Dataset dataset : getReferences(repo, element.id)) {
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
