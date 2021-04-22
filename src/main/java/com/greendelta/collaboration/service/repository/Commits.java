package com.greendelta.collaboration.service.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.util.Collections;

public class Commits {

	private static final Logger log = LogManager.getLogger(Commits.class);
	private final FileRepository repo;

	Commits(FileRepository repo) throws IOException {
		this.repo = repo;
	}

	public Commit get(String id) {
		try {
			RevCommit rev = id != null
					? repo.parseCommit(ObjectId.fromString(id))
					: repo.parseCommit(repo.resolve(Constants.HEAD));
			if (rev == null)
				return null;
			return new Commit(rev);
		} catch (IOException e) {
			return null;
		}
	}

	public Find find() {
		return new Find(repo);
	}

	public class Find {

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
			this.includeStart = true;
			this.start = from;
			return this;
		}

		public Find after(String after) {
			this.includeStart = false;
			this.start = after;
			return this;
		}

		public Find until(String until) {
			this.includeEnd = true;
			this.end = until;
			return this;
		}

		public Find before(String before) {
			this.includeEnd = false;
			this.end = before;
			return this;
		}

		public Find path(String path) {
			this.path = path;
			this.type = null;
			this.refId = null;
			return this;
		}

		public Find model(ModelType type, String refId) {
			this.type = type;
			this.refId = refId;
			this.path = null;
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
					commits.add(new Commit(commit));
					if (singleResult)
						return commits;
				}
				if (includeStart && start != null) {
					RevCommit commit = repo.parseCommit(toObjectId(start));
					commits.add(new Commit(commit));
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

	}

	public class Commit extends org.openlca.cloud.model.data.Commit {

		RevCommit rev;

		private Commit(RevCommit rev) {
			this.id = rev.getId().getName();
			this.message = rev.getFullMessage();
			this.timestamp = rev.getCommitTime();
			this.user = rev.getAuthorIdent().getName();
			this.rev = rev;
		}

	}

}
