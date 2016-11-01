package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greendelta.cloud.service.MessageService.ConversationDescriptor;

public class ConversationMapper {

	private final MessageMapper messageMapper = new MessageMapper();

	public List<Map<String, Object>> map(List<ConversationDescriptor> conversations) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (ConversationDescriptor conversation : conversations)
			all.add(map(conversation));
		return all;
	}

	public Map<String, Object> map(ConversationDescriptor conversation) {
		Map<String, Object> map = new HashMap<>();
		map.put("messages", Collections.singleton(messageMapper.map(conversation.lastMessage)));
		map.put("unreadMessages", conversation.unreadMessages);
		return map;
	}

}
