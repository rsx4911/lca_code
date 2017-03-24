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

	public boolean canMove(String repository) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isGroup(repository))
			return false; // can not move groups
		Role role = membershipService.getRole(user, repository);
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

	public boolean canEditMembersOf(String groupOrRepo) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(Permission.EDIT_MEMBERS);
	}

	public boolean canCreateRepositoryIn(String group) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (isOwnNamespace(user, group))
			return user.settings.canCreateRepositories;
		Role role = membershipService.getRole(user, group);
		return role.getPermissions().contains(Permission.WRITE);
	}

	public boolean canRead(Comment comment) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (comment.user.equals(user))
			return true;
		if (!canRead(comment.repositoryPath))
			return false;
		if (comment.restrictedToRole != null) {
			Role role = membershipService.getRole(user, comment.repositoryPath);
			return comment.restrictedToRole.ordinal() <= role.ordinal();
		}
		return true;
	}

	public boolean canCommentIn(String repositoryPath) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (!canRead(repositoryPath))
			return false;
		Role role = membershipService.getRole(user, repositoryPath);
		return role.getPermissions().contains(Permission.COMMENT);
	}

	private boolean isOwnNamespace(User user, String groupOrRepo) {
		if (isGroup(groupOrRepo))
			return groupOrRepo.equalsIgnoreCase(user.username);
		String group = groupOrRepo.substring(0, groupOrRepo.indexOf(File.separator));
		return group.equalsIgnoreCase(user.username);
	}

	private boolean isGroup(String groupOrRepo) {
		return !groupOrRepo.contains(File.separator);
	}

}
