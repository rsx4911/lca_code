package com.greendelta.collaboration.webservice.task;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
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

	@Inject
	public TaskResource(TaskService service, UserService userService, RepositoryService repoService,
			AccessService accessService) {
		this.service = service;
		this.userService = userService;
		this.repoService = repoService;
		this.accessService = accessService;
	}

	@GET
	public Response getAll() {
		User user = userService.getCurrentUser();
		ObjectMap result = new ObjectMap();
		result.put("tasks", Client.map(service.getAllFor(user), Tasks::map));
		boolean canCreateTasks = false;
		for (Repository repo : repoService.getAllAccessible()) {
			if (!accessService.canManageTaskIn(repo.toId()))
				continue;
			canCreateTasks = true;
			break;
		}
		result.put("canCreateTasks", canCreateTasks);
		return Respond.ok(result);
	}

}
