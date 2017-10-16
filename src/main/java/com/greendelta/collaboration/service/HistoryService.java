package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;

public class HistoryService {

	private static final Logger log = LogManager.getLogger(HistoryService.class);
	private final SearchService searchService;

	@Inject
	public HistoryService(SearchService searchService) {
		this.searchService = searchService;
	}

	/**
	 * Returns the last commit of the repository
	 */
	public Commit getLastCommit(Repository repo) {
		List<Commit> commits = getCommits(repo);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	/**
	 * Returns the last commit of the specified data set that was not a
	 * "Delete"-commit
	 */
	public Commit getLastCommit(Repository repo, ModelType type, String refId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = readHistory(file, new ModelCommitFilter(repo, type, refId));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommit(Repository repo, ModelType type, String refId, String untilCommitId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = readHistory(file, new LastCommitFilter(untilCommitId, repo, type, refId, false));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommitBefore(Repository repo, ModelType type, String refId, String beforeCommitId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = readHistory(file, new LastCommitFilter(beforeCommitId, repo, type, refId, true));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getCommit(Repository repo, String commitId) {
		File historyFile = repo.getHistoryFile(false);
		List<Commit> commits = readHistory(historyFile, new SpecificCommitFilter(commitId));
		if (commits.isEmpty())
			return null;
		return commits.get(0);
	}

	public List<Commit> getCommits(Repository repo) {
		File file = repo.getHistoryFile(false);
		return readHistory(file, null);
	}

	public List<Commit> getCommits(Repository repo, ModelType type, String refId) {
		File file = repo.getHistoryFile(false);
		return readHistory(file, new ModelCommitFilter(repo, type, refId));
	}

	public List<Commit> getCommitsAfter(Repository repo, String afterCommitId) {
		File file = repo.getHistoryFile(false);
		return readHistory(file, new AfterCommitFilter(afterCommitId));
	}

	public List<Commit> getCommitsUntil(Repository repo, String untilCommitId) {
		File file = repo.getHistoryFile(false);
		return readHistory(file, new UntilCommitFilter(untilCommitId));
	}

	private List<Commit> readHistory(File file, Filter<Commit> filter) {
		if (file == null)
			return Collections.emptyList();
		if (!file.exists())
			return Collections.emptyList();
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			if (lines.isEmpty())
				return Collections.emptyList();
			List<Commit> commits = new ArrayList<>();
			for (String entry : lines) {
				if (entry.trim().isEmpty())
					continue;
				Commit commit = Commit.parse(entry);
				if (filter == null || !filter.filter(commit))
					commits.add(commit);
			}
			return commits;
		} catch (IOException e) {
			log.error("Unexpected error reading to commit history", e);
			return Collections.emptyList();
		}
	}

	interface Filter<T> {
		boolean filter(T element);
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

	private class ModelCommitFilter implements Filter<Commit> {

		private final Repository repo;
		private final ModelType type;
		private final String refId;

		private ModelCommitFilter(Repository repo, ModelType type, String refId) {
			this.repo = repo;
			this.type = type;
			this.refId = refId;
		}

		@Override
		public boolean filter(Commit element) {
			for (IndexEntry entry : searchService.getAll(repo, element)) {
				if (entry.type != type)
					continue;
				if (!entry.refId.equals(refId))
					continue;
				return false;
			}
			return true;

		}

	}

	private class UntilCommitFilter implements Filter<Commit> {

		private final String commitId;
		private boolean reachedId;

		private UntilCommitFilter(String commitId) {
			this.commitId = commitId;
		}

		@Override
		public boolean filter(Commit element) {
			if (reachedId)
				return true;
			if (element.id.equals(commitId))
				reachedId = true;
			return false;
		}

	}

	private class LastCommitFilter implements Filter<Commit> {

		private final String commitId;
		private final Repository repo;
		private final ModelType type;
		private final String refId;
		private boolean done;
		private boolean beforeCommit;

		private LastCommitFilter(String commitId, Repository repo, ModelType type, String refId, boolean beforeCommit) {
			this.commitId = commitId;
			this.repo = repo;
			this.type = type;
			this.refId = refId;
			this.beforeCommit = beforeCommit;
		}

		@Override
		public boolean filter(Commit element) {
			if (done)
				return true;
			if (element.id.equals(commitId))
				done = true;
			if (beforeCommit && done)
				return true;
			return !containsModel(element);
		}

		private boolean containsModel(Commit commit) {
			for (IndexEntry entry : searchService.getAll(repo, commit)) {
				if (entry.type != type)
					continue;
				if (!entry.refId.equals(refId))
					continue;
				return true;
			}
			return false;
		}

	}

	private class SpecificCommitFilter implements Filter<Commit> {

		private final String commitId;

		private SpecificCommitFilter(String commitId) {
			this.commitId = commitId;
		}

		@Override
		public boolean filter(Commit element) {
			return !element.id.equals(commitId);
		}
	}

}
