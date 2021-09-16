package com.greendelta.collaboration.webservice.util;

import com.google.common.base.Strings;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.ObjectMap;

public class Users {

	private Users() {
		// only static access
	}

	public static ObjectMap mapForSelf(User user) {
		ObjectMap map = ObjectMap.fromObject(user);
		map.remove("hash", "salt", "avatar", "twoFactorSecret");
		if (!Strings.isNullOrEmpty(user.twoFactorSecret))
			map.put("twoFactorAuth", true);
		map.put("settings.blockedUsers", Client.map(user.settings.blockedUsers, Users::mapForOthers));
		return map;
	}

	public static ObjectMap mapForOthers(User user) {
		if (user == null)
			return null;
		ObjectMap map = ObjectMap.fromObject(user);
		map.removeAllBut("name", "username");
		return map;
	}

	public static ObjectMap mapForAdmin(User user) {
		if (user == null)
			return null;
		ObjectMap map = ObjectMap.fromObject(user);
		map.removeAllBut("name", "username");
		map.put("deactivated", user.isDeactivated());
		return map;
	}

}
