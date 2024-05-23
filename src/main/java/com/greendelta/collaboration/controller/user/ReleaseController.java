package com.greendelta.collaboration.controller.user;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.ReleaseInfo;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.PermissionsService;

@RestController
@RequestMapping("ws/release")
public class ReleaseController {

	private final ReleaseService service;
	private final RepositoryService repoService;
	private final PermissionsService permissions;
	private final SettingsService settings;

	public ReleaseController(ReleaseService service, RepositoryService repoService, PermissionsService permissions, SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.permissions = permissions;
		this.settings = settings;
	}

	@PostMapping("{group}/{name}/{commitId}")
	public void release(@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId) {
		if (!settings.is(ServerSetting.RELEASES_ENABLED))
			throw Response.unavailable("Release feature not enabled");
		try (var repo = repoService.get(group, name)) {
			if (service.isReleased(repo.path(), commitId))
				throw Response.conflict("Commit " + commitId + " is already released");
			if (!permissions.canCreateReleasesIn(repo.path()))
				throw Response.forbidden(repo.path(), Permission.CREATE_RELEASES);
			var release = new ReleaseInfo();
			release.repositoryPath = repo.path();
			release.commitId = commitId;
			// TODO add other info
			service.insert(release);
		}
	}
}
