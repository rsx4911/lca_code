package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.util.Directories;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.SettingsService.Settings;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

public class GroupService {

	private final AccessService accessService;
	private final MembershipService membershipService;
	private final UserService userService;
	private final SettingsService settingsService;

	@Inject
	public GroupService(AccessService accessService, MembershipService membershipService, UserService userService,
			SettingsService settingsService) {
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.userService = userService;
		this.settingsService = settingsService;
	}

	private String getRootPath() {
		return settingsService.get(ServerSetting.REPOSITORY_PATH);
	}

	public boolean exists(String group) {
		return exists(group, false);
	}

	public boolean exists(String group, boolean skipAccessCheck) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return false;
		File root = new File(path);
		if (root.list() == null)
			return false;
		for (String child : root.list())
			if (child.equalsIgnoreCase(group))
				if (!skipAccessCheck && !accessService.canRead(group))
					throw new UnauthorizedAccessException(group, "READ");
				else
					return true;
		return false;
	}

	public boolean isUserNamespace(String group) {
		return isUserNamespace(group, false);
	}

	public boolean isUserNamespace(String group, boolean skipAccessCheck) {
		if (!exists(group, skipAccessCheck))
			return false;
		return userService.exists(group);
	}

	public boolean create(String group, boolean userGroup) {
		User currentUser = userService.getCurrentUser();
		if (userGroup && !currentUser.isUserManager())
			throw new UnauthorizedAccessException("", "CREATE_GROUP");
		if (!currentUser.isDataManager() && !currentUser.settings.canCreateGroups)
			throw new UnauthorizedAccessException("", "CREATE_GROUP");
		if (exists(group))
			return false;
		String path = getPath(group);
		if (path == null || path.isEmpty())
			return false;
		boolean created = new File(path).mkdir();
		if (!created)
			return false;
		if (userGroup)
			return true;
		membershipService.addMembership(currentUser, group, Role.OWNER, true);
		return true;
	}

	public boolean delete(String group) {
		if (!accessService.canDelete(group))
			throw new UnauthorizedAccessException(group, "DELETE");
		String path = getPath(group);
		if (path == null || path.isEmpty())
			return false;
		return Directories.delete(new File(path));
	}

	public long getCount(boolean adminArea) {
		return getAll(adminArea, false).size();
	}

	public SearchResult<String> getAll(int page, int pageSize, String filter, boolean adminArea,
			boolean onlyIfCanWrite) {
		List<String> accessible = getAll(adminArea, onlyIfCanWrite);
		return SearchResults.pagedAndFiltered(page, pageSize, filter, accessible);
	}

	public long getRepositoryCount(String group) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return 0;
		File root = new File(path);
		if (!root.exists() || !root.isDirectory())
			return 0;
		File groupDir = new File(root, group);
		if (!accessService.canRead(group, false))
			return 0;
		return groupDir.listFiles().length;
	}

	private List<String> getAll(boolean adminArea, boolean onlyIfCanWrite) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return new ArrayList<>();
		File root = new File(path);
		if (!root.exists() || !root.isDirectory())
			return new ArrayList<>();
		List<String> groups = new ArrayList<>();
		User user = userService.getCurrentUser();
		for (File group : root.listFiles()) {
			if (!group.isDirectory())
				continue;
			if (!accessService.canRead(group.getName(), !adminArea))
				continue;
			if (onlyIfCanWrite && !accessService.canWrite(group.getName()))
				continue;
			if (isUserNamespace(group.getName()) && (user == null || !group.getName().equals(user.username)))
				continue;
			groups.add(group.getName());
		}
		return groups;
	}

	private String getPath(String group) {
		String path = getRootPath();
		if (path == null)
			return null;
		return path + File.separator + group;
	}

	public Settings<GroupSetting> getSettings(String group) {
		if (!accessService.canRead(group))
			throw new UnauthorizedAccessException(group, "READ");
		Settings<GroupSetting> settings = settingsService.get(SettingType.GROUP_SETTING, group,
				accessService::canSetSettings);
		User user = userService.getForUsername(group);
		if (user == null)
			return settings;
		if (settings.get(GroupSetting.LABEL) == null) {
			settings.set(GroupSetting.LABEL, user.name);
		}
		if (settings.get(GroupSetting.DESCRIPTION) == null) {
			settings.set(GroupSetting.DESCRIPTION, "The default group for user " + user.name);
		}
		return settings;
	}

}
