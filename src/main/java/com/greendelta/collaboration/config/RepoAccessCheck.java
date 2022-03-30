package com.greendelta.collaboration.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.config.filter.git.GitRequest;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.util.Requests;

@Component
public class RepoAccessCheck {

	private final AccessService accessService;
	private final SettingsService settingsService;
	private final SessionService sessionService;
	private final GitFilterConfig gitFilterConfig;

	@Autowired
	public RepoAccessCheck(AccessService accessService, SettingsService settingsService, SessionService sessionService,
			GitFilterConfig gitFilterConfig) {
		this.accessService = accessService;
		this.settingsService = settingsService;
		this.sessionService = sessionService;
		this.gitFilterConfig = gitFilterConfig;
	}

	public boolean canAccess(HttpServletRequest request) throws ServletException, IOException {
		var path = new RepositoryPath(Requests.getRelativePath(request));
		if (!path.isGroupOrRepo())
			return true;
		if (!gitFilterConfig.isGitUrl(request))
			return accessService.canRead(path.toString());
		return canGitAccess(new GitRequest(request), path.toString());
	}

	private boolean canGitAccess(GitRequest request, String repoId) throws IOException, ServletException {
		var loggedIn = request.basicHttpLogin(sessionService);
		var canAccess = false;
		if (request.isGitPush()) {
			canAccess = accessService.canWrite(repoId) && !areCommitsProhibited(repoId);
		} else {
			canAccess = accessService.canRead(repoId);
		}
		if (loggedIn) {
			request.basicHttpLogout(sessionService);
		}
		return canAccess;
	}

	private boolean areCommitsProhibited(String repoId) {
		return settingsService.get(SettingType.REPOSITORY_SETTING, repoId, accessService::canSetSettings)
				.is(RepositorySetting.PROHIBIT_COMMITS);
	}

}
