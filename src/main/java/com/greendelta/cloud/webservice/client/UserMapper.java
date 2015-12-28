package com.greendelta.cloud.webservice.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.User;

public class UserMapper {

	public List<Map<String, Object>> map(List<User> users) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (User user : users) {
			ObjectMap map = ObjectMap.fromObject(user);
			map.remove("hash", "salt", "avatar");
			all.add(map);
		}
		return all;
	}

	public Map<String, Object> map(User user) {
		ObjectMap map = ObjectMap.fromObject(user);
		map.remove("hash", "salt", "avatar");
		return map;
	}

}
