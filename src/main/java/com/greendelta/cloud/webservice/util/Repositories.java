package com.greendelta.cloud.webservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.service.Repository;

public class Repositories {

	private Repositories() {
		// only static access
	}

	public static List<Map<String, Object>> map(List<Repository> repos) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (Repository repo : repos)
			all.add(map(repo, null));
		return all;
	}

	public static Map<String, Object> map(Repository repo, Boolean groupIsUserNamespace) {
		ObjectMap map = ObjectMap.fromMap(new HashMap<>());
		map.put("group", repo.group);
		map.put("name", repo.name);
		if (groupIsUserNamespace != null)
			map.put("groupIsUserNamespace", groupIsUserNamespace);
		return map;
	}
}
