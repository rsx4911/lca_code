package com.greendelta.collaboration.service.user;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.error.RepositoryNotFoundException;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class AccessService {

	private final UserService userService;
	private final MembershipService membershipService;
	private final SettingsService settingsService;

	@Autowired
	public AccessService(UserService userService, MembershipService membershipService,
			SettingsService settingsService) {
		this.userService = userService;
		this.membershipService = membershipService;
		this.settingsService = settingsService;
	}

	public boolean canRead(String groupOrRepo) {
		return canRead(groupOrRepo, false);
	}

	public boolean canRead(String groupOrRepo, boolean ignoreDataManager) {
		if (isPublic(groupOrRepo))
			return true;
		var user = userService.getCurrentUser();
		if (!ignoreDataManager && user.isDataManager())
			return true;
		if (isOwnNamespace(user, groupOrRepo))
			return true;
		if (isGroup(groupOrRepo))
			if (membershipService.hasMembershipInAnyRepoInGroup(user, groupOrRepo))
				return true;
		if (!repositoryIsActive(groupOrRepo))
			return false;
		return hasPermissionTo(user, Permission.READ, groupOrRepo, ignoreDataManager);
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
		var user = userService.getCurrentUser();
		if (isOwnNamespace(user, group)) {
			if (!user.settings.canCreateRepositories)
				return false;
			var noOfRepos = user.settings.noOfRepositories;
			String path = settingsService.get(ServerSetting.REPOSITORY_PATH);
			return noOfRepos == 0 || noOfRepos > userService.getNoOfRepositories(user, path);
		}
		return hasPermissionTo(Permission.CREATE, group);
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
		if (comment.user.equals(user))
			return true;
		return canManageCommentsIn(comment.repositoryPath);
	}

	public boolean canManageCommentsIn(String repositoryPath) {
		var user = userService.getCurrentUser();
		if (user.isDataManager())
			return true;
		if (!canRead(repositoryPath))
			return false;
		return hasPermissionTo(user, Permission.MANAGE_COMMENTS, repositoryPath);
	}

	public boolean canCommentIn(String repositoryPath) {
		return hasPermissionTo(Permission.COMMENT, repositoryPath);
	}

	public boolean canReviewIn(User user, String repositoryPath) {
		return hasPermissionTo(user, Permission.REVIEW, repositoryPath, true);
	}

	public boolean canManageTaskIn(String repositoryPath) {
		return hasPermissionTo(Permission.MANAGE_TASK, repositoryPath);
	}

	public boolean canCreateChangeLog(String repositoryPath) {
		return hasPermissionTo(Permission.CAN_CREATE_CHANGE_LOG, repositoryPath);
	}

	private boolean hasPermissionTo(Permission permission, String groupOrRepo) {
		var user = userService.getCurrentUser();
		return hasPermissionTo(user, permission, groupOrRepo);
	}

	private boolean hasPermissionTo(User user, Permission permission, String groupOrRepo) {
		return hasPermissionTo(user, permission, groupOrRepo, false);
	}

	private boolean hasPermissionTo(User user, Permission permission, String groupOrRepo, boolean ignoreDataManager) {
		if (!ignoreDataManager && user.isDataManager())
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

	private boolean isPublic(String groupOrRepo) {
		if (!settingsService.is(ServerSetting.PUBLIC_REPOSITORY_ENABLED))
			return false;
		String repositoryPath = settingsService.get(ServerSetting.REPOSITORY_PATH);
		if (repositoryPath == null)
			return false;
		var dir = new File(repositoryPath, groupOrRepo);
		if (!isGroup(groupOrRepo)) {
			try {
				return settingsService.is(RepositorySetting.PUBLIC_ACCESS, groupOrRepo);
			} catch (UnsupportedSchemaException e) {
				return false;
			}
		}
		if (!dir.isDirectory() || dir.listFiles() == null)
			return false;
		for (var child : dir.listFiles()) {
			try {
				var path = new RepositoryPath(groupOrRepo, child.getName()).toString();
				if (settingsService.is(RepositorySetting.PUBLIC_ACCESS, path))
					return true;
			} catch (RepositoryNotFoundException | UnsupportedSchemaException e) {
				// ignore
			}
		}
		return false;
	}

}
