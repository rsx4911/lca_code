package com.greendelta.collaboration.controller.util;

import java.util.HashMap;
import java.util.Map;

import org.openlca.git.RepositoryInfo;
import org.openlca.git.model.Commit;

import com.greendelta.collaboration.model.ReleaseInfo;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;

public class Repositories {

	private Repositories() {
		// only static access
	}

	public static Map<String, Object> map(Repository repo, Commit commit, ReleaseInfo release) {
		var map = Maps.create();
		map.put("group", repo.group);
		map.put("name", repo.name);
		var label = repo.groupSettings.get(GroupSetting.LABEL, repo.group) + "/" + release.label;
		map.put("label", label);
		map.put("settings", Releases.map(release));
		Maps.put(map, "settings.releaseDate", commit.timestamp);
		map.put("hasReleases", true);
		return map;
	}

	public static Map<String, Object> mapForList(Repository repo, Boolean hasReleases) {
		var map = map(repo);
		if (hasReleases != null) {
			map.put("hasReleases", hasReleases);
		}
		return map;
	}

	public static Map<String, Object> mapForUser(Repository repo, Boolean groupIsUserNamespace) {
		var map = map(repo);
		if (groupIsUserNamespace != null) {
			map.put("groupIsUserNamespace", groupIsUserNamespace);
		}
		return map;
	}

	private static Map<String, Object> map(Repository repo) {
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
		return map;
	}

}
