package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;

public class MessageService {

	private final Dao<Message> dao;

	@Inject
	public MessageService(Dao<Message> dao) {
		this.dao = dao;
	}

	public Message insert(Message message) {
		return dao.insert(message);
	}

	public void delete(Message message) {
		dao.delete(message);
	}

	public List<Message> getAll(User from, User to, int page) {
		return null;
	}

	public List<Message> getLast(User user, int count) {
		return null;
	}

	public List<Message> getUnreadMessages(User user) {
		String jpql = "SELECT m FROM Message m WHERE m.unread = true AND m.to = :user";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		return dao.getAll(jpql, attributes);
	}

	public List<ConversationDescriptor> getConversations(User user) {
		String jpql = "SELECT m FROM Message m WHERE m.from = :user OR m.to = :user ORDER BY m.date DESC";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		List<Message> all = dao.getAll(jpql, attributes);
		Map<Long, ConversationDescriptor> conversations = new HashMap<>();
		for (Message message : all) {
			User with = message.from.equals(user) ? message.to : message.from;
			ConversationDescriptor conversation = conversations.get(with.getId());
			if (conversation == null) {
				conversations.put(with.getId(), conversation = new ConversationDescriptor(message));
			}
			if (message.unread && message.from.equals(with)) {
				conversation.unreadMessages++;
			}
		}
		return new ArrayList<>(conversations.values());
	}

	public List<Message> getMessages(User user, User with, int limit, Date before) {
		String jpql = "SELECT m FROM Message m WHERE ((m.from = :user AND m.to = :with) OR (m.to = :user AND m.from = :with)) ";
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

	public void markAsRead(User user) {
		String jpql = "SELECT m FROM Message m WHERE m.from = :user AND m.unread = true";
		List<Message> messages = dao.getAll(jpql, Collections.singletonMap("user", user));
		for (Message message : messages) {
			message.unread = false;
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
