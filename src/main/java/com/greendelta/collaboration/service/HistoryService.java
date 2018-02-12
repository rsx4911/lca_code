package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;

public class HistoryService {

	private static final Logger log = LogManager.getLogger(HistoryService.class);
	private final SearchService searchService;
	// caches the commit references for the last repository requested, this is
	// to reduce calls to the search api for consecutive calls over different
	// requests in the same repo
	private static String lastRepoId;
	private static Map<String, Set<String>> lastResult;

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
		List<Commit> commits = readHistory(file, new ModelCommitFilter(repo, refId));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommit(Repository repo, ModelType type, String refId, String untilCommitId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = readHistory(file, new LastCommitFilter(untilCommitId, repo, refId, false));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommitBefore(Repository repo, ModelType type, String refId, String beforeCommitId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = readHistory(file, new LastCommitFilter(beforeCommitId, repo, refId, true));
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
		return readHistory(file, new ModelCommitFilter(repo, refId));
	}

	public List<Commit> getCommitsAfter(Repository repo, String afterCommitId) {
		return getCommitsAfter(repo, afterCommitId, false);
	}

	public List<Commit> getCommitsAfter(Repository repo, String afterCommitId, boolean includeLimit) {
		File file = repo.getHistoryFile(false);
		return readHistory(file, new AfterCommitFilter(afterCommitId, includeLimit));
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

	private boolean isReferenceIn(Repository repo, Commit element, String refId) {
		if (!repo.toId().equals(lastRepoId)) {
			lastResult = new HashMap<>();
			lastRepoId = repo.toId();
		}
		if (!lastResult.containsKey(element.id)) {
			Set<String> ids = new HashSet<>();
			for (IndexEntry entry : searchService.getDescriptors(repo, element)) {
				ids.add(entry.refId);
			}
			lastResult.put(element.id, ids);
		}
		return lastResult.get(element.id).contains(refId);
	}

	interface Filter<T> {
		boolean filter(T element);
	}

	private class AfterCommitFilter implements Filter<Commit> {

		private String commitId;
		private boolean reachedId;
		private boolean includeLimit;

		private AfterCommitFilter(String commitId, boolean includeLimit) {
			this.commitId = commitId;
			this.reachedId = commitId == null;
			this.includeLimit = includeLimit;
		}

		@Override
		public boolean filter(Commit element) {
			if (reachedId)
				return false;
			if (element.id.equals(commitId))
				reachedId = true;
			if (!includeLimit)
				return true;
			return !reachedId;
		}

	}

	private class ModelCommitFilter implements Filter<Commit> {

		private final Repository repo;
		private final String refId;

		private ModelCommitFilter(Repository repo, String refId) {
			this.repo = repo;
			this.refId = refId;
		}

		@Override
		public boolean filter(Commit element) {
			return !isReferenceIn(repo, element, refId);
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
		private final String refId;
		private boolean done;
		private boolean beforeCommit;

		private LastCommitFilter(String commitId, Repository repo, String refId, boolean beforeCommit) {
			this.commitId = commitId;
			this.repo = repo;
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
			return !isReferenceIn(repo, element, refId);
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
