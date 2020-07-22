package com.greendelta.collaboration.webservice.util;

import java.util.HashMap;

import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.ObjectMap;

public class Repositories {

	private Repositories() {
		// only static access
	}

	public static ObjectMap map(Repository repo) {
		return map(repo, null);
	}

	public static ObjectMap map(Repository repo, Boolean groupIsUserNamespace) {
		ObjectMap map = ObjectMap.fromMap(new HashMap<>());
		map.put("group", repo.group);
		map.put("name", repo.name);
		map.put("label", repo.getLabel());
		map.put("settings", ObjectMap.fromObject(repo.settings));
		map.put("groupSettings", ObjectMap.fromObject(repo.groupSettings));
		if (groupIsUserNamespace != null)
			map.put("groupIsUserNamespace", groupIsUserNamespace);
		return map;
	}

}
