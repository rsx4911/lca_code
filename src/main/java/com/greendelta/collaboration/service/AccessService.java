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
		return hasPermissionTo(user, Permission.READ, groupOrRepo, ignoreAdmin);
	}

	public boolean canWrite(String groupOrRepo) {
		return hasPermissionTo(Permission.WRITE, groupOrRepo);
	}

	public boolean canMove(String repository) {
		if (isGroup(repository))
			return false; // can not move groups
		return hasPermissionTo(Permission.MOVE, repository);
	}

	public boolean canDelete(String groupOrRepo) {
		return hasPermissionTo(Permission.DELETE, groupOrRepo);
	}

	public boolean canEditMembersOf(String groupOrRepo) {
		return hasPermissionTo(Permission.EDIT_MEMBERS, groupOrRepo);
	}

	public boolean canCreateRepositoryIn(String group) {
		User user = userService.getCurrentUser();
		if (isOwnNamespace(user, group))
			return user.settings.canCreateRepositories;
		return hasPermissionTo(Permission.WRITE, group);
	}

	public boolean canRead(Comment comment) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (comment.user.equals(user))
			return true;
		if (!canRead(comment.repositoryPath))
			return false;
		if (comment.replyTo.user.equals(user))
			return true;
		if (comment.restrictedToRole != null) {
			Role role = membershipService.getRole(user, comment.repositoryPath);
			return comment.restrictedToRole.ordinal() <= role.ordinal();
		}
		return true;
	}

	public boolean canCommentIn(String repositoryPath) {
		return hasPermissionTo(Permission.COMMENT, repositoryPath);
	}

	public boolean canReviewIn(User user, String repositoryPath) {
		return hasPermissionTo(user, Permission.REVIEW, repositoryPath);
	}

	public boolean canManageTaskIn(String repositoryPath) {
		return hasPermissionTo(Permission.MANAGE_TASK, repositoryPath);
	}

	private boolean hasPermissionTo(Permission permission, String groupOrRepo) {
		User user = userService.getCurrentUser();
		return hasPermissionTo(user, permission, groupOrRepo);
	}

	private boolean hasPermissionTo(User user, Permission permission, String groupOrRepo) {
		return hasPermissionTo(user, permission, groupOrRepo, false);
	}

	private boolean hasPermissionTo(User user, Permission permission, String groupOrRepo, boolean ignoreAdmin) {
		if (!ignoreAdmin && user.admin)
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		Role role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(permission);
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
