package com.greendelta.collaboration.controller.util;

import org.openlca.util.Strings;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.ObjectMap;

public class Users {

	private Users() {
		// only static access
	}

	public static ObjectMap mapForSelf(User user) {
		var map = ObjectMap.fromObject(user);
		map.remove("password", "avatar", "twoFactorSecret", "authorities", "accountNonExpired", "accountNonLocked",
				"credentialsNonExpired", "enabled");
		if (!Strings.nullOrEmpty(user.twoFactorSecret)) {
			map.put("twoFactorAuth", true);
		}
		map.put("settings.blockedUsers", user.settings.blockedUsers.stream().map(Users::mapForOthers).toList());
		return map;
	}

	public static ObjectMap mapForOthers(User user) {
		if (user == null)
			return null;
		var map = ObjectMap.fromObject(user);
		map.removeAllBut("name", "username");
		return map;
	}

	public static ObjectMap mapForAdmin(User user) {
		if (user == null)
			return null;
		var map = ObjectMap.fromObject(user);
		map.removeAllBut("name", "username");
		map.put("deactivated", user.isDeactivated());
		return map;
	}

}
