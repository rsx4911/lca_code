package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.MessagingService.ConversationDescriptor;
import com.greendelta.collaboration.util.ObjectMap;

public class Conversations {

	private Conversations() {
		// only static access
	}

	public static List<ObjectMap> map(List<ConversationDescriptor> conversations, User currentUser) {
		List<ObjectMap> all = new ArrayList<>();
		for (ConversationDescriptor conversation : conversations)
			all.add(map(conversation, currentUser));
		return all;
	}

	public static ObjectMap map(ConversationDescriptor conversation, User currentUser) {
		ObjectMap map = new ObjectMap();
		map.put("recipient", getRecipient(conversation.lastMessage, currentUser));
		map.put("messages", Collections.singleton(Messages.map(conversation.lastMessage, currentUser)));
		map.put("unreadMessages", conversation.unreadMessages);
		return map;
	}

	private static ObjectMap getRecipient(Message message, User currentUser) {
		if (message.team != null) {
			ObjectMap recipient = Teams.mapForOthers(message.team);
			recipient.put("type", "team");
			recipient.put("id", message.team.teamname);
			return recipient;
		}
		User other = currentUser.equals(message.from) ? message.to : message.from;
		ObjectMap recipient = Users.mapForOthers(other);
		recipient.put("type", "user");
		recipient.put("id", other.username);
		return recipient;
	}
}
