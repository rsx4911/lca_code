package com.greendelta.collaboration.controller.user;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Conversations;
import com.greendelta.collaboration.controller.util.Messages;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.UserSettings;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/messaging")
public class MessagingController {

	private final MessagingService service;
	private final UserService userService;
	private final TeamService teamService;
	private final SettingsService settings;

	public MessagingController(MessagingService service, UserService userService, TeamService teamService,
			SettingsService settings) {
		this.service = service;
		this.userService = userService;
		this.teamService = teamService;
		this.settings = settings;
	}

	@GetMapping
	public List<Map<String, Object>> getConversations() {
		if (!settings.is(ServerSetting.MESSAGING_ENABLED))
			throw Response.unavailable("Messaging feature not enabled");
		var user = userService.getCurrentUser();
		var conversations = service.getConversations(user);
		return Conversations.map(conversations, user);
	}

	@GetMapping("user/{username}")
	public ResponseEntity<List<Map<String, Object>>> getMessages(
			@PathVariable String username,
			@RequestParam(required = false) long before) {
		if (!settings.is(ServerSetting.MESSAGING_ENABLED))
			throw Response.unavailable("Messaging feature not enabled");
		var user = userService.getCurrentUser();
		var other = userService.getForUsername(username);
		if (user.settings.blockedUsers.contains(other))
			return Response.noContent();
		var cal = Calendar.getInstance();
		if (before > 0) {
			cal.setTimeInMillis(before);
		}
		var conversation = service.getMessages(user, other, 20, before > 0 ? cal.getTime() : null);
		if (conversation.isEmpty())
			return Response.noContent();
		return Response.ok(Messages.map(conversation, user));
	}

	@GetMapping("team/{teamname}")
	public ResponseEntity<List<Map<String, Object>>> getTeamMessages(
			@PathVariable String teamname,
			@RequestParam(required = false) long before) {
		if (!settings.is(ServerSetting.MESSAGING_ENABLED))
			throw Response.unavailable("Messaging feature not enabled");
		var team = teamService.getForTeamname(teamname);
		var cal = Calendar.getInstance();
		if (before > 0) {
			cal.setTimeInMillis(before);
		}
		var user = userService.getCurrentUser();
		var conversation = service.getMessages(user, team, 20, before > 0 ? cal.getTime() : null);
		if (conversation.isEmpty())
			return Response.noContent();
		return Response.ok(Messages.map(conversation, user));
	}

	@PutMapping("settings")
	public UserSettings updateSettings(@RequestBody UserSettings userSettings) {
		if (!settings.is(ServerSetting.MESSAGING_ENABLED))
			throw Response.unavailable("Messaging feature not enabled");
		var currentUser = userService.getCurrentUser();
		currentUser.settings.messagingEnabled = userSettings.messagingEnabled;
		currentUser.settings.showOnlineStatus = userSettings.showOnlineStatus;
		currentUser.settings.showReadReceipt = userSettings.showReadReceipt;
		currentUser = userService.update(currentUser);
		return currentUser.settings;
	}

	@PutMapping("block/{username}")
	public void blockUser(@PathVariable String username) {
		if (!settings.is(ServerSetting.MESSAGING_ENABLED))
			throw Response.unavailable("Messaging feature not enabled");
		var other = userService.getForUsername(username);
		if (other == null)
			throw Response.notFound();
		var currentUser = userService.getCurrentUser();
		currentUser.settings.blockedUsers.add(other);
		currentUser = userService.update(currentUser);
	}

	@PutMapping("unblock/{username}")
	public void unblockUser(@PathVariable String username) {
		if (!settings.is(ServerSetting.MESSAGING_ENABLED))
			throw Response.unavailable("Messaging feature not enabled");
		var other = userService.getForUsername(username);
		if (other == null)
			throw Response.notFound();
		var currentUser = userService.getCurrentUser();
		currentUser.settings.blockedUsers.remove(other);
		currentUser = userService.update(currentUser);
	}

}
