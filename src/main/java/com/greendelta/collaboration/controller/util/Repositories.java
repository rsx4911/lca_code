package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;

public class Repositories {

	private Repositories() {
		// only static access
	}

	public static Map<String, Object> map(Repository repo) {
		return map(repo, null);
	}

	public static Map<String, Object> map(Repository repo, Boolean groupIsUserNamespace) {
		var map = Maps.create();
		map.put("group", repo.group);
		map.put("name", repo.name);
		map.put("label", repo.getLabel());
		map.put("settings", repo.settings.toMap());
		map.put("groupSettings", repo.groupSettings.toMap());
		if (groupIsUserNamespace != null) {
			map.put("groupIsUserNamespace", groupIsUserNamespace);
		}
		return map;
	}

}
