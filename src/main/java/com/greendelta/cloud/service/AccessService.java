package com.greendelta.cloud.service;

import java.io.File;

import com.google.inject.Inject;
import com.greendelta.cloud.model.Permission;
import com.greendelta.cloud.model.Role;
import com.greendelta.cloud.model.User;

public class AccessService {

	private final MembershipService membershipService;

	@Inject
	public AccessService(MembershipService membershipService) {
		this.membershipService = membershipService;
	}

	public boolean canRead(User user, String groupOrRepo) {
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		if (isGroup(groupOrRepo))
			if (membershipService.hasMembershipInAnyRepoInGroup(user, groupOrRepo))
				return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.READ);
	}

	public boolean canWrite(User user, String groupOrRepo) {
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.WRITE);
	}

	public boolean canMove(User user, String groupOrRepo) {
		if (isGroup(groupOrRepo))
			return false; // can not move groups
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.MOVE);
	}
	
	public boolean canDelete(User user, String groupOrRepo) {
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.DELETE);
	}

	public boolean canEditMembers(User user, String groupOrRepo) {
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.EDIT_MEMBERS);
	}

	public boolean canCreateRepository(User user, String groupOrRepo) {
		if (isOwnNamespace(user, groupOrRepo))
			return user.settings.canCreateRepositories;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.WRITE);
	}

	private boolean isOwnNamespace(User user, String groupOrRepo) {
		if (isGroup(groupOrRepo))
			return groupOrRepo.equals(user.username);
		String group = groupOrRepo.substring(0, groupOrRepo.indexOf(File.separator));
		return group.equals(user.username);
	}

	private boolean isGroup(String groupOrRepo) {
		return !groupOrRepo.contains(File.separator);
	}

}
