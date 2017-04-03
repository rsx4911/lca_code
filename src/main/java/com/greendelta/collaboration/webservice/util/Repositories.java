package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.service.Repository;

public class Repositories {

	private Repositories() {
		// only static access
	}

	public static List<ObjectMap> map(List<Repository> repos) {
		List<ObjectMap> all = new ArrayList<>();
		for (Repository repo : repos)
			all.add(map(repo, null));
		return all;
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
