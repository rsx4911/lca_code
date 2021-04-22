package com.greendelta.collaboration.service.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import com.greendelta.collaboration.service.repository.Commits.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

public class References {

	private static final Logger log = LogManager.getLogger(Commits.class);
	private final FileRepository repo;

	References(FileRepository repo) throws IOException {
		this.repo = repo;
	}

	public boolean has(ModelType type, Commit commit) {
		return !getForType(type, commit).isEmpty();
	}

	public CommitReference get(ModelType type, String refId, Commit commit) {
		List<CommitReference> refs = get(type, refId, commit, null);
		if (refs.isEmpty())
			return null;
		return refs.get(0);
	}

	public List<CommitReference> getFor(Commit commit) {
		return get(null, null, commit, null);
	}

	public List<CommitReference> getForType(ModelType type, Commit commit) {
		return get(type, null, commit, null);
	}

	public List<CommitReference> getForPath(String path, Commit commit) {
		return get(null, null, commit, path);
	}

	private List<CommitReference> get(ModelType type, String refId, Commit commit, String path) {
		if (commit == null)
			return new ArrayList<>();
		try (TreeWalk walk = new TreeWalk(repo)) {
			List<CommitReference> refs = new ArrayList<>();
			walk.addTree(commit.rev.getTree());
			walk.setRecursive(true);
			if (path != null) {
				walk.setFilter(PathFilter.create(path));
			} else if (type != null && refId == null) {
				walk.setFilter(PathFilter.create(type.name()));
			} else if (type != null && refId != null) {
				walk.setFilter(new ModelFilter(type, refId));
			}
			while (walk.next()) {
				// TODO filter binaries
				refs.add(new CommitReference(type(walk), refId(walk), commit.id, walk.getObjectId(0)));
			}
			return refs;
		} catch (IOException e) {
			log.error("Error getting references, type: " + type + ", refId: " + refId + ", commit: " + commit.id
					+ ", path: " + path, e);
			return new ArrayList<>();
		}
	}

	private ModelType type(TreeWalk walk) {
		String path = walk.getPathString();
		if (path.contains("/")) {
			path = path.substring(0, path.indexOf("/"));
		}
		return ModelType.valueOf(path);
	}
	
	private String refId(TreeWalk walk) {
		String name = walk.getNameString();
		return name.substring(0, name.indexOf("."));
	}

	public static class CommitReference extends FileReference {

		private static final long serialVersionUID = -3043683669338603504L;
		public String commitId;
		public ObjectId objectId;

		private CommitReference(ModelType type, String refId, String commitId, ObjectId objectId) {
			this.type = type;
			this.refId = refId;
			this.commitId = commitId;
			this.objectId = objectId;
		}

	}

}
