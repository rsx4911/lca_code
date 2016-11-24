package com.greendelta.cloud.webservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;

public class Messages {

	private Messages() {
		// only static access
	}

	public static List<Map<String, Object>> map(List<Message> messages, User currentUser) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (Message message : messages)
			all.add(map(message, currentUser));
		return all;
	}

	public static Map<String, Object> map(Message message, User currentUser) {
		ObjectMap map = ObjectMap.fromObject(message);
		map.remove("from", "to", "showReadReceipt");
		map.put("from", Users.mapForOthers(message.from));
		map.put("to", Users.mapForOthers(message.to));
		if (message.team != null)
			map.put("team", Teams.mapForOthers(message.team));
		map.put("date", message.date.getTime());
		if (!message.showReadReceipt && message.from.equals(currentUser)) 
			map.remove("read");
		return map;
	}

}
