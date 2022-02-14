package com.greendelta.collaboration.controller.util;

import java.util.Map;

import org.openlca.util.Strings;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.Maps;

public class Users {

	private Users() {
		// only static access
	}

	public static Map<String, Object> mapForSelf(User user) {
		var map = Maps.of(user);
		Maps.remove(map, "password", "avatar", "twoFactorSecret", "authorities", "accountNonExpired",
				"accountNonLocked",
				"credentialsNonExpired", "enabled");
		if (!Strings.nullOrEmpty(user.twoFactorSecret)) {
			map.put("twoFactorAuth", true);
		}
		Maps.put(map, "settings.blockedUsers", user.settings.blockedUsers.stream().map(Users::mapForOthers).toList());
		return map;
	}

	public static Map<String, Object> mapForOthers(User user) {
		if (user == null)
			return null;
		var map = Maps.of(user);
		Maps.removeAllBut(map, "name", "username");
		return map;
	}

	public static Map<String, Object> mapForAdmin(User user) {
		if (user == null)
			return null;
		var map = Maps.of(user);
		Maps.removeAllBut(map, "name", "username", "email");
		map.put("deactivated", user.isDeactivated());
		map.put("activeUntil", user.settings.activeUntil);
		return map;
	}

}
