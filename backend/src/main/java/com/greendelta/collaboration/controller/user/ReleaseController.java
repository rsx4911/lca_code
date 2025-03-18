package com.greendelta.collaboration.controller.user;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openlca.git.model.Commit;
import org.openlca.util.Strings;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.ReleaseInfo;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.IndexService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.util.Maps;

@RestController
@RequestMapping("ws/release")
public class ReleaseController {

	private final ReleaseService service;
	private final RepositoryService repoService;
	private final LibraryService libraryService;
	private final HistoryService historyService;
	private final ReleaseService releaseService;
	private final IndexService indexService;
	private final PermissionsService permissions;
	private final SettingsService settings;

	public ReleaseController(ReleaseService service, RepositoryService repoService, LibraryService libraryService,
			HistoryService historyService, ReleaseService releaseService, IndexService indexService,
			PermissionsService permissions, SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.libraryService = libraryService;
		this.historyService = historyService;
		this.releaseService = releaseService;
		this.indexService = indexService;
		this.permissions = permissions;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}/{commitId}")
	public Map<String, Object> getReleaseInfo(@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId) {
		try (var repo = repoService.get(group, name)) {
			checkAccess(repo, commitId);
			if (!service.isReleased(repo.path(), commitId))
				throw Response.notFound("Commit " + commitId + " is not released");
			var release = service.get(repo.path(), commitId);
			var mapped = Maps.of(release);
			var previousId = getPreviousReleaseId(repo, commitId);
			var commits = repo.commits.find().after(previousId).until(commitId).all();
			mapped.put("generatedChangeLog", generateChangeLog(commits));
			return mapped;
		}
	}

	private String getPreviousReleaseId(Repository repo, String commitId) {
		var released = historyService.getReleasedCommits(repo);
		if (released.size() == 1)
			return null;
		Commit previous = null;
		for (var release : released) {
			if (release.id.equals(commitId))
				break;
			previous = release;
		}
		if (previous == null)
			return null;
		return previous.id;
	}

	private String generateChangeLog(List<Commit> commits) {
		var changeLog = "";
		for (var commit : commits) {
			if (!changeLog.isEmpty()) {
				changeLog += "\n";
			}
			changeLog += "* " + commit.message;
		}
		return changeLog;
	}
	
	@PostMapping("{group}/{name}/{commitId}")
	public void release(@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId,
			@RequestBody ReleaseInfo release) {
		try (var repo = repoService.get(group, name)) {
			var before = historyService.getLatestReleasedCommit(repo);
			save(repo, commitId, release);
			var after = historyService.getLatestReleasedCommit(repo);
			if (before == null || !before.id.equals(after.id)) {
				indexService.indexPublicAsync(RepositoryPath.of(repo.path()), before, after);
			}
		}
	}

	@PutMapping("{group}/{name}/{commitId}")
	public void updateRelease(@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId,
			@RequestBody ReleaseInfo release) {
		try (var repo = repoService.get(group, name)) {
			var before = historyService.getLatestReleasedCommit(repo);
			var previous = releaseService.get(repo.path(), commitId).getTags();
			save(repo, commitId, release);
			if (!before.id.equals(commitId))
				return;
			if (!new HashSet<>(release.getTags()).equals(new HashSet<>(previous))) {
				indexService.updatePublicTagsAsync(RepositoryPath.of(group, name));
			}
		}
	}

	private void save(Repository repo, String commitId, ReleaseInfo release) {
		if (!settings.is(ServerSetting.RELEASES_ENABLED))
			throw Response.unavailable("Release feature not enabled");
		if (Strings.nullOrEmpty(release.label))
			throw Response.badRequest("label", "Missing input");
		if (Strings.nullOrEmpty(release.version))
			throw Response.badRequest("version", "Missing input");
		var commit = checkAccess(repo, commitId);
		release.repositoryPath = repo.path();
		release.commitId = commitId;
		var fromDb = service.get(repo.path(), commitId);
		if (fromDb == null) {
			service.insert(release);
			repoService.generateCachedJson(repo, commitId, libraryService.getLinkedLibraries(repo, commit));
		} else {
			release.id = fromDb.id;
			service.update(release);
		}
	}

	@DeleteMapping("{group}/{name}/{commitId}")
	public void revokeRelease(@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId) {
		try (var repo = repoService.get(group, name)) {
			checkAccess(repo, commitId);
			if (!service.isReleased(repo.path(), commitId))
				throw Response.notFound("Commit " + commitId + " is not released");
			var before = historyService.getLatestReleasedCommit(repo);
			var release = service.get(repo.path(), commitId);
			service.delete(release);
			repoService.deleteCachedJson(repo, commitId);
			var after = historyService.getLatestReleasedCommit(repo);
			if (after == null || !after.id.equals(before.id)) {
				indexService.indexPublicAsync(RepositoryPath.of(repo.path()), before, after);
			}
		}
	}

	private Commit checkAccess(Repository repo, String commitId) {
		var commit = historyService.getAccessibleCommit(repo, commitId);
		if (commit == null)
			throw Response.notFound("Commit " + commitId + " not found");
		if (!permissions.canCreateReleasesIn(repo.path()))
			throw Response.forbidden(repo.path(), Permission.MANAGE_RELEASES);
		return commit;
	}

}
