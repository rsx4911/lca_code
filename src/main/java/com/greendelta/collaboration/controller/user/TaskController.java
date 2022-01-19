package com.greendelta.collaboration.controller.user;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Tasks;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;

@RestController
@RequestMapping("ws/task/general")
public class TaskController {

	private final TaskService service;
	private final UserService userService;
	private final RepositoryService repoService;
	private final AccessService accessService;
	private final SettingsService settingsService;

	@Autowired
	public TaskController(TaskService service, UserService userService, RepositoryService repoService,
			AccessService accessService, SettingsService settingsService) {
		this.service = service;
		this.userService = userService;
		this.repoService = repoService;
		this.accessService = accessService;
		this.settingsService = settingsService;
	}

	@GetMapping
	public Map<String, Object> getAll() {
		if (!settingsService.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		try (var accessible = repoService.getAllAccessible()) {
			var repositories = accessible.stream()
					.collect(Collectors.toMap(repo -> repo.path(), repo -> repo));
			var user = userService.getCurrentUser();
			var result = new ObjectMap();
			result.put("tasks", service.getAllFor(user).stream()
					.map(task -> Tasks.map(task, repositories.get(task.repositoryPath)))
					.toList());
			var canCreateTasks = false;
			for (var repo : repositories.values()) {
				if (!accessService.canManageTaskIn(repo.path()))
					continue;
				canCreateTasks = true;
				break;
			}
			result.put("canCreateTasks", canCreateTasks);
			return result;
		}
	}

}
