package com.greendelta.collaboration.service.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.util.Collections;

public class Commits {

	private static final Logger log = LogManager.getLogger(Commits.class);
	private final FileRepository repo;

	Commits(Repository repo) throws IOException {
		this(repo.dir);
	}

	Commits(File dir) throws IOException {
		this.repo = new FileRepository(dir);
	}

	public Commit get(String id) {
		return new Find(repo).until(id).last();
	}

	public Find find() {
		return new Find(repo);
	}

	public static class Find {

		private final FileRepository repo;
		private String start;
		private boolean includeStart;
		private String end;
		private boolean includeEnd;
		private ModelType type;
		private String refId;

		private Find(FileRepository repo) {
			this.repo = repo;
		}

		public Find from(String from) {
			if (start != null)
				throw new IllegalStateException("can only set from, after or id once");
			this.includeStart = true;
			this.start = from;
			return this;
		}

		public Find after(String after) {
			if (start != null)
				throw new IllegalStateException("can only set from, after or id once");
			this.start = after;
			return this;
		}

		public Find until(String until) {
			if (end != null)
				throw new IllegalStateException("can only set until, before or id once");
			this.includeEnd = true;
			this.end = until;
			return this;
		}

		public Find before(String before) {
			if (end != null)
				throw new IllegalStateException("can only set until, before or id once");
			this.end = before;
			return this;
		}

		public Find model(ModelType type, String refId) {
			if (this.type != null || this.refId != null)
				throw new IllegalStateException("can only set model once");
			if (type == null || refId == null || refId.isEmpty())
				throw new IllegalArgumentException("Type and refId must be set");
			this.type = type;
			this.refId = refId;
			return this;
		}

		public String id() {
			List<Commit> all = all(true);
			if (all == null || all.isEmpty())
				return null;
			return all.get(0).id;
		}

		public Commit last() {
			List<Commit> all = all(true);
			if (all == null || all.isEmpty())
				return null;
			return all.get(0);
		}

		public List<Commit> all() {
			return all(false);
		}

		private List<Commit> all(boolean singleResult) {
			List<Commit> commits = new ArrayList<>();
			try {
				ObjectId fromId = toObjectId(start);
				ObjectId untilId = toObjectId(end);
				for (RevCommit commit : get(fromId, untilId)) {
					String commitId = commit.getId().name();
					if (!includeEnd && end != null && commitId.equals(end))
						continue;
					if (!containsModel(commit))
						continue;
					commits.add(convert(commit));
					if (singleResult)
						return commits;
				}
				if (includeStart && start != null) {
					RevCommit commit = repo.parseCommit(fromId);
					if (containsModel(commit)) {
						commits.add(convert(commit));
					}
				}
			} catch (IOException | GitAPIException e) {
				log.error("Error accessing history", e);
			}
			Collections.reverse(commits);
			return commits;
		}

		@SuppressWarnings("resource")
		private Iterable<RevCommit> get(ObjectId from, ObjectId until) throws IOException, GitAPIException {
			LogCommand command = new Git(repo).log();
			if (until == null) {
				until = repo.resolve("refs/heads/master");
			}
			if (from != null) {
				command.addRange(from, until);
			} else {
				command.add(until);
			}
			try {
				return command.call();
			} catch (NoHeadException e) {
				return new ArrayList<>();
			}
		}

		private ObjectId toObjectId(String value) {
			if (value == null)
				return null;
			return ObjectId.fromString(value);
		}

		private boolean containsModel(RevCommit commit) {
			if (type == null || refId == null || refId.isEmpty())
				return true;
			try (TreeWalk tw = new TreeWalk(repo)) {
				tw.setFilter(PathFilter.create(type.name()));
				tw.addTree(commit.getTree());
				tw.setRecursive(true);
				while (tw.next()) {
					// TODO for now support both endings
					if (tw.isPathSuffix((refId + ".json").getBytes(), (refId + ".json").getBytes().length))
						return true;
					if (tw.isPathSuffix((refId + ".proto").getBytes(), (refId + ".proto").getBytes().length))
						return true;
				}
				return false;
			} catch (IOException e) {
				log.error("Error trying to find " + type.name() + " " + refId + " in commit " + commit.getId().name(),
						e);
				return false;
			}
		}

		private Commit convert(RevCommit gitCommit) {
			if (gitCommit == null)
				return null;
			Commit commit = new Commit();
			commit.id = gitCommit.getId().getName();
			commit.message = gitCommit.getFullMessage();
			commit.timestamp = gitCommit.getCommitTime();
			commit.user = gitCommit.getAuthorIdent().getName();
			return commit;
		}
	}

}
