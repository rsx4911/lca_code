package com.greendelta.cloud.util;

import java.util.ArrayList;
import java.util.List;

public class Names {

	private final static List<String> reservedNames;

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
		reservedNames.add("group");
		reservedNames.add("groups");
		reservedNames.add("dashboard");
		reservedNames.add("administration");
		reservedNames.add("members");
		reservedNames.add("member");
	}

	public static boolean isReserved(String name) {
		return reservedNames.contains(name.toLowerCase());
	}

}
