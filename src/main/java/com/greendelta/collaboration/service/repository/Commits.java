package com.greendelta.collaboration.service.repository;

import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

public class Commits {

	private final Repository repo;

	Commits(Repository repo) {
		this.repo = repo;
	}

	public String getLastId() {
		Commit commit = getLast();
		if (commit == null)
			return null;
		return commit.id;
	}

	public String getLastId(ModelType type, String refId) {
		Commit commit = getLast(type, refId);
		if (commit == null)
			return null;
		return commit.id;
	}

	public Commit getLast() {
		return getLast(null);
	}

	public Commit getLast(ModelType type, String refId) {
		return getLast(new ModelCommitFilter(type, refId));
	}

	public Commit getLast(ModelType type, String refId, String untilCommitId) {
		return getLast(new LastCommitFilter(untilCommitId, type, refId, false));
	}

	public Commit getLastBefore(ModelType type, String refId, String beforeCommitId) {
		return getLast(new LastCommitFilter(beforeCommitId, type, refId, true));
	}

	public Commit get(String commitId) {
		return getFirst(new SpecificCommitFilter(commitId));
	}

	public List<Commit> get() {
		return get((Filter<Commit>) null);
	}

	public List<Commit> get(ModelType type, String refId) {
		return get(new ModelCommitFilter(type, refId));
	}

	public List<Commit> getAfter(String afterCommitId) {
		return getAfter(afterCommitId, false);
	}

	public List<Commit> getAfter(String afterCommitId, boolean includeLimit) {
		return get(new AfterCommitFilter(afterCommitId, includeLimit));
	}

	public List<Commit> getUntil(String untilCommitId) {
		return get(new UntilCommitFilter(untilCommitId));
	}

	private boolean isInCommit(ModelType type, String refId, String commitId) {
		// TODO
		return true;
	}

	private Commit getFirst(Filter<Commit> filter) {
		List<Commit> commits = get(filter);
		if (commits.isEmpty())
			return null;
		return commits.get(0);
	}

	private Commit getLast(Filter<Commit> filter) {
		List<Commit> commits = get(filter);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	private List<Commit> get(Filter<Commit> filter) {
		// TODO
		return new ArrayList<>();
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

		private final ModelType type;
		private final String refId;

		private ModelCommitFilter(ModelType type, String refId) {
			this.type = type;
			this.refId = refId;
		}

		@Override
		public boolean filter(Commit element) {
			return isInCommit(type, refId, element.id);
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
		private final ModelType type;
		private final String refId;
		private boolean done;
		private boolean beforeCommit;

		private LastCommitFilter(String commitId, ModelType type, String refId, boolean beforeCommit) {
			this.commitId = commitId;
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
			return isInCommit(type, refId, element.id);
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
