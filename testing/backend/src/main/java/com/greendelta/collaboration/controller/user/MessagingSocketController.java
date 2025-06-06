package com.greendelta.collaboration.controller.user;

import java.security.Principal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.openlca.util.Strings;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.greendelta.collaboration.controller.util.Messages;
import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

@Controller
public class MessagingSocketController {

	private final Map<String, Set<String>> sessions = new HashMap<>();
	private final SimpMessagingTemplate messaging;
	private final MessagingService service;
	private final UserService userService;
	private final TeamService teamService;

	public MessagingSocketController(SimpMessagingTemplate messaging, MessagingService service, UserService userService,
			TeamService teamService) {
		this.messaging = messaging;
		this.service = service;
		this.userService = userService;
		this.teamService = teamService;
	}

	@MessageMapping("/new-message")
	public void newMessage(@Payload NewMessage message, Principal principal) {
		var from = getUser(principal);
		if (from == null)
			return;
		if ("team".equals(message.to.type)) {
			var team = teamService.getForTeamname(message.to.id);
			insertAndSendMessage(from, team, message.text);
		} else {
			var to = userService.getForUsername(message.to.id);
			insertAndSendMessage(from, to, message.text);
		}
	}

	private void insertAndSendMessage(User from, User to, String text) {
		var message = createMessage(from, to, null, text);
		notifyNewMessage(from, message);
		if (!sessions.containsKey(to.username))
			return;
		if (to.settings.blockedUsers.contains(from))
			return;
		notifyNewMessage(to, message);
	}

	private void insertAndSendMessage(User from, Team team, String text) {
		for (var user : team.users) {
			var message = createMessage(from, user, team, text);
			if (!sessions.containsKey(user.username))
				continue;
			notifyNewMessage(user, message);
		}
	}

	private Message createMessage(User from, User to, Team team, String text) {
		var message = new Message();
		message.from = from;
		message.to = to;
		message.team = team;
		message.text = text;
		message.date = Calendar.getInstance().getTime();
		message.readDate = from.equals(to) ? message.date : null;
		message.showReadReceipt = to.settings.showReadReceipt;
		return service.insert(message);
	}

	@MessageMapping("/mark-as-read")
	public void markAsRead(@Payload Recipient recipient, Principal principal) {
		var user = getUser(principal);
		if (user == null)
			return;
		if ("team".equals(recipient.type)) {
			var team = teamService.getForTeamname(recipient.id);
			service.markAsRead(user, team);
		} else {
			var hasRead = userService.getForUsername(recipient.id);
			service.markAsRead(user, hasRead);
			if (!sessions.containsKey(hasRead.username))
				return;
			if (!user.settings.showReadReceipt)
				return;
			notifyMarkAsRead(user, hasRead);
		}
	}

	@MessageMapping("/is-online")
	public void isOnline(@Payload Recipient recipient, Principal principal) {
		var user = getUser(principal);
		if (user == null)
			return;
		var isOnline = sessions.containsKey(recipient.id);
		if (!isOnline)
			return;
		var pinged = userService.getForUsername(recipient.id);
		if (!pinged.settings.showOnlineStatus)
			return;
		if (pinged.settings.blockedUsers.contains(user))
			return;
		notifyConnected(user, pinged);
	}

	private void notifyNewMessage(User toNotify, Message message) {
		send(toNotify, "new-message", Messages.map(message, message.from));
	}

	private void notifyMarkAsRead(User toNotify, User hasRead) {
		send(toNotify, "mark-as-read", hasRead.username);
	}

	private void notifyConnected(User toNotify, User connected) {
		send(toNotify, "connected", connected.username);
	}

	private void notifyDisconnected(User toNotify, User disconnected) {
		send(toNotify, "disconnected", disconnected.username);
	}

	private void notifyConnectionStateChanged(User user, BiConsumer<User, User> notifier) {
		if (!user.settings.showOnlineStatus)
			return;
		for (var conversation : service.getConversations(user)) {
			var lastMessage = conversation.lastMessage;
			if (lastMessage.team != null)
				continue;
			var other = user.equals(lastMessage.from) ? lastMessage.to : lastMessage.from;
			if (!sessions.containsKey(other.username))
				continue;
			if (user.settings.blockedUsers.contains(other))
				continue;
			notifier.accept(other, user);
		}
	}

	private void send(User user, String destination, Object data) {
		if (!sessions.containsKey(user.username))
			return;
		for (var session : sessions.get(user.username)) {
			messaging.convertAndSendToUser(session, destination, data);
		}
	}

	private User getUser(Principal principal) {
		if (principal == null || Strings.nullOrEmpty(principal.getName()))
			return null;
		var user = userService.getForUsername(principal.getName());
		if (user == null || user.isAnonymous())
			return null;
		return user;
	}

	@Component
	public class OnConnect implements ApplicationListener<SessionConnectedEvent> {

		@Override
		public void onApplicationEvent(SessionConnectedEvent event) {
			var user = getUser(event.getUser());
			if (user == null)
				return;
			var session = SimpAttributesContextHolder.currentAttributes().getSessionId();
			var wasConnected = sessions.containsKey(user.username) && !sessions.get(user.username).isEmpty();
			sessions.computeIfAbsent(user.username, u -> new HashSet<>()).add(session);
			if (wasConnected || !user.settings.showOnlineStatus)
				return;
			notifyConnectionStateChanged(user, MessagingSocketController.this::notifyConnected);
		}

	}

	@Component
	public class OnDisconnect implements ApplicationListener<SessionDisconnectEvent> {

		@Override
		public void onApplicationEvent(SessionDisconnectEvent event) {
			var user = getUser(event.getUser());
			if (user == null)
				return;
			var userSessions = sessions.get(user.username);
			if (userSessions == null)
				return;
			userSessions.remove(event.getSessionId());
			if (userSessions.isEmpty()) {
				sessions.remove(user.username);
			}
			notifyConnectionStateChanged(user, MessagingSocketController.this::notifyDisconnected);
		}

	}

	private static class NewMessage {

		public Recipient to;
		public String text;

	}

	private static class Recipient {

		public String type;
		public String id;

	}
}
