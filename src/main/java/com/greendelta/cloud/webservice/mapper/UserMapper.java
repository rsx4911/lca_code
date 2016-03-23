package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.User;

public class UserMapper {

	public List<Map<String, Object>> mapForSelfOrAdmin(List<User> users) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (User user : users)
			all.add(mapForSelf(user));
		return all;
	}

	public List<Map<String, Object>> mapForOthers(List<User> users) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (User user : users)
			all.add(mapForOthers(user));
		return all;
	}

	public Map<String, Object> mapForSelf(User user) {
		ObjectMap map = ObjectMap.fromObject(user);
		map.remove("hash", "salt", "avatar");
		return map;
	}

	public Map<String, Object> mapForOthers(User user) {
		ObjectMap map = ObjectMap.fromObject(user);
		map.removeAllBut("name", "username");
		return map;
	}

}
