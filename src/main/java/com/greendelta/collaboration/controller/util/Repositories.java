package com.greendelta.collaboration.controller.util;

import java.util.HashMap;
import java.util.Map;

import org.openlca.git.RepositoryInfo;

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
		var version = new HashMap<String, Object>();
		var repoVersion = repo.getServerVersion();
		version.put("repository", repoVersion);
		version.put("server", RepositoryInfo.REPOSITORY_CURRENT_SERVER_VERSION);
		version.put("isSupported", RepositoryInfo.REPOSITORY_SUPPORTED_SERVER_VERSIONS.contains(repoVersion));
		map.put("version", version);
		map.put("settings", repo.settings.toMap());
		map.put("groupSettings", repo.groupSettings.toMap());
		if (groupIsUserNamespace != null) {
			map.put("groupIsUserNamespace", groupIsUserNamespace);
		}
		return map;
	}

}
