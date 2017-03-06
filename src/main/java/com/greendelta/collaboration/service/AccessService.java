package com.greendelta.collaboration.service;

import java.io.File;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;

public class AccessService {

	private final UserService userService;
	private final MembershipService membershipService;

	@Inject
	public AccessService(UserService userService, MembershipService membershipService) {
		this.userService = userService;
		this.membershipService = membershipService;
	}

	public boolean canRead(String groupOrRepo) {
		return canRead(groupOrRepo, false);
	}

	public boolean canRead(String groupOrRepo, boolean ignoreAdmin) {
		User user = userService.getCurrentUser();
		if (!ignoreAdmin && user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		if (isGroup(groupOrRepo))
			if (membershipService.hasMembershipInAnyRepoInGroup(user, groupOrRepo))
				return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.READ);
	}

	public boolean canWrite(String groupOrRepo) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.WRITE);
	}

	public boolean canMove(String groupOrRepo) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isGroup(groupOrRepo))
			return false; // can not move groups
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.MOVE);
	}

	public boolean canDelete(String groupOrRepo) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.DELETE);
	}

	public boolean canEditMembers(String groupOrRepo) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.EDIT_MEMBERS);
	}

	public boolean canCreateRepository(String groupOrRepo) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return user.settings.canCreateRepositories;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.WRITE);
	}

	public boolean canRead(Comment comment) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		canRead(comment.repositoryPath);
		if (!comment.restrictedTo.isEmpty())
			return comment.restrictedTo.contains(user);
		if (comment.restrictedToRole != null) {
			Role role = membershipService.getRole(user, comment.repositoryPath);
			return comment.restrictedToRole == role;
		}
		return true;
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
