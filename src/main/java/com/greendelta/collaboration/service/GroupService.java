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
import com.google.inject.name.Named;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

public class GroupService {

	private final static Logger log = LogManager.getLogger(GroupService.class);
	private final String root;
	private final AccessService accessService;
	private final MembershipService membershipService;
	private final UserService userService;

	@Inject
	public GroupService(@Named("repository.path") String repositoryPath,
			AccessService accessService, MembershipService membershipService, UserService userService) {
		this.root = repositoryPath;
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.userService = userService;
	}

	public boolean exists(String group) {
		return exists(group, false);
	}

	public boolean exists(String group, boolean skipAccessCheck) {
		File root = new File(this.root);
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
		if (!currentUser.admin && !currentUser.settings.canCreateGroups)
			throw new UnauthorizedAccessException("", "CREATE_GROUP");
		if (exists(group))
			return false;
		boolean created = new File(getPath(group)).mkdir();
		if (!created)
			return false;
		if (userGroup)
			return true;
		membershipService.addMembership(currentUser, group, Role.OWNER, true);
		return true;
	}

	boolean delete(String group) {
		return Directories.delete(new File(getPath(group)));
	}

	public byte[] getAvatar(String group) {
		if (!exists(group))
			return null;
		File avatarFile = new File(root, group + File.separator + "avatar");
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
		if (!accessService.canWrite(group))
			throw new UnauthorizedAccessException(group, "WRITE");
		File avatarFile = new File(root, group + File.separator + "avatar");
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
		File root = new File(this.root);
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
		return root + File.separator + group;
	}

}
