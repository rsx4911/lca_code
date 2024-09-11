package com.greendelta.collaboration.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.openlca.git.model.Commit;
import org.openlca.git.repo.Commits.Find;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class HistoryService {

	private final UserService userService;
	private final ReleaseService releaseService;
	private final SettingsService settings;

	public HistoryService(UserService userService, ReleaseService releaseService, SettingsService settings) {
		this.userService = userService;
		this.releaseService = releaseService;
		this.settings = settings;
	}

	public List<Commit> getAccessibleCommits(Repository repo) {
		return getAccessibleCommits(repo, null);
	}

	public List<Commit> getAccessibleCommits(Repository repo, Consumer<Find> options) {
		var find = repo.commits.find();
		if (options != null) {
			options.accept(find);
		}
		var commits = find.all();
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isAnonymous())
			return commits;
		var releases = getReleaseCommitIds(repo);
		return commits.stream()
				.filter(commit -> releases.contains(commit.id))
				.collect(Collectors.toList());
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
		return getLatestAccessibleCommit(repo, null);
	}

	public Commit getLatestAccessibleCommit(Repository repo, Consumer<Find> options) {
		var accessibleCommits = getAccessibleCommits(repo, options);
		if (accessibleCommits.isEmpty())
			return null;
		return accessibleCommits.get(accessibleCommits.size() - 1);
	}

	public List<Commit> getReleasedCommits(Repository repo) {
		var releaseCommitIds = getReleaseCommitIds(repo);
		return getAccessibleCommits(repo).stream()
				.filter(commit -> releaseCommitIds.contains(commit.id))
				.collect(Collectors.toList());
	}

	public Commit getLatestReleasedCommit(Repository repo) {
		var releasedCommits = getReleasedCommits(repo);
		if (releasedCommits.isEmpty())
			return null;
		return releasedCommits.get(releasedCommits.size() - 1);
	}

	private Set<String> getReleaseCommitIds(Repository repo) {
		if (!settings.is(ServerSetting.RELEASES_ENABLED))
			return new HashSet<>();
		return releaseService.getFor(repo.path()).stream()
				.map(r -> r.commitId)
				.collect(Collectors.toSet());
	}

}
