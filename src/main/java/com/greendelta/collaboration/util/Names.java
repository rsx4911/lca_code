package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.List;

public class Names {

	private final static List<String> reservedNames;
	private final static String REGEX_NAME = "^[a-zA-Z0-9_]+$";

	static {
		reservedNames = new ArrayList<>();
		reservedNames.add("null");
		reservedNames.add("undefined");
		reservedNames.add("user");
		reservedNames.add("users");
		reservedNames.add("team");
		reservedNames.add("teams");
		reservedNames.add("repository");
		reservedNames.add("repositories");
		reservedNames.add("public");
		reservedNames.add("images");
		reservedNames.add("fonts");
		reservedNames.add("sockets");
		reservedNames.add("group");
		reservedNames.add("groups");
		reservedNames.add("dashboard");
		reservedNames.add("administration");
		reservedNames.add("members");
		reservedNames.add("member");
		reservedNames.add("search");
		reservedNames.add("references");
		reservedNames.add("settings");
		reservedNames.add("messaging");
		reservedNames.add("messages");
		reservedNames.add("block");
		reservedNames.add("unblock");
	}

	public static boolean isReserved(String name) {
		return reservedNames.contains(name.toLowerCase());
	}

	public static boolean isValid(String name) {
		if (name.length() < 4)
			return false;
		if (!name.matches(REGEX_NAME))
			return false;
		return true;
	}

}
