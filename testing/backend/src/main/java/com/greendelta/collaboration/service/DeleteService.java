package com.greendelta.collaboration.service;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class DeleteService {

	private final UserService userService;
	private final TeamService teamService;
	private final MembershipService memberService;
	private final RepositoryService repoService;
	private final GroupService groupService;
	private final TaskService taskService;
	private final MessagingService messagingService;
	private final CommentService commentService;
	private final ReleaseService releaseService;
	private final PermissionsService permissions;

	public DeleteService(UserService userService, TeamService teamService, MembershipService memberService,
			RepositoryService repoService, GroupService groupService, TaskService taskService,
			MessagingService messagingService, ReleaseService releaseService, CommentService commentService,
			PermissionsService permissions) {
		this.userService = userService;
		this.teamService = teamService;
		this.memberService = memberService;
		this.repoService = repoService;
		this.groupService = groupService;
		this.taskService = taskService;
		this.messagingService = messagingService;
		this.commentService = commentService;
		this.releaseService = releaseService;
		this.permissions = permissions;
	}

	public void delete(User user) {
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isUserManager())
			throw Response.forbidden("User " + user.id, Permission.DELETE);
		for (var repo : repoService.getAllAccessible()) {
			if (repo.group.equals(user.username)) {
				delete(repo);
			}
		}
		groupService.delete(user.username);
		teamService.getTeamsFor(user).forEach(team -> {
			teamService.removeMember(user, team);
		});
		memberService.removeMemberships(user);
		deleteTasksAndAssignmentsOf(user);
		messagingService.getMessages(user).forEach(message -> {
			messagingService.delete(message);
		});
		commentService.clearUser(user);
		userService.delete(user);
	}

	private void deleteTasksAndAssignmentsOf(User user) {
		taskService.getAllFor(user).forEach(task -> {
			if (user.equals(task.initiator)) {
				taskService.delete(task);
			} else {
				task.assignments.stream()
						.filter(a -> a.assignedTo.equals(user) || a.endedBy.equals(user))
						.forEach(a -> task.assignments.remove(a));
				if (task.assignments.isEmpty()) {
					task.state = TaskState.CREATED;
				} else {
					boolean stillActive = false;
					for (TaskAssignment assignment : task.assignments) {
						if (assignment.endDate == null) {
							stillActive = true;
							break;
						}
					}
					if (!stillActive) {
						task.state = TaskState.PROCESSING;
					}
				}
				taskService.update(task);
			}
		});
	}

	public void delete(Team team) {
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isUserManager())
			throw Response.forbidden("Team " + team.id, Permission.DELETE);
		memberService.removeMemberships(team);
		messagingService.getMessages(team).forEach(message -> {
			messagingService.delete(message);
		});
		teamService.delete(team);
	}

	public void delete(Repository repo) {
		if (!permissions.canDelete(repo.path()))
			throw Response.forbidden(repo.path(), Permission.DELETE);
		deleteTasksOf(repo);
		commentService.delete(repo);
		repo.settings.delete();
		releaseService.delete(repo.path());
		// TODO check if index is always updated when this is called
		repoService.delete(repo);
		memberService.removeMemberships(repo.path());
	}

	private void deleteTasksOf(Repository repo) {
		taskService.getAllFor(repo).forEach(task -> {
			taskService.delete(task);
		});
	}

	public void deleteGroup(String name) {
		if (!permissions.canDelete(name))
			throw Response.forbidden(name, Permission.DELETE);
		for (var repo : repoService.getAllAccessible()) {
			if (repo.group.equals(name)) {
				delete(repo);
			}
		}
		groupService.delete(name);
		memberService.removeMemberships(name);
	}

}
