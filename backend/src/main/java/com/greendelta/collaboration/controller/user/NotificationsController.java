package com.greendelta.collaboration.controller.user;

import java.util.Arrays;
import java.util.List;

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
	private final SettingsService settings;

	public NotificationsController(UserService userService, SettingsService settings) {
		this.userService = userService;
		this.settings = settings;
	}

	@GetMapping
	public List<Notification> getEnabled() {
		if (!settings.is(ServerSetting.NOTIFICATIONS_ENABLED))
			throw Response.unavailable("Notifications feature not enabled");
		var currentUser = userService.getCurrentUser();
		return Arrays.asList(Notification.values()).stream()
				.filter(n -> currentUser.settings.isEnabled(n))
				.toList();
	}

	@PutMapping("enable/{notifications}")
	public void enable(@PathVariable String notifications) {
		if (!settings.is(ServerSetting.NOTIFICATIONS_ENABLED))
			throw Response.unavailable("Notifications feature not enabled");
		var currentUser = userService.getCurrentUser();
		parse(notifications).forEach(notification -> currentUser.settings.enable(notification));
		userService.update(currentUser);
	}

	@PutMapping("disable/{notifications}")
	public void disable(@PathVariable String notifications) {
		if (!settings.is(ServerSetting.NOTIFICATIONS_ENABLED))
			throw Response.unavailable("Notifications feature not enabled");
		var currentUser = userService.getCurrentUser();
		parse(notifications).forEach(notification -> currentUser.settings.disable(notification));
		userService.update(currentUser);
	}

	private List<Notification> parse(String value) {
		return Arrays.asList(value.split(",")).stream()
				.map(v -> Notification.valueOf(v))
				.toList();
	}

}
