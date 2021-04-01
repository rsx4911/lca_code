package com.greendelta.collaboration.webservice.user;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Tasks;

@Path("task/general")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

	private final TaskService service;
	private final UserService userService;
	private final RepositoryService repoService;
	private final AccessService accessService;
	private final SettingsService settingsService;

	@Inject
	public TaskResource(TaskService service, UserService userService, RepositoryService repoService,
			AccessService accessService, SettingsService settingsService) {
		this.service = service;
		this.userService = userService;
		this.repoService = repoService;
		this.accessService = accessService;
		this.settingsService = settingsService;
	}

	@GET
	public Response getAll() {
		if (!settingsService.is(ServerSetting.TASKS_ENABLED))
			return Respond.status(Status.SERVICE_UNAVAILABLE, "Task feature not enabled");
		Map<String, Repository> repositories = Collections.map(repoService.getAllAccessible(), repo -> repo.toId());
		User user = userService.getCurrentUser();
		ObjectMap result = new ObjectMap();
		result.put("tasks", Client.map(service.getAllFor(user),
				task -> Tasks.map(task, repositories.get(task.repositoryPath))));
		boolean canCreateTasks = false;
		for (Repository repo : repositories.values()) {
			if (!accessService.canManageTaskIn(repo.toId()))
				continue;
			canCreateTasks = true;
			break;
		}
		result.put("canCreateTasks", canCreateTasks);
		return Respond.ok(result);
	}

}
