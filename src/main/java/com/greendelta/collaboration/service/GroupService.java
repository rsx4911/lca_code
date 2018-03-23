package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.util.Directories;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

public class GroupService {

	private final static Logger log = LogManager.getLogger(GroupService.class);
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
		return settingsService.get(Key.REPOSITORY_PATH);
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
		if (!currentUser.isAdmin() && !currentUser.settings.canCreateGroups)
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

	boolean delete(String group) {
		String path = getPath(group);
		if (path == null || path.isEmpty())
			return false;
		return Directories.delete(new File(path));
	}

	public byte[] getAvatar(String group) {
		if (!exists(group))
			return null;
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return null;
		File avatarFile = new File(path, group + File.separator + "avatar");
		if (!avatarFile.exists())
			return null;
		try {
			return Files.toByteArray(avatarFile);
		} catch (IOException e) {
			log.error("Error reading group avatar file", e);
			return null;
		}
	}

	public void setAvatar(String group, InputStream file) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return;
		if (!accessService.canWrite(group))
			throw new UnauthorizedAccessException(group, "WRITE");
		File avatarFile = new File(path, group + File.separator + "avatar");
		if (file != null)
			try (FileOutputStream output = new FileOutputStream(avatarFile)) {
				ByteStreams.copy(file, output);
			} catch (IOException e) {
				log.error("Error writing group avatar file", e);
			}
		else if (avatarFile.exists())
			avatarFile.delete();
	}

	public long getCount(boolean adminArea) {
		return getAll(adminArea, false).size();
	}

	public SearchResult<String> getAll(int page, int pageSize, String filter, boolean adminArea, boolean onlyIfCanWrite) {
		List<String> accessible = getAll(adminArea, onlyIfCanWrite);
		return SearchResults.pagedAndFiltered(page, pageSize, filter, accessible);
	}

	private List<String> getAll(boolean adminArea, boolean onlyIfCanWrite) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return new ArrayList<>();
		File root = new File(path);
		List<String> groups = new ArrayList<>();
		for (File group : root.listFiles()) {
			if (!group.isDirectory())
				continue;
			if (!accessService.canRead(group.getName(), !adminArea))
				continue;
			if (onlyIfCanWrite && !accessService.canWrite(group.getName()))
				continue;
			if (isUserNamespace(group.getName()))
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

}
