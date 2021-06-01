package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;

public class MessagingService {

	private final Dao<Message> dao;
	private final UserService userService;
	private final TeamService teamService;

	@Inject
	public MessagingService(Dao<Message> dao, UserService userService, TeamService teamService) {
		this.dao = dao;
		this.userService = userService;
		this.teamService = teamService;
	}

	public Message insert(Message message) {
		return dao.insert(message);
	}

	public void delete(Message message) {
		dao.delete(message);
	}

	public List<ConversationDescriptor> getConversations(User user) {
		String jpql = "SELECT m FROM Message m WHERE ((m.from = :user AND m.team IS NULL) OR m.to = :user) ORDER BY m.date DESC";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		List<Message> all = dao.getAll(jpql, attributes);
		Map<String, ConversationDescriptor> conversations = new HashMap<>();
		for (Message message : all) {
			String key = getKey(user, message);
			ConversationDescriptor conversation = conversations.get(key);
			if (conversation == null) {
				conversations.put(key, conversation = new ConversationDescriptor(message));
			}
			if (message.read == null && !message.from.equals(user)) {
				conversation.unreadMessages++;
			}
		}
		return new ArrayList<>(conversations.values());
	}

	private String getKey(User user, Message message) {
		if (message.team != null)
			return "team-" + message.team.id;
		User with = message.from.equals(user) ? message.to : message.from;
		return "user-" + with.id;
	}

	public List<Message> getMessages(User user, User with, int limit, Date before) {
		String jpql = "SELECT m FROM Message m WHERE ((m.from = :user AND m.to = :with) OR (m.to = :user AND m.from = :with)) AND m.team IS NULL ";
		if (before != null)
			jpql += "AND m.date < :before ";
		jpql += "ORDER BY m.date DESC";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("with", with);
		if (before != null)
			attributes.put("before", before);
		List<Message> messages = dao.getAll(jpql, attributes, 0, limit);
		Collections.sort(messages, new MessageSorter());
		return messages;
	}

	public List<Message> getMessages(User user) {
		String jpql = "SELECT m FROM Message m WHERE m.from = :user OR m.to = :user";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		return dao.getAll(jpql, attributes);
	}

	public List<Message> getMessages(Team team) {
		String jpql = "SELECT m FROM Message m WHERE m.team = :team";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("team", team);
		return dao.getAll(jpql, attributes);
	}

	public List<Message> getMessages(User user, Team team, int limit, Date before) {
		String jpql = "SELECT m FROM Message m WHERE m.team = :team AND m.to = :user ";
		if (before != null)
			jpql += "AND m.date < :before ";
		jpql += "ORDER BY m.date DESC";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("team", team);
		attributes.put("user", user);
		if (before != null)
			attributes.put("before", before);
		List<Message> messages = dao.getAll(jpql, attributes, 0, limit);
		Collections.sort(messages, new MessageSorter());
		return messages;
	}

	public void markAsRead(User user, User with) {
		String jpql = "SELECT m FROM Message m WHERE m.to = :user AND m.from = :with AND m.team IS NULL AND m.read IS NULL";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("with", with);
		List<Message> messages = dao.getAll(jpql, attributes);
		for (Message message : messages) {
			message.read = Calendar.getInstance().getTime();
		}
		dao.update(messages);
	}

	public void markAsRead(User user, Team team) {
		String jpql = "SELECT m FROM Message m WHERE m.to = :user AND m.team = :team AND m.read IS NULL";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("team", team);
		List<Message> messages = dao.getAll(jpql, attributes);
		for (Message message : messages) {
			message.read = Calendar.getInstance().getTime();
		}
		dao.update(messages);
	}

	public List<User> filterUsers(List<User> users) {
		User currentUser = userService.getCurrentUser();
		if (currentUser.isUserManager())
			return users;
		List<Team> teams = teamService.getTeamsFor(currentUser);
		return com.greendelta.collaboration.util.Collections.filter(users, (user) -> {
			if (currentUser.settings.blockedUsers.contains(user))
				return true;
			if (!user.settings.messagingEnabled)
				return true;
			if (!user.settings.messagingRestricted)
				return false;
			for (Team team : teams)
				if (team.users.contains(user))
					return false;
			return true;
		});
	}

	public class ConversationDescriptor {

		public final Message lastMessage;
		public int unreadMessages;

		public ConversationDescriptor(Message lastMessage) {
			this.lastMessage = lastMessage;
		}

	}

	private class MessageSorter implements Comparator<Message> {

		@Override
		public int compare(Message m1, Message m2) {
			return Long.compare(m1.date.getTime(), m2.date.getTime());
		}

	}

}
