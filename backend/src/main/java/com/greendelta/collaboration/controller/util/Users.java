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
		return mapForSelf(user, null);
	}

	public static Map<String, Object> mapForCurrentUser(User user, boolean isInTeam) {
		return mapForSelf(user, isInTeam);
	}

	private static Map<String, Object> mapForSelf(User user, Boolean isInTeam) {
		var map = Maps.of(user);
		Maps.removeAllBut(map, "id", "username", "name", "email", "settings", "deactivated");
		if (!Strings.nullOrEmpty(user.twoFactorSecret)) {
			map.put("twoFactorAuth", true);
		}
		Maps.put(map, "settings.blockedUsers", user.settings.blockedUsers.stream().map(Users::mapForOthers).toList());
		if (isInTeam != null) {
			Maps.put(map, "isInTeam", isInTeam);
		}
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
