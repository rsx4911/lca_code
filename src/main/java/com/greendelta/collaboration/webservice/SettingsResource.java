package com.greendelta.collaboration.webservice;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ModelTypes;

@Path("public/settings")
@Produces(MediaType.APPLICATION_JSON)
public class SettingsResource {

	private final SettingsService service;
	private final UserService userService;
	private final RepositoryService repoService;

	@Inject
	public SettingsResource(SettingsService service, UserService userService, RepositoryService repoService) {
		this.service = service;
		this.userService = userService;
		this.repoService = repoService;
	}

	@GET
	public Response getServerSettings() {
		User user = userService.getCurrentUser();
		boolean isAdmin = user != null && user.isAdmin();
		if (isAdmin) {
			List<String> repos = Collections.convertToList(repoService.getPublic(), repo -> repo.toId());
			cleanup(ServerSetting.REPOSITORIES_ORDER, repos, true);
			cleanup(ServerSetting.REPOSITORIES_HIDDEN, repos, false);
			cleanup(ServerSetting.MODEL_TYPES_ORDER, ModelTypes.DEFAULT_ORDER, true);
			cleanup(ServerSetting.MODEL_TYPES_HIDDEN, ModelTypes.DEFAULT_ORDER, false);
		}
		Map<String, Object> serverConfig = service.serverConfig.toMap(setting -> isAdmin || setting.isPublic());
		return Respond.ok(serverConfig);
	}

	private void cleanup(ServerSetting key, List<String> values, boolean appendNew) {
		List<String> list = service.get(key);
		List<String> filtered = Collections.filter(list, name -> !values.contains(name));
		boolean changed = !filtered.equals(list);
		if (!changed && !appendNew)
			return;
		if (appendNew) {
			List<String> newValues = Collections.filter(values, value -> filtered.contains(value));
			if (!changed && newValues.isEmpty())
				return;
			filtered.addAll(newValues);
		}
		service.set(key, filtered);
	}

}
