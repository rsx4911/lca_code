package com.greendelta.collaboration.controller.util;

import java.util.List;
import java.util.Map;

import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.Maps;

public class Messages {

	private Messages() {
		// only static access
	}

	public static List<Map<String, Object>> map(List<Message> messages, User currentUser) {
		return messages.stream().map(m -> map(m, currentUser)).toList();
	}

	public static Map<String, Object> map(Message message, User currentUser) {
		var map = Maps.of(message);
		Maps.remove(map, "from", "to", "showReadReceipt");
		map.put("from", Users.mapForOthers(message.from));
		map.put("to", Users.mapForOthers(message.to));
		if (message.team != null) {
			map.put("team", Teams.mapForOthers(message.team));
		}
		map.put("date", message.date.getTime());
		if (!message.showReadReceipt && message.from.equals(currentUser)) {
			map.remove("readDate");
		}
		return map;
	}

}
