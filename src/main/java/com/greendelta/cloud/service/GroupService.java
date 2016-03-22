package com.greendelta.cloud.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.util.Directories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.Role;
import com.greendelta.cloud.model.User;

public class GroupService {

	private final static Logger log = LoggerFactory.getLogger(GroupService.class);
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
		if (!new File(getPath(group)).exists())
			return false;
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canRead(currentUser, group))
			throw new UnauthorizedAccessException(group, "READ");
		return true;
	}

	public boolean isUserNamespace(String group) {
		if (!exists(group))
			return false;
		return userService.exists(group);
	}

	public boolean create(String group) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.canCreateGroups)
			throw new UnauthorizedAccessException("", "CREATE_GROUP");
		if (exists(group))
			return false;
		boolean created = new File(getPath(group)).mkdir();
		if (!created)
			return false;
		membershipService.addMembership(currentUser, group, Role.OWNER);
		return true;
	}

	public boolean delete(String group) {
		if (!exists(group))
			return false;
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canDelete(currentUser, group))
			throw new UnauthorizedAccessException(group, "DELETE");
		File groupDir = new File(getPath(group));
		for (File repo : groupDir.listFiles())
			membershipService.removeMemberships(Repository.toId(group, repo.getName()));
		membershipService.removeMemberships(group);
		return Directories.delete(groupDir);
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

	public PagedResult<String> getAll(int page, String filter,
			boolean adminArea) {
		List<String> accessible = getAll(adminArea);
		return PagedResult.pagedAndFiltered(page, filter, accessible);
	}

	private List<String> getAll(boolean adminArea) {
		File root = new File(this.root);
		List<String> groups = new ArrayList<>();
		User currentUser = userService.getCurrentUser();
		boolean isAdmin = adminArea && currentUser.admin;
		for (File group : root.listFiles()) {
			if (!isAdmin && !accessService.canRead(currentUser, group.getName()))
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
