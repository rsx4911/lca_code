package websocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;
import com.greendelta.cloud.service.MessagingService;
import com.greendelta.cloud.service.MessagingService.ConversationDescriptor;
import com.greendelta.cloud.service.TeamService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Collections;
import com.greendelta.cloud.webservice.util.Messages;

@ServerEndpoint(value = "/sockets/messaging", configurator = WebsocketConfigurator.class)
public class MessagingEndpoint {

	// username->sessionId(s)
	private static volatile Map<String, Set<String>> online = new HashMap<>();

	private final MessagingService service;
	private final UserService userService;
	private final TeamService teamService;

	@Inject
	public MessagingEndpoint(MessagingService service, UserService userService, TeamService teamService) {
		this.service = service;
		this.userService = userService;
		this.teamService = teamService;
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		User user = getUser(config);
		boolean wasConnected = Collections.addToSet(online, user.username, session.getId()).size() > 1;
		if (wasConnected)
			return;
		notifyConnected(session, user);
	}

	private User getUser(EndpointConfig config) {
		Subject subject = (Subject) config.getUserProperties().get("subject");
		String username = subject.getPrincipal().toString();
		return userService.getForUsername(username);
	}

	private void notifyConnected(Session session, User user) {
		for (ConversationDescriptor conversation : service.getConversations(user)) {
			Message lastMessage = conversation.lastMessage;
			if (lastMessage.team != null)
				continue;
			User other = user.equals(lastMessage.from) ? lastMessage.to : lastMessage.from;
			if (!online.containsKey(other.username))
				continue;
			send(session, new Event(EventType.CONNECTED, other.username));
		}
		broadcast(session, new Event(EventType.CONNECTED, user.username));
	}

	@OnMessage
	public void onMessage(String value, Session session) {
		Gson gson = new Gson();
		Event event = gson.fromJson(value, Event.class);
		switch (event.type) {
		case NEW_MESSAGE:
			onNewMessage(session, gson.fromJson(event.data.toString(), NewMessage.class));
			break;
		case MESSAGE_READ:
			onMessageRead(session, gson.fromJson(event.data.toString(), Recipient.class));
			break;
		case IS_ONLINE:
			onPingUser(session, gson.fromJson(event.data.toString(), Recipient.class));
			break;
		default:
			break;
		}
	}

	private void onNewMessage(Session session, NewMessage data) {
		User from = getUser(session);
		if ("team".equals(data.to.type)) {
			Team team = teamService.getForTeamname(data.to.id);
			insertAndSendMessage(session, from, team, data.text);
		} else {
			User to = userService.getForUsername(data.to.id);
			insertAndSendMessage(session, from, to, data.text);
		}
	}

	private User getUser(Session session) {
		if (session == null || !session.isOpen())
			return null;
		for (String key : online.keySet())
			if (online.get(key).contains(session.getId()))
				return userService.getForUsername(key);
		return null;
	}

	private void insertAndSendMessage(Session session, User from, User to, String text) {
		Message message = createMessage(from, to, null, text);
		notifyNewMessage(session, message, from);
		if (!online.containsKey(to.username))
			return;
		notifyNewMessage(session, message, to);
	}

	private void insertAndSendMessage(Session session, User from, Team team, String text) {
		for (User user : team.users) {
			Message message = createMessage(from, user, team, text);
			if (!online.containsKey(user.username))
				continue;
			notifyNewMessage(session, message, user);
		}
	}

	private Message createMessage(User from, User to, Team team, String text) {
		Message message = new Message();
		message.from = from;
		message.to = to;
		message.team = team;
		message.text = text;
		message.date = Calendar.getInstance().getTime();
		message.read = from.equals(to) ? message.date : null;
		return service.insert(message);
	}

	private void notifyNewMessage(Session session, Message message, User user) {
		Session[] sessions = getSessions(session, online.get(user.username));
		send(sessions, new Event(EventType.NEW_MESSAGE, Messages.map(message)));
	}

	private void onMessageRead(Session session, Recipient data) {
		User user = getUser(session);
		if ("team".equals(data.type)) {
			Team team = teamService.getForTeamname(data.id);
			service.markAsRead(user, team);
		} else {
			User other = userService.getForUsername(data.id);
			service.markAsRead(user, other);
			if (!online.containsKey(other.username))
				return;
			notifyMessageRead(session, user, other);
		}
	}

	private void notifyMessageRead(Session session, User recipient, User sender) {
		Session[] sessions = getSessions(session, online.get(sender.username));
		send(sessions, new Event(EventType.MESSAGE_READ, recipient.username));
	}

	private void onPingUser(Session session, Recipient user) {
		boolean isOnline = online.containsKey(user.id);
		if (!isOnline)
			return;
		User self = getUser(session);
		Session[] sessions = getSessions(session, online.get(self.username));
		send(sessions, new Event(EventType.IS_ONLINE, user.id));
	}

	@OnClose
	public void onClose(Session session) {
		String username = Collections.remove(online, session.getId());
		if (username == null)
			return;
		broadcast(session, new Event(EventType.DISCONNECTED, username));
	}

	private void broadcast(Session session, Event event) {
		if (session == null || !session.isOpen())
			return;
		send(session.getOpenSessions(), event);
	}

	private void send(Session session, Event event) {
		send(new Session[] { session }, event);
	}

	private void send(Set<Session> sessions, Event event) {
		send(sessions.toArray(new Session[sessions.size()]), event);
	}

	private void send(Session[] sessions, Event event) {
		if (sessions == null || sessions.length == 0)
			return;
		for (Session s : sessions) {
			if (!s.isOpen())
				continue;
			s.getAsyncRemote().sendText(new Gson().toJson(event));
		}
	}

	private Session[] getSessions(Session session, Set<String> sessionIds) {
		if (session == null || !session.isOpen())
			return null;
		List<Session> sessions = new ArrayList<>();
		for (Session s : session.getOpenSessions())
			if (sessionIds.contains(s.getId()))
				sessions.add(s);
		return sessions.toArray(new Session[sessions.size()]);
	}

	private class Event {

		private final EventType type;
		private final Object data;

		private Event(EventType type, Object data) {
			this.type = type;
			this.data = data;
		}

	}

	private enum EventType {

		CONNECTED, DISCONNECTED, NEW_MESSAGE, MESSAGE_READ, IS_ONLINE;

	}

	private class NewMessage {

		private Recipient to;
		private String text;

	}

	private class Recipient {

		String type;
		String id;

	}

}
