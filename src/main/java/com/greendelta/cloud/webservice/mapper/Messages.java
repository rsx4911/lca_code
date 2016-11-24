package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.chat.Message;

public class Messages {

	private Messages() {
		// only static access
	}

	public static List<Map<String, Object>> map(List<Message> messages) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (Message message : messages)
			all.add(map(message));
		return all;
	}

	public static Map<String, Object> map(Message message) {
		ObjectMap map = ObjectMap.fromObject(message);
		map.remove("from", "to");
		map.put("from", Users.mapForOthers(message.from));
		map.put("to", Users.mapForOthers(message.to));
		if (message.team != null)
			map.put("team", Teams.mapForOthers(message.team));
		map.put("date", message.date.getTime());
		return map;
	}

}
