package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;

public class MessagingService {

	private final Dao<Message> dao;

	@Inject
	public MessagingService(Dao<Message> dao) {
		this.dao = dao;
	}

	public Message insert(Message message) {
		return dao.insert(message);
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
			return "team-" + message.team.getId();
		User with = message.from.equals(user) ? message.to : message.from;
		return "user-" + with.getId();
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
		List<Message> filtered = dao.getAll(jpql, attributes, 0, limit);
		Collections.sort(filtered, new MessageSorter());
		return filtered;
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
		List<Message> filtered = dao.getAll(jpql, attributes, 0, limit);
		Collections.sort(filtered, new MessageSorter());
		return filtered;
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
