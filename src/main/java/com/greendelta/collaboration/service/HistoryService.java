package com.greendelta.collaboration.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class HistoryService {

	private final UserService userService;
	private final ReleaseService releaseService;

	public HistoryService(UserService userService, ReleaseService releaseService) {
		this.userService = userService;
		this.releaseService = releaseService;
	}

	public List<Commit> getAccessibleCommits(Repository repo) {
		return getAccessibleCommits(repo, null, null, null, null);
	}

	public List<Commit> getAccessibleCommits(Repository repo, String path) {
		return getAccessibleCommits(repo, path, null, null, null);
	}

	public List<Commit> getAccessibleCommits(Repository repo, ModelType type, String refId) {
		return getAccessibleCommits(repo, null, type, refId, null);
	}

	public List<Commit> getAccessibleCommitsUntil(Repository repo, String path, String commitId) {
		return getAccessibleCommits(repo, path, null, null, commitId);
	}

	private List<Commit> getAccessibleCommits(Repository repo, String path, ModelType type, String refId,
			String commitId) {
		var commits = repo.commits.find().path(path).model(type, refId).until(commitId).all();
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isAnonymous())
			return commits;
		var releases = getReleaseCommitIds(repo);
		var accessible = commits.stream()
				.filter(commit -> releases.contains(commit.id))
				.collect(Collectors.toList());
		if (accessible.isEmpty())
			throw Response.unauthorized();
		return accessible;
	}

	public Commit getAccessibleCommit(Repository repo, String commitId) {
		var currentUser = userService.getCurrentUser();
		if (commitId == null)
			return getLatestAccessibleCommit(repo);
		var commit = repo.commits.get(commitId);
		if (commit == null)
			return null;
		var releases = getReleaseCommitIds(repo);
		if (currentUser.isAnonymous() && !releases.contains(commit.id))
			throw Response.unauthorized();
		return commit;
	}

	public Commit getLatestAccessibleCommit(Repository repo) {
		return getLatestAccessibleCommitUntil(repo, null, null);
	}

	public Commit getLatestAccessibleCommitUntil(Repository repo, String path, String commitId) {
		var accessibleCommits = getAccessibleCommits(repo, path, null, null, commitId);
		if (!accessibleCommits.isEmpty())
			return accessibleCommits.get(accessibleCommits.size() - 1);
		return null;
	}

	public Commit getPreviouslyAccessibleCommit(Repository repo, String commitId) {
		var accessibleCommits = getAccessibleCommits(repo, null, null, null, commitId);
		if (accessibleCommits.size() == 1)
			return null;
		return accessibleCommits.get(accessibleCommits.size() - 2);
	}

	private Set<String> getReleaseCommitIds(Repository repo) {
		return releaseService.getFor(repo.path()).stream()
				.map(r -> r.commitId)
				.collect(Collectors.toSet());
	}

}
