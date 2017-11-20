package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.error.RepositoryNotFoundException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;

public class AccessService {

	private final String repositoryPath;
	private final UserService userService;
	private final MembershipService membershipService;

	@Inject
	public AccessService(@Named("repository.path") String repositoryPath, UserService userService,
			MembershipService membershipService) {
		this.repositoryPath = repositoryPath;
		this.userService = userService;
		this.membershipService = membershipService;
	}

	public boolean canRead(String groupOrRepo) {
		return canRead(groupOrRepo, false);
	}

	public boolean canRead(String groupOrRepo, boolean ignoreAdmin) {
		if (isPublic(groupOrRepo))
			return true;
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

	public boolean canSetSettings(String groupOrRepo) {
		return hasPermissionTo(Permission.SET_SETTINGS, groupOrRepo);
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

	public List<Comment> filterCanRead(List<Comment> comments) {
		List<Comment> canRead = new ArrayList<>();
		User user = userService.getCurrentUser();
		Map<String, Role> userRoles = new HashMap<>();
		for (Comment comment : comments) {
			if (user.admin || comment.user.equals(user)) {
				canRead.add(comment);
				continue;
			}
			if (!canRead(comment.repositoryPath))
				continue;
			if (!comment.released)
				continue;
			if (comment.approvedBy == null && !canManageCommentsIn(comment.repositoryPath))
				continue;
			if ((comment.replyTo != null && comment.replyTo.user.equals(user)) || comment.restrictedToRole == null) {
				canRead.add(comment);
				continue;
			}
			Role role = userRoles.get(comment.repositoryPath);
			if (role == null) {
				role = membershipService.getRole(user, comment.repositoryPath);
				userRoles.put(comment.repositoryPath, role);
			}
			if (comment.restrictedToRole.ordinal() > role.ordinal())
				continue;
			canRead.add(comment);
		}
		return canRead;
	}

	public boolean canManage(Comment comment) {
		User user = userService.getCurrentUser();
		if (comment.user.equals(user))
			return true;
		return canManageCommentsIn(comment.repositoryPath);
	}

	public boolean canManageCommentsIn(String repositoryPath) {
		User user = userService.getCurrentUser();
		if (user.admin)
			return true;
		if (!canRead(repositoryPath))
			return false;
		return hasPermissionTo(user, Permission.MANAGE_COMMENTS, repositoryPath);
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
		String group = groupOrRepo.substring(0, groupOrRepo.indexOf("/"));
		return group.equalsIgnoreCase(user.username);
	}

	private boolean isGroup(String groupOrRepo) {
		return !groupOrRepo.contains("/");
	}

	private boolean isPublic(String groupOrRepo) {
		File dir = new File(repositoryPath, groupOrRepo);
		if (!isGroup(groupOrRepo)) {
			Repository repo = Repository.getIgnoreSchema(repositoryPath, dir.getParentFile().getName(), dir.getName());
			return repo.settings.publicAccess;
		}
		if (!dir.isDirectory() || dir.listFiles() == null)
			return false;
		for (File child : dir.listFiles()) {
			try {
				Repository repo = Repository.getIgnoreSchema(repositoryPath, groupOrRepo, child.getName());
				if (repo.settings.publicAccess)
					return true;
			} catch (RepositoryNotFoundException e) {
				// ignore
			}
		}
		return false;
	}

}
