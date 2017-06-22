package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.servlet.SessionScoped;
import com.greendelta.collaboration.index.DatasetIndexEntry;
import com.greendelta.collaboration.service.DataAccessor.Filter;

/* Commits are stored in text files, this way the repository is always independent from any database 
 * and can e.g. also be copied from one server to another without any other migration 
 * (except user rights on the new server)
 * 
 * Since the commit references are often read and can be quite big a cache is used to enhance read performance 
 * in the web UI. This was added because of heavy performance issues in the data set browsing

 * The cache is session scoped. Singleton scope could lead to a memory problem for bigger cloud instances, 
 * request scope would not solve the performance issues since browsing to a data set is split up in several requests.
 * Session scope is more than needed, but since it does not cause any problems the decision was to not implement 
 * a custom scope which would be quite some effort and would make the client code less readable
 * 
 * Also making the complete history service session scoped would be more than necessary, therefore a inner class is created 
 * with session scope, which is a solution with good enough performance enhancement and least implementation effort
 */
public class HistoryService {

	private final static Logger log = LoggerFactory.getLogger(HistoryService.class);
	private final static Charset charset = Charset.forName("utf-8");
	private final RepositoryService repoService;
	private final DataAccessor dataAccessor;
	private final ReferencesCache referencesCache;

	@Inject
	public HistoryService(RepositoryService repoService, DataAccessor dataAccessor, ReferencesCache referencesCache) {
		this.repoService = repoService;
		this.dataAccessor = dataAccessor;
		this.referencesCache = referencesCache;
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

	public boolean isLastCommit(DatasetIndexEntry entry) {
		String group = entry.repositoryId.substring(0, entry.repositoryId.indexOf("/"));
		String name = entry.repositoryId.substring(entry.repositoryId.indexOf("/") + 1);
		Repository repo = repoService.get(group, name);
		Commit commit = getLastCommit(repo, entry.type, entry.refId);
		if (commit == null)
			return false;
		return commit.id.equals(entry.commitId);
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

	public List<Dataset> getReferences(Repository repo, String commitId) {
		String key = repo.toId() + "/" + commitId;
		if (referencesCache.containsKey(key))
			return referencesCache.get(key);
		File file = repo.getCommitFile(commitId, false);
		try {
			String json = new String(Files.readAllBytes(file.toPath()), charset);
			List<Dataset> references = new Gson().fromJson(json, new TypeToken<List<Dataset>>() {
			}.getType());
			referencesCache.put(key, references);
			return references;
		} catch (IOException e) {
			log.error("Unexpected error while parsing commit history entry", e);
			return Collections.emptyList();
		}
	}

	public Dataset getReference(Repository repo, String commitId, ModelType type, String refId) {
		List<Dataset> references = getReferences(repo, commitId);
		for (Dataset reference : references)
			if (reference.type == type && reference.refId.equals(refId))
				return reference;
		return null;
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
			return !containsModel(element.id);
		}

		private boolean containsModel(String commitId) {
			for (Dataset dataset : getReferences(repo, commitId)) {
				if (dataset.type != type)
					continue;
				if (!dataset.refId.equals(refId))
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

	@SessionScoped
	public static class ReferencesCache extends HashMap<String, List<Dataset>> {

		private static final long serialVersionUID = -833987089892754712L;

	}

}
