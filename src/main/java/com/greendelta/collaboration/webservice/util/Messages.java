package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.User;

public class Messages {

	private Messages() {
		// only static access
	}

	public static List<ObjectMap> map(List<Message> messages, User currentUser) {
		List<ObjectMap> all = new ArrayList<>();
		for (Message message : messages)
			all.add(map(message, currentUser));
		return all;
	}

	public static ObjectMap map(Message message, User currentUser) {
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
