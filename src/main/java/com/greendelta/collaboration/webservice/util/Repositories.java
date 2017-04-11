package com.greendelta.collaboration.webservice.util;

import java.util.HashMap;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.service.Repository;

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
		if (groupIsUserNamespace != null)
			map.put("groupIsUserNamespace", groupIsUserNamespace);
		return map;
	}
}
