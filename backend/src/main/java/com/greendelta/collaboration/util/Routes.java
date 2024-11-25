package com.greendelta.collaboration.util;

import java.util.Arrays;
import java.util.List;

public class Routes {

	private final static List<String> USER_ROUTES = Arrays.asList(
			"repository", "user", "dashboard", "administration", "messages", "tasks", "group", "groups", "error");

	private final static List<String> PUBLIC_RESOURCES = Arrays.asList(
			"css", "fonts", "images", "js", "graph");

	private final static List<String> LOGIN_URLS = Arrays.asList(
			"login", "reset-password", "sign-up");

	private final static List<String> OTHER_PUBLIC_URLS = Arrays.asList(
			"imprint", "job", "maintenance");

	private final static List<String> RESERVED = Arrays.asList(
			"null", "undefined", "users", "team", "teams", "repositories", "categoryInfo", "count", "public", "ws",
			"sockets", "stomp", "group", "groups", "dashboard", "commit", "category", "members", "member", "references",
			"settings", "admin", "usermanager", "datamanager", "librarymanager", "messaging", "block", "unblock",
			"login", "search", "imprint", "overlay", "import", "export", "missing", "api");

	public static boolean isUserRoute(String name) {
		return USER_ROUTES.contains(name);
	}

	public static String[] getUserRoutes() {
		return USER_ROUTES.toArray(new String[USER_ROUTES.size()]);
	}

	public static boolean isPublicResource(String path) {
		if (path.contains("/")) {
			path = path.substring(0, path.indexOf("/"));
		}
		return PUBLIC_RESOURCES.contains(path);
	}

	public static boolean isLoginUrl(String name) {
		return LOGIN_URLS.contains(name);
	}

	public static boolean isPublicUrl(String name) {
		return isLoginUrl(name) || OTHER_PUBLIC_URLS.contains(name);
	}

	public static boolean isReserved(String name) {
		name = name.toLowerCase().strip();
		return isUserRoute(name) || isPublicResource(name) || isPublicUrl(name) || RESERVED.contains(name);
	}

	public static boolean isValid(String name) {
		if (name.length() < 3)
			return false;
		var regex = "^[a-zA-Z0-9_-]+$";
		if (!name.matches(regex))
			return false;
		return true;
	}

}
