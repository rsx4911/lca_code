package com.greendelta.collaboration.controller.util;

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
		return conversations.stream().map(c -> map(c, currentUser)).toList();
	}

	public static ObjectMap map(ConversationDescriptor conversation, User currentUser) {
		var map = new ObjectMap();
		map.put("recipient", getRecipient(conversation.lastMessage, currentUser));
		map.put("messages", Collections.singleton(Messages.map(conversation.lastMessage, currentUser)));
		map.put("unreadMessages", conversation.unreadMessages);
		return map;
	}

	private static ObjectMap getRecipient(Message message, User currentUser) {
		if (message.team != null) {
			var recipient = Teams.mapForOthers(message.team);
			recipient.put("type", "team");
			recipient.put("id", message.team.teamname);
			return recipient;
		}
		var other = currentUser.equals(message.from) ? message.to : message.from;
		var recipient = Users.mapForOthers(other);
		recipient.put("type", "user");
		recipient.put("id", other.username);
		return recipient;
	}
}
