package com.greendelta.collaboration.util;

import java.util.Arrays;
import java.util.List;

public class Routes {

	private final static List<String> USER_ROUTES = Arrays.asList(
			"repository", "user", "dashboard", "administration", "messages", "tasks", "group", "groups", "error");
	
	private final static List<String> PUBLIC_RESOURCES = Arrays.asList(
			"css", "fonts", "images", "js", "graph");

	public final static List<String> PUBLIC_URLS = Arrays.asList(
			"login", "reset-password", "sign-up", "imprint", "job", "maintenance");
	
	private final static List<String> RESERVED = Arrays.asList(
			"null", "undefined", "users", "team", "teams", "repositories", "categoryInfo", "count", "public", "ws",
			"sockets", "group", "groups", "dashboard", "commit", "category", "members", "member", "references",
			"settings", "admin", "usermanager", "datamanager", "messaging", "block", "unblock", "login", "search",
			"imprint", "overlay", "import", "export");

	public static boolean isUserRoute(String name) {
		return USER_ROUTES.contains(name);
	}

	public static String[] getUserRoutes() {
		return USER_ROUTES.toArray(new String[USER_ROUTES.size()]);
	}

	public static boolean isPublicResource(String name) {
		return PUBLIC_RESOURCES.contains(name);
	}
	
	public static boolean isPublicUrl(String name) {
		return PUBLIC_URLS.contains(name);
	}

	public static boolean isReserved(String name) {
		name = name.toLowerCase().strip();
		return isUserRoute(name) || isPublicResource(name) || isPublicUrl(name) || RESERVED.contains(name);
	}

	public static boolean isValid(String name, char... additionalValidChars) {
		if (name.length() < 4)
			return false;
		var regex = "^[a-zA-Z0-9_";
		if (additionalValidChars != null) {
			for (var character : additionalValidChars) {
				regex += character;
			}
		}
		regex += "]+$";
		if (!name.matches(regex))
			return false;
		return true;
	}

}
