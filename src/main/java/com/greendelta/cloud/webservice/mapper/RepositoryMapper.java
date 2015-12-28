package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.service.Repository;

public class RepositoryMapper {

	public List<Map<String, Object>> map(List<Repository> repos) {
		List<Map<String, Object>> all = new ArrayList<>();
		for (Repository repo : repos)
			all.add(map(repo));
		return all;
	}

	public Map<String, Object> map(Repository repo) {
		ObjectMap map = ObjectMap.fromMap(new HashMap<>());
		map.put("group", repo.group);
		map.put("name", repo.name);
		return map;
	}

}
