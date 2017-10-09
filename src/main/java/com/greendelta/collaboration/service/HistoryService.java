package com.greendelta.collaboration.service;

import java.io.File;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.DataAccessor.Filter;

public class HistoryService {

	private final SearchService searchService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public HistoryService(SearchService searchService) {
		this.searchService = searchService;
	}

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

	public Commit getLastCommit(Repository repo, ModelType type, String refId, String untilCommitId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = dataAccessor.readHistory(file, new LastCommitFilter(untilCommitId, repo, type, refId,
				false));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getLastCommitBefore(Repository repo, ModelType type, String refId, String beforeCommitId) {
		File file = repo.getHistoryFile(false);
		List<Commit> commits = dataAccessor.readHistory(file, new LastCommitFilter(beforeCommitId, repo, type, refId,
				true));
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public Commit getCommit(Repository repo, String commitId) {
		File historyFile = repo.getHistoryFile(false);
		List<Commit> commits = dataAccessor.readHistory(historyFile, new SpecificCommitFilter(commitId));
		if (commits.isEmpty())
			return null;
		return commits.get(0);
	}

	public List<Commit> getCommits(Repository repo) {
		return getCommitsAfter(repo, null);
	}

	public List<Commit> getCommits(Repository repo, ModelType type, String refId) {
		return getCommitsBefore(repo, type, refId, null);
	}

	public List<Commit> getCommitsAfter(Repository repo, String afterCommitId) {
		File file = repo.getHistoryFile(false);
		return dataAccessor.readHistory(file, new AfterCommitFilter(afterCommitId));
	}

	public List<Commit> getCommitsBetween(Repository repo, String afterCommitId, String untilCommitId) {
		File file = repo.getHistoryFile(false);
		return dataAccessor.readHistory(file, new BetweenCommitFilter(afterCommitId, untilCommitId));
	}

	public List<Commit> getCommitsUntil(Repository repo, String untilCommitId) {
		File file = repo.getHistoryFile(false);
		return dataAccessor.readHistory(file, new UntilCommitFilter(untilCommitId));
	}

	public List<Commit> getCommitsBefore(Repository repo, ModelType type, String refId, String beforeCommitId) {
		File historyFile = repo.getHistoryFile(false);
		return dataAccessor.readHistory(historyFile, new BeforeCommitFilter(beforeCommitId, repo, type, refId));
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

	private class BetweenCommitFilter implements Filter<Commit> {

		private String afterCommitId;
		private String untilCommitId;
		private boolean reachedAfterCommitId;
		private boolean reachedUntilCommitId;

		private BetweenCommitFilter(String afterCommitId, String untilCommitId) {
			this.afterCommitId = afterCommitId;
			this.untilCommitId = untilCommitId;
			this.reachedAfterCommitId = afterCommitId == null;
		}

		@Override
		public boolean filter(Commit element) {
			if (reachedUntilCommitId)
				return true;
			boolean filter = !reachedAfterCommitId || reachedUntilCommitId;
			if (element.id.equals(untilCommitId))
				reachedUntilCommitId = true;
			if (element.id.equals(afterCommitId))
				reachedAfterCommitId = true;
			return filter;
		}

	}

	private class BeforeCommitFilter implements Filter<Commit> {

		private final String commitId;
		private final Repository repo;
		private final ModelType type;
		private final String refId;
		private boolean reachedId;

		private BeforeCommitFilter(String commitId, Repository repo, ModelType type, String refId) {
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
			for (IndexEntry entry : searchService.getAll(repo, element)) {
				if (entry.action == IndexAction.DELETE)
					continue;
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
		private boolean beforeCommit; // if true the commit itself will not be
										// returned

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
				if (entry.action == IndexAction.DELETE)
					continue;
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
