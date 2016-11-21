package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.chat.Message;

public class MessageMapper {

	private final UserMapper userMapper = new UserMapper();
	private final TeamMapper teamMapper = new TeamMapper();

	public List<Map<String, Object>> map(List<Message> messages) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (Message message : messages)
			all.add(map(message));
		return all;
	}

	public Map<String, Object> map(Message message) {
		ObjectMap map = ObjectMap.fromObject(message);
		map.remove("from", "to");
		map.put("from", userMapper.mapForOthers(message.from));
		map.put("to", userMapper.mapForOthers(message.to));
		if (message.team != null)
			map.put("team", teamMapper.mapForOthers(message.team));
		map.put("date", message.date.getTime());
		return map;
	}

}
