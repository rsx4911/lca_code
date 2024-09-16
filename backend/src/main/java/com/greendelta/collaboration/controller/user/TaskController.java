package com.greendelta.collaboration.controller.user;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Tasks;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;

@RestController
@RequestMapping("ws/task/general")
public class TaskController {

	private final TaskService service;
	private final UserService userService;
	private final RepositoryService repoService;
	private final PermissionsService permissions;
	private final SettingsService settings;

	public TaskController(TaskService service, UserService userService, RepositoryService repoService,
			PermissionsService permissions, SettingsService settings) {
		this.service = service;
		this.userService = userService;
		this.repoService = repoService;
		this.permissions = permissions;
		this.settings = settings;
	}

	@GetMapping
	public Map<String, Object> getAll() {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		try (var accessible = repoService.getAllAccessible()) {
			var repositories = accessible.stream()
					.collect(Collectors.toMap(Repository::path, repo -> repo));
			var user = userService.getCurrentUser();
			var result = Maps.create();
			result.put("tasks", service.getAllFor(user).stream()
					.map(task -> Tasks.map(task, repositories.get(task.repositoryPath)))
					.toList());
			var canCreateTasks = false;
			for (var repo : repositories.values()) {
				if (!permissions.canManageTaskIn(repo.path()))
					continue;
				canCreateTasks = true;
				break;
			}
			result.put("canCreateTasks", canCreateTasks);
			return result;
		}
	}

}
