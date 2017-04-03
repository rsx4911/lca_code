package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.util.ObjectMap;

import com.google.common.base.Strings;
import com.greendelta.collaboration.model.User;

public class Users {

	private Users() {
		// only static access
	}

	public static List<ObjectMap> mapForSelfOrAdmin(List<User> users) {
		List<ObjectMap> all = new ArrayList<>();
		for (User user : users)
			all.add(mapForSelf(user));
		return all;
	}

	public static List<ObjectMap> mapForOthers(List<User> users) {
		List<ObjectMap> all = new ArrayList<>();
		for (User user : users)
			all.add(mapForOthers(user));
		return all;
	}

	public static ObjectMap mapForSelf(User user) {
		ObjectMap map = ObjectMap.fromObject(user);
		map.remove("hash", "salt", "avatar", "twoFactorSecret");
		if (!Strings.isNullOrEmpty(user.twoFactorSecret))
			map.put("twoFactorAuth", true);
		map.put("settings.blockedUsers", mapForOthers(user.settings.blockedUsers));
		return map;
	}

	public static ObjectMap mapForOthers(User user) {
		ObjectMap map = ObjectMap.fromObject(user);
		map.removeAllBut("name", "username");
		return map;
	}

}
