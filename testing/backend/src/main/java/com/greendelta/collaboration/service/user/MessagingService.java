package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;

@Service
public class MessagingService {

	private final Dao<Message> dao;
	private final UserService userService;

	public MessagingService(Dao<Message> dao, UserService userService) {
		this.dao = dao;
		this.userService = userService;
	}

	public Message insert(Message message) {
		return dao.insert(message);
	}

	public void delete(Message message) {
		dao.delete(message);
	}

	public List<ConversationDescriptor> getConversations(User user) {
		var jpql = "SELECT m FROM Message m WHERE ((m.from = :user AND m.team IS NULL) OR m.to = :user) ORDER BY m.date DESC";
		var attributes = new HashMap<String, Object>();
		attributes.put("user", user);
		var conversations = new HashMap<String, ConversationDescriptor>();
		dao.getAll(jpql, attributes).forEach(message -> {
			var key = getKey(user, message);
			var conversation = conversations.get(key);
			if (conversation == null) {
				conversations.put(key, conversation = new ConversationDescriptor(message));
			}
			if (message.readDate == null && !message.from.equals(user)) {
				conversation.unreadMessages++;
			}
			var other = message.from.equals(user) ? message.to : message.from;
			conversation.blocked = other.settings.blockedUsers.contains(user);
		});
		return new ArrayList<>(conversations.values());
	}

	private String getKey(User user, Message message) {
		if (message.team != null)
			return "team-" + message.team.id;
		var with = message.from.equals(user) ? message.to : message.from;
		return "user-" + with.id;
	}

	public List<Message> getMessages(User user, User with, int limit, Date before) {
		var jpql = "SELECT m FROM Message m WHERE ((m.from = :user AND m.to = :with) OR (m.to = :user AND m.from = :with)) AND m.team IS NULL ";
		if (before != null)
			jpql += "AND m.date < :before ";
		jpql += "ORDER BY m.date DESC";
		var attributes = new HashMap<String, Object>();
		attributes.put("user", user);
		attributes.put("with", with);
		if (before != null) {
			attributes.put("before", before);
		}
		return dao.getAll(jpql, attributes, 0, limit).stream()
				.sorted(new MessageSorter())
				.toList();
	}

	public List<Message> getMessages(User user) {
		var jpql = "SELECT m FROM Message m WHERE m.from = :user OR m.to = :user";
		var attributes = new HashMap<String, Object>();
		attributes.put("user", user);
		return dao.getAll(jpql, attributes);
	}

	public List<Message> getMessages(Team team) {
		var jpql = "SELECT m FROM Message m WHERE m.team = :team";
		var attributes = new HashMap<String, Object>();
		attributes.put("team", team);
		return dao.getAll(jpql, attributes);
	}

	public List<Message> getMessages(User user, Team team, int limit, Date before) {
		var jpql = "SELECT m FROM Message m WHERE m.team = :team AND m.to = :user ";
		if (before != null)
			jpql += "AND m.date < :before ";
		jpql += "ORDER BY m.date DESC";
		var attributes = new HashMap<String, Object>();
		attributes.put("team", team);
		attributes.put("user", user);
		if (before != null) {
			attributes.put("before", before);
		}
		return dao.getAll(jpql, attributes, 0, limit).stream()
				.sorted(new MessageSorter())
				.toList();
	}

	public void markAsRead(User user, User with) {
		var jpql = "SELECT m FROM Message m WHERE m.to = :user AND m.from = :with AND m.team IS NULL AND m.readDate IS NULL";
		var attributes = new HashMap<String, Object>();
		attributes.put("user", user);
		attributes.put("with", with);
		var messages = dao.getAll(jpql, attributes);
		for (Message message : messages) {
			message.readDate = Calendar.getInstance().getTime();
		}
		dao.update(messages);
	}

	public void markAsRead(User user, Team team) {
		var jpql = "SELECT m FROM Message m WHERE m.to = :user AND m.team = :team AND m.readDate IS NULL";
		var attributes = new HashMap<String, Object>();
		attributes.put("user", user);
		attributes.put("team", team);
		var messages = dao.getAll(jpql, attributes);
		messages.forEach(message -> {
			message.readDate = Calendar.getInstance().getTime();
		});
		dao.update(messages);
	}

	public boolean canMessage(User user) {
		var currentUser = userService.getCurrentUser();
		if (currentUser.settings.blockedUsers.contains(user))
			return false;
		if (!user.settings.messagingEnabled)
			return false;
		return true;
	}

	public class ConversationDescriptor {

		public final Message lastMessage;
		public int unreadMessages;
		public boolean blocked;

		public ConversationDescriptor(Message lastMessage) {
			this.lastMessage = lastMessage;
		}
		
		public User getOtherUser(User user) {
			if (user.equals(lastMessage.from))
				return lastMessage.to;
			return lastMessage.from;
		}

	}

	private class MessageSorter implements Comparator<Message> {

		@Override
		public int compare(Message m1, Message m2) {
			return Long.compare(m1.date.getTime(), m2.date.getTime());
		}

	}

}
