package com.greendelta.collaboration.service.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
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
		return new Find(repo).until(id).latest();
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
		private String path;

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

		public Find path(String path) {
			if (this.type != null || this.refId != null || this.path != null)
				throw new IllegalStateException("can only set model or path once");
			this.path = path;
			return this;
		}

		public Find model(ModelType type, String refId) {
			if (this.type != null || this.refId != null || this.path != null)
				throw new IllegalStateException("can only set model or path once");
			if (type == null || refId == null || refId.isEmpty())
				throw new IllegalArgumentException("Type and refId must be set");
			this.type = type;
			this.refId = refId;
			return this;
		}

		public String latestId() {
			List<Commit> all = all(true);
			if (all == null || all.isEmpty())
				return null;
			return all.get(all.size() - 1).id;
		}

		public Commit latest() {
			List<Commit> all = all(true);
			if (all == null || all.isEmpty())
				return null;
			return all.get(all.size() - 1);
		}

		public List<Commit> all() {
			return all(false);
		}

		private List<Commit> all(boolean singleResult) {
			List<Commit> commits = new ArrayList<>();
			try (RevWalk walk = get()) {
				for (RevCommit commit : walk) {
					String commitId = commit.getId().name();
					if (!includeEnd && end != null && commitId.equals(end))
						continue;
					commits.add(convert(commit));
					if (singleResult)
						return commits;
				}
				if (includeStart && start != null) {
					RevCommit commit = repo.parseCommit(toObjectId(start));
					commits.add(convert(commit));
				}
			} catch (IOException | GitAPIException e) {
				log.error("Error accessing history", e);
			}
			Collections.reverse(commits);
			return commits;
		}

		private RevWalk get() throws IOException, GitAPIException {
			ObjectId startId = toObjectId(start);
			ObjectId endId = toObjectId(end);
			if (endId == null) {
				endId = repo.resolve(Constants.HEAD);
			}
			RevWalk walk = new RevWalk(repo);
			if (startId != null) {
				walk.markUninteresting(walk.lookupCommit(startId));
			}
			walk.markStart(walk.lookupCommit(endId));
			TreeFilter filter = null;
			if (path != null) {
				filter = PathFilter.create(path);
			} else if (type != null && refId != null) {
				filter = new ModelFilter(type, refId);
			}
			if (filter != null) {
				walk.setTreeFilter(AndTreeFilter.create(filter, TreeFilter.ANY_DIFF));
			}
			return walk;
		}

		private ObjectId toObjectId(String value) {
			if (value == null)
				return null;
			return ObjectId.fromString(value);
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

	private static class ModelFilter extends TreeFilter {

		private final ModelType type;
		private final String refId;

		private ModelFilter(ModelType type, String refId) {
			this.type = type;
			this.refId = refId;
		}

		@Override
		public boolean include(TreeWalk tw)
				throws MissingObjectException, IncorrectObjectTypeException, IOException {
			if (tw.getFileMode() == FileMode.TREE)
				return tw.getPathString().startsWith(type.name());
			String name = tw.getNameString();
			return name.equals(refId + ".proto") || name.equals(refId + ".json");
		}

		@Override
		public boolean shouldBeRecursive() {
			return true;
		}

		@Override
		public TreeFilter clone() {
			return null;
		}

	}

}
