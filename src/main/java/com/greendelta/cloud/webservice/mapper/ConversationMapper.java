package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;
import com.greendelta.cloud.service.MessageService.ConversationDescriptor;

public class ConversationMapper {

	private final MessageMapper messageMapper = new MessageMapper();
	private final UserMapper userMapper = new UserMapper();
	private final TeamMapper teamMapper = new TeamMapper();

	public List<Map<String, Object>> map(List<ConversationDescriptor> conversations, User currentUser) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (ConversationDescriptor conversation : conversations)
			all.add(map(conversation, currentUser));
		return all;
	}

	public Map<String, Object> map(ConversationDescriptor conversation, User currentUser) {
		Map<String, Object> map = new HashMap<>();
		map.put("recipient", getRecipient(conversation.lastMessage, currentUser));
		map.put("messages", Collections.singleton(messageMapper.map(conversation.lastMessage)));
		map.put("unreadMessages", conversation.unreadMessages);
		return map;
	}

	private Map<String, Object> getRecipient(Message message, User currentUser) {
		Map<String, Object> recipient = null;
		if (message.team != null) {
			recipient = teamMapper.mapForOthers(message.team);
			recipient.put("type", "team");
			recipient.put("id", message.team.teamname);
			return recipient;
		}
		User other = currentUser.equals(message.from) ? message.to : message.from;
		recipient = userMapper.mapForOthers(other);
		recipient.put("type", "user");
		recipient.put("id", other.username);
		return recipient;
	}
}
