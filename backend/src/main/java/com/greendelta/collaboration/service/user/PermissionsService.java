package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class PermissionsService {

	private final UserService userService;
	private final MembershipService membershipService;
	private final GroupService groupService;

	public PermissionsService(UserService userService, MembershipService membershipService, SettingsService settings) {
		this.userService = userService;
		this.membershipService = membershipService;
		// cannot inject group service - would result in a dependency loop
		this.groupService = new GroupService(this, membershipService, userService, settings);
	}

	public boolean canRead(String groupOrRepo) {
		var user = userService.getCurrentUser();
		if (user.isDataManager())
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		if (isGroup(groupOrRepo))
			if (membershipService.hasMembershipInAnyRepoInGroup(user, groupOrRepo))
				return true;
		if (!repositoryIsActive(groupOrRepo))
			return false;
		return hasPermissionTo(user, Permission.READ, groupOrRepo);
	}

	private boolean repositoryIsActive(String repository) {
		if (!repository.contains("/"))
			return true; // groups are always active
		var group = repository.split("/")[0];
		if (userService.exists(group)) {
			var user = userService.getForUsername(group);
			return !user.isDeactivated();
		}
		var memberships = membershipService.getMemberships(repository);
		for (var membership : memberships) {
			if (membership.role != Role.OWNER)
				continue;
			if (membership.user != null && !membership.user.isDeactivated())
				return true;
			if (membership.team == null)
				continue;
			for (var user : membership.team.users)
				if (!user.isDeactivated())
					return true;
		}
		return false;
	}

	public boolean canWriteTo(String groupOrRepo) {
		return hasPermissionTo(Permission.WRITE, groupOrRepo);
	}

	public boolean canSetSettingsOf(String groupOrRepo) {
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
		var user = userService.getCurrentUser();
		if (isOwnNamespace(user, group)) {
			if (!user.settings.canCreateRepositories)
				return false;
			var noOfRepos = user.settings.noOfRepositories;
			return noOfRepos == 0 || noOfRepos > groupService.getRepositoryCount(user.username);
		}
		if (!hasPermissionTo(Permission.CREATE, group))
			return false;
		var groupSettings = groupService.getSettings(group);
		var noOfRepos = groupSettings.get(GroupSetting.NO_OF_REPOSITORIES, 0);
		return noOfRepos == 0 || noOfRepos > groupService.getRepositoryCount(group);
	}

	public List<Comment> filterCanRead(List<Comment> comments) {
		var canRead = new ArrayList<Comment>();
		var user = userService.getCurrentUser();
		var userRoles = new HashMap<String, Role>();
		for (var comment : comments) {
			if (user.isDataManager() || user.equals(comment.user)) {
				canRead.add(comment);
				continue;
			}
			if (!canRead(comment.repositoryPath))
				continue;
			if (!comment.released)
				continue;
			if (!comment.approved && !canManageCommentsIn(comment.repositoryPath))
				continue;
			if ((comment.replyTo != null && comment.replyTo.user.equals(user)) || comment.restrictedToRole == null) {
				canRead.add(comment);
				continue;
			}
			var role = userRoles.get(comment.repositoryPath);
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
		var user = userService.getCurrentUser();
		if (comment.user != null && comment.user.equals(user))
			return true;
		return canManageCommentsIn(comment.repositoryPath);
	}

	public boolean canManageCommentsIn(String repositoryPath) {
		var user = userService.getCurrentUser();
		if (user.isDataManager())
			return true;
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

	public boolean canCreateChangeLogOf(String repositoryPath) {
		return hasPermissionTo(Permission.CREATE_CHANGE_LOG, repositoryPath);
	}

	public boolean canCreateReleasesIn(String repositoryPath) {
		return hasPermissionTo(Permission.MANAGE_RELEASES, repositoryPath);
	}

	private boolean hasPermissionTo(Permission permission, String groupOrRepo) {
		var user = userService.getCurrentUser();
		return hasPermissionTo(user, permission, groupOrRepo);
	}

	private boolean hasPermissionTo(User user, Permission permission, String groupOrRepo) {
		if (permission != Permission.REVIEW && user.isDataManager())
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		var role = membershipService.getRole(user, groupOrRepo);
		return role.getPermissions().contains(permission);
	}

	public boolean isOwnNamespace(User user, String groupOrRepo) {
		if (isGroup(groupOrRepo))
			return groupOrRepo.equalsIgnoreCase(user.username);
		var group = groupOrRepo.substring(0, groupOrRepo.indexOf("/"));
		return group.equalsIgnoreCase(user.username);
	}

	private boolean isGroup(String groupOrRepo) {
		return !groupOrRepo.contains("/");
	}

}
