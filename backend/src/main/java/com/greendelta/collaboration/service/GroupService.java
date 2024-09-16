package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openlca.util.Dirs;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.SettingsService.Settings;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class GroupService {

	private final PermissionsService permissions;
	private final MembershipService membershipService;
	private final UserService userService;
	private final SettingsService settings;

	public GroupService(PermissionsService permissions, MembershipService membershipService, UserService userService,
			SettingsService settings) {
		this.permissions = permissions;
		this.membershipService = membershipService;
		this.userService = userService;
		this.settings = settings;
	}

	private String getRootPath() {
		return settings.get(ServerSetting.REPOSITORY_PATH);
	}

	public boolean exists(String group) {
		return exists(group, false);
	}

	public boolean exists(String group, boolean skipAccessCheck) {
		var path = getRootPath();
		if (path == null || path.isEmpty())
			return false;
		var root = new File(path);
		if (root.list() == null)
			return false;
		for (var child : root.list())
			if (child.equalsIgnoreCase(group))
				if (!skipAccessCheck && !permissions.canRead(group))
					throw Response.forbidden(group, Permission.READ);
				else
					return true;
		return false;
	}

	public boolean isUserNamespace(String group) {
		return userService.exists(group);
	}

	public boolean isOwnNamespace(String group) {
		var user = userService.getCurrentUser();
		return isUserNamespace(group) && user != null && group.equals(user.username);
	}

	public boolean create(String group, boolean userGroup) {
		var currentUser = userService.getCurrentUser();
		if (userGroup && !currentUser.isUserManager() && !userGroup)
			throw Response.forbidden(null, Permission.CREATE);
		if (!currentUser.isDataManager() && !currentUser.settings.canCreateGroups && !userGroup)
			throw Response.forbidden(null, Permission.CREATE);
		if (exists(group))
			return false;
		var path = getPath(group);
		if (path == null || path.isEmpty())
			return false;
		var created = new File(path).mkdir();
		if (!created)
			return false;
		if (userGroup)
			return true;
		membershipService.addMembership(currentUser, group, Role.OWNER, true);
		return true;
	}

	public boolean delete(String group) {
		if (!permissions.canDelete(group))
			throw Response.forbidden(group, Permission.DELETE);
		getSettings(group).delete();
		var path = getPath(group);
		if (path == null || path.isEmpty())
			return false;
		Dirs.delete(new File(path));
		return true;
	}

	public long getRepositoryCount(String group) {
		return getRepositoryDirs(group).length;
	}

	public long getSize(String group) {
		var repoDirs = getRepositoryDirs(group);
		var size = 0l;
		for (var repoDir : repoDirs) {
			size += Dirs.size(repoDir.toPath());
		}
		return size;
	}

	private File[] getRepositoryDirs(String group) {
		var path = getRootPath();
		if (path == null || path.isEmpty())
			return new File[0];
		var root = new File(path);
		if (!root.exists() || !root.isDirectory())
			return new File[0];
		var groupDir = new File(root, group);
		if (!permissions.canRead(group))
			return new File[0];
		if (!groupDir.exists() || groupDir.listFiles() == null)
			return new File[0];
		return groupDir.listFiles();
	}

	public List<String> getAllAccessible() {
		var path = getRootPath();
		if (path == null || path.isEmpty())
			return new ArrayList<>();
		var root = new File(path);
		if (!root.exists() || !root.isDirectory())
			return new ArrayList<>();
		var groups = new ArrayList<String>();
		var user = userService.getCurrentUser();
		for (var group : root.listFiles()) {
			if (!group.isDirectory())
				continue;
			if (!permissions.canRead(group.getName()))
				continue;
			if (isUserNamespace(group.getName()) && (user == null || !group.getName().equals(user.username)))
				continue;
			groups.add(group.getName());
		}
		if (user != null && !user.isAnonymous() && !groups.contains(user.username)) {
			groups.add(user.username);
		}
		return groups;
	}

	private String getPath(String group) {
		var path = getRootPath();
		if (path == null)
			return null;
		return path + File.separator + group;
	}

	public Settings<GroupSetting> getSettings(String group) {
		Settings<GroupSetting> groupSettings = settings.get(SettingType.GROUP_SETTING, group,
				permissions::canSetSettingsOf);
		var user = userService.getForUsername(group);
		if (user == null)
			return groupSettings;
		groupSettings.setDefault(GroupSetting.LABEL, user.name);
		groupSettings.setDefault(GroupSetting.DESCRIPTION, "The default group for user " + user.name);
		return groupSettings;
	}

}
