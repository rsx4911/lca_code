package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.List;

public class Names {

	private final static List<String> RESERVED;
	private final static List<String> USER_ROUTES;

	static {
		USER_ROUTES = new ArrayList<>();
		USER_ROUTES.add("repository");
		USER_ROUTES.add("user");
		USER_ROUTES.add("dashboard");
		USER_ROUTES.add("administration");
		USER_ROUTES.add("messages");
		USER_ROUTES.add("tasks");
		USER_ROUTES.add("group");
		USER_ROUTES.add("groups");
		USER_ROUTES.add("error");

		RESERVED = new ArrayList<>();
		RESERVED.add("null");
		RESERVED.add("undefined");
		RESERVED.add("users");
		RESERVED.add("team");
		RESERVED.add("teams");
		RESERVED.add("repositories");
		RESERVED.add("categoryInfo");
		RESERVED.add("count");
		RESERVED.add("public");
		RESERVED.add("images");
		RESERVED.add("fonts");
		RESERVED.add("ws");
		RESERVED.add("sockets");
		RESERVED.add("group");
		RESERVED.add("groups");
		RESERVED.add("dashboard");
		RESERVED.add("commit");
		RESERVED.add("category");
		RESERVED.add("members");
		RESERVED.add("member");
		RESERVED.add("references");
		RESERVED.add("settings");
		RESERVED.add("admin");
		RESERVED.add("usermanager");
		RESERVED.add("datamanager");
		RESERVED.add("messaging");
		RESERVED.add("block");
		RESERVED.add("unblock");
		RESERVED.add("login");
		RESERVED.add("search");
		RESERVED.add("imprint");
		RESERVED.add("overlay");
		RESERVED.add("import");
		RESERVED.add("export");
	}

	public static boolean isUserRoute(String name) {
		return USER_ROUTES.contains(name);
	}

	public static String[] getUserRoutes() {
		return USER_ROUTES.toArray(new String[USER_ROUTES.size()]);
	}

	public static boolean isReserved(String name) {
		return isUserRoute(name) || RESERVED.contains(name.toLowerCase());
	}

	public static boolean isValid(String name, char... additionalValidChars) {
		if (name.length() < 4)
			return false;
		String regex = "^[a-zA-Z0-9_";
		if (additionalValidChars != null) {
			for (char character : additionalValidChars) {
				regex += character;
			}
		}
		regex += "]+$";
		if (!name.matches(regex))
			return false;
		return true;
	}

}
