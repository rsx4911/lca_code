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
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;
import com.greendelta.cloud.service.MessageService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.mapper.MessageMapper;

@ServerEndpoint(value = "/sockets/messages", configurator = WebsocketConfigurator.class)
public class MessageEndpoint {

	// username->sessionId(s)
	private static volatile Map<String, Set<String>> online = new HashMap<>();

	private final MessageService service;
	private final UserService userService;

	@Inject
	public MessageEndpoint(MessageService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		User user = getUser(config);
		Set<String> ids = online.get(user.username);
		if (ids == null) {
			ids = new HashSet<>();
			online.put(user.username, ids);
		}
		ids.add(session.getId());
		broadcast(session, new Event(EventType.CONNECTED, user.username));
	}

	private User getUser(EndpointConfig config) {
		Subject subject = (Subject) config.getUserProperties().get("subject");
		String username = subject.getPrincipal().toString();
		return userService.getForUsername(username);
	}

	@OnMessage
	public void onMessage(String data, Session session, EndpointConfig config) {
		Gson gson = new Gson();
		Event event = gson.fromJson(data, Event.class);
		if (event.type == EventType.NEW_MESSAGE) {
			String[] content = event.data.toString().split(";");
			User from = getUser(session);
			User to = userService.getForUsername(content[0]);
			Message message = new Message();
			message.date = Calendar.getInstance().getTime();
			message.from = from;
			message.to = to;
			message.text = content[1];
			message.unread = true;
			message = service.insert(message);
			MessageMapper mapper = new MessageMapper();
			send(new Event(EventType.NEW_MESSAGE, mapper.map(message)),
					getSessions(session, online.get(from.username)));
			if (online.containsKey(to.username))
				send(new Event(EventType.NEW_MESSAGE, mapper.map(message)),
						getSessions(session, online.get(to.username)));
		}
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

}
