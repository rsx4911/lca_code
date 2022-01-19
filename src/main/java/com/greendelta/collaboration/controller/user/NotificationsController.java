package com.greendelta.collaboration.controller.user;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Notification;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/notifications")
public class NotificationsController {

	private final UserService userService;
	private final SettingsService settingsService;

	@Autowired
	public NotificationsController(UserService userService, SettingsService settingsService) {
		this.userService = userService;
		this.settingsService = settingsService;
	}

	@GetMapping
	public List<Notification> getEnabled() {
		if (!settingsService.is(ServerSetting.NOTIFICATIONS_ENABLED))
			throw Response.unavailable("Notifications feature not enabled");
		var currentUser = userService.getCurrentUser();
		return Arrays.asList(Notification.values()).stream()
				.filter(n -> currentUser.isEnabled(n))
				.toList();
	}

	@PutMapping("enable/{notifications}")
	public void enable(@PathVariable("notifications") String notifications) {
		if (!settingsService.is(ServerSetting.NOTIFICATIONS_ENABLED))
			throw Response.unavailable("Notifications feature not enabled");
		var currentUser = userService.getCurrentUser();
		parse(notifications).forEach(notification -> currentUser.enable(notification));
		userService.update(currentUser);
	}

	@PutMapping("disable/{notifications}")
	public void disable(@PathVariable("notifications") String notifications) {
		if (!settingsService.is(ServerSetting.NOTIFICATIONS_ENABLED))
			throw Response.unavailable("Notifications feature not enabled");
		var currentUser = userService.getCurrentUser();
		parse(notifications).forEach(notification -> currentUser.disable(notification));
		userService.update(currentUser);
	}

	private List<Notification> parse(String value) {
		return Arrays.asList(value.split(",")).stream()
				.map(v -> Notification.valueOf(v))
				.toList();
	}

}
