package com.greendelta.collaboration.controller.user;

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
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.util.Maps;

@RestController
@RequestMapping("ws/release")
public class ReleaseController {

	private final ReleaseService service;
	private final RepositoryService repoService;
	private final LibraryService libraryService;
	private final HistoryService historyService;
	private final PermissionsService permissions;
	private final SettingsService settings;

	public ReleaseController(ReleaseService service, RepositoryService repoService, LibraryService libraryService,
			HistoryService historyService, PermissionsService permissions, SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.libraryService = libraryService;
		this.historyService = historyService;
		this.permissions = permissions;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}/{commitId}")
	public Map<String, Object> getReleaseInfo(@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId) {
		try (var repo = repoService.get(group, name)) {
			checkAccess(repo, commitId);
			if (!service.isReleased(repo.path(), commitId))
				throw Response.notFound("Commit " + commitId + " is not released");
			var release = service.get(repo.path(), commitId);
			return Maps.of(release);
		}
	}

	@PostMapping("{group}/{name}/{commitId}")
	public void release(@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId,
			@RequestBody ReleaseInfo release) {
		save(group, name, commitId, release);
	}

	@PutMapping("{group}/{name}/{commitId}")
	public void updateRelease(@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId,
			@RequestBody ReleaseInfo release) {
		save(group, name, commitId, release);
	}

	private void save(String group, String name, String commitId, ReleaseInfo release) {
		if (!settings.is(ServerSetting.RELEASES_ENABLED))
			throw Response.unavailable("Release feature not enabled");
		if (Strings.nullOrEmpty(release.label))
			throw Response.badRequest("label", "Missing input");
		if (Strings.nullOrEmpty(release.version))
			throw Response.badRequest("version", "Missing input");
		try (var repo = repoService.get(group, name)) {
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
	}

	@DeleteMapping("{group}/{name}/{commitId}")
	public void revokeRelease(@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId) {
		try (var repo = repoService.get(group, name)) {
			checkAccess(repo, commitId);
			if (!service.isReleased(repo.path(), commitId))
				throw Response.notFound("Commit " + commitId + " is not released");
			var release = service.get(repo.path(), commitId);
			service.delete(release);
			repoService.deleteCachedJson(repo, commitId);
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
