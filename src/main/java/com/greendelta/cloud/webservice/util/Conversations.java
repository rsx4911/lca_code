package com.greendelta.cloud.webservice.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;
import com.greendelta.cloud.service.MessagingService.ConversationDescriptor;

public class Conversations {

	private Conversations() {
		// only static access
	}
	
	public static List<Map<String, Object>> map(List<ConversationDescriptor> conversations, User currentUser) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (ConversationDescriptor conversation : conversations)
			all.add(map(conversation, currentUser));
		return all;
	}

	public static Map<String, Object> map(ConversationDescriptor conversation, User currentUser) {
		Map<String, Object> map = new HashMap<>();
		map.put("recipient", getRecipient(conversation.lastMessage, currentUser));
		map.put("messages", Collections.singleton(Messages.map(conversation.lastMessage)));
		map.put("unreadMessages", conversation.unreadMessages);
		return map;
	}

	private static Map<String, Object> getRecipient(Message message, User currentUser) {
		Map<String, Object> recipient = null;
		if (message.team != null) {
			recipient = Teams.mapForOthers(message.team);
			recipient.put("type", "team");
			recipient.put("id", message.team.teamname);
			return recipient;
		}
		User other = currentUser.equals(message.from) ? message.to : message.from;
		recipient = Users.mapForOthers(other);
		recipient.put("type", "user");
		recipient.put("id", other.username);
		return recipient;
	}
}
