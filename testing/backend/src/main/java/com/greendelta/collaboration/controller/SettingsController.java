package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ModelTypes;

@RestController
@RequestMapping("ws/public/settings")
public class SettingsController {

	private final SettingsService service;
	private final UserService userService;
	private final RepositoryService repoService;

	public SettingsController(SettingsService service, UserService userService, RepositoryService repoService) {
		this.service = service;
		this.userService = userService;
		this.repoService = repoService;
	}

	@GetMapping
	public Map<String, Object> getServerSettings() {
		var user = userService.getCurrentUser();
		if (user.isAdmin()) {
			try (var repos = repoService.getReleased()) {
				var paths = repos.stream().map(repo -> repo.path()).toList();
				cleanup(ServerSetting.REPOSITORIES_ORDER, paths, true);
				cleanup(ServerSetting.REPOSITORIES_HIDDEN, paths, false);
				cleanup(ServerSetting.MODEL_TYPES_ORDER, ModelTypes.DEFAULT_ORDER, true);
				cleanup(ServerSetting.MODEL_TYPES_HIDDEN, ModelTypes.DEFAULT_ORDER, false);
			}
		}
		var settings = service.serverConfig.toPreservedMap();
		settings.put("SEARCH_AVAILABLE", service.searchConfig.isSearchAvailable());
		settings.put("USAGE_SEARCH_ENABLED", service.searchConfig.isUsageSearchEnabled());
		return settings;
	}

	private void cleanup(ServerSetting key, List<String> values, boolean appendNew) {
		List<String> list = service.get(key, new ArrayList<>());
		var filtered = list.stream().filter(name -> values.contains(name)).collect(Collectors.toList());
		var changed = !filtered.equals(list);
		if (!changed && !appendNew)
			return;
		if (appendNew) {
			var newValues = values.stream().filter(value -> !filtered.contains(value)).toList();
			if (!changed && newValues.isEmpty())
				return;
			filtered.addAll(newValues);
		}
		service.set(key, filtered);
	}

}
