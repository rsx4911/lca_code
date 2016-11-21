package websocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
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
import com.greendelta.cloud.service.MessageService;
import com.greendelta.cloud.service.TeamService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.mapper.MessageMapper;

@ServerEndpoint(value = "/sockets/messages", configurator = WebsocketConfigurator.class)
public class MessageEndpoint {

	// username->sessionId(s)
	private static volatile Map<String, Set<String>> online = new HashMap<>();

	private final MessageService service;
	private final UserService userService;
	private final TeamService teamService;

	@Inject
	public MessageEndpoint(MessageService service, UserService userService, TeamService teamService) {
		this.service = service;
		this.userService = userService;
		this.teamService = teamService;
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		Subject subject = (Subject) config.getUserProperties().get("subject");
		String username = subject.getPrincipal().toString();
		User user = userService.getForUsername(username);
		Set<String> ids = online.get(user.username);
		if (ids == null) {
			ids = new HashSet<>();
			online.put(user.username, ids);
		}
		ids.add(session.getId());
		broadcast(session, new Event(EventType.CONNECTED, user.username));
	}

	@OnMessage
	public void onMessage(String value, Session session) {
		Gson gson = new Gson();
		Event event = gson.fromJson(value, Event.class);
		if (event.type != EventType.NEW_MESSAGE)
			return;
		NewMessage data = gson.fromJson(event.data.toString(), NewMessage.class);
		User from = getUser(session);
		User to = "user".equals(data.to.type) ? userService.getForUsername(data.to.id) : null;
		Team team = "team".equals(data.to.type) ? teamService.getForTeamname(data.to.id) : null;
		insertAndSendMessage(session, from, to, team, data.text);
	}

	private void insertAndSendMessage(Session session, User from, User to, Team team, String text) {
		if (team == null) {
			Message message = createMessage(from, to, null, text);
			Session[] sessions = getSessions(session, online.get(from.username));
			send(new Event(EventType.NEW_MESSAGE, new MessageMapper().map(message)), sessions);
			if (!online.containsKey(to.username))
				return;
			sessions = getSessions(session, online.get(message.to.username));
			send(new Event(EventType.NEW_MESSAGE, new MessageMapper().map(message)), sessions);
			return;
		}
		for (User user : team.users) {
			Message message = createMessage(from, user, team, text);
			if (!online.containsKey(user.username))
				continue;
			Session[] sessions = getSessions(session, online.get(user.username));
			send(new Event(EventType.NEW_MESSAGE, new MessageMapper().map(message)), sessions);
		}
	}

	private Message createMessage(User from, User to, Team team, String text) {
		Message message = new Message();
		message.from = from;
		message.to = to;
		message.team = team;
		message.text = text;
		message.date = Calendar.getInstance().getTime();
		message.unread = !from.equals(to);
		return service.insert(message);
	}

	@OnClose
	public void onClose(Session session) {
		String username = null;
		for (String key : new ArrayList<>(online.keySet())) {
			Set<String> ids = online.get(key);
			if (!ids.contains(session.getId()))
				continue;
			ids.remove(session.getId());
			if (!ids.isEmpty())
				continue;
			username = key;
			online.remove(key);
		}
		if (username == null)
			return;
		broadcast(session, new Event(EventType.DISCONNECTED, username));
	}

	private void broadcast(Session session, Event event) {
		if (session == null || !session.isOpen())
			return;
		for (Session s : session.getOpenSessions()) {
			s.getAsyncRemote().sendText(new Gson().toJson(event));
		}
	}

	private void send(Event event, Session... sessions) {
		if (sessions == null || sessions.length == 0)
			return;
		for (Session session : sessions) {
			if (!session.isOpen())
				continue;
			session.getAsyncRemote().sendText(new Gson().toJson(event));
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

	private User getUser(Session session) {
		if (session == null || !session.isOpen())
			return null;
		for (String key : online.keySet())
			if (online.get(key).contains(session.getId()))
				return userService.getForUsername(key);
		return null;
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

		CONNECTED, DISCONNECTED, NEW_MESSAGE;

	}

	private class NewMessage {

		private Recipient to;
		private String text;

		private class Recipient {

			String type;
			String id;

		}

	}

}
