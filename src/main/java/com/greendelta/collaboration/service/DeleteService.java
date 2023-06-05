package com.greendelta.collaboration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.error.ForbiddenAccessException;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.RestrictionService;
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
	private final AccessService accessService;
	private final CommentService commentService;
	private final RestrictionService restrictionService;

	@Autowired
	public DeleteService(UserService userService, TeamService teamService, MembershipService memberService,
			RepositoryService repoService, GroupService groupService, TaskService taskService,
			MessagingService messagingService, AccessService accessService, CommentService commentService,
			RestrictionService restrictionService) {
		this.userService = userService;
		this.teamService = teamService;
		this.memberService = memberService;
		this.repoService = repoService;
		this.groupService = groupService;
		this.taskService = taskService;
		this.messagingService = messagingService;
		this.accessService = accessService;
		this.commentService = commentService;
		this.restrictionService = restrictionService;
	}

	public void delete(User user) {
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isUserManager())
			throw new ForbiddenAccessException("User " + user.id, "DELETE");
		try (var result = repoService.getAll(0, 0, user.username + "/", false, false)) {
			result.data.forEach(this::delete);
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
			throw new ForbiddenAccessException("Team " + team.id, "DELETE");
		memberService.removeMemberships(team);
		messagingService.getMessages(team).forEach(message -> {
			messagingService.delete(message);
		});
		teamService.delete(team);
	}

	public void delete(Repository repo) {
		if (!accessService.canDelete(repo.path()))
			throw new ForbiddenAccessException(repo.path(), "DELETE");
		deleteTasksOf(repo);
		commentService.delete(repo);
		repo.settings.delete();
		repoService.delete(repo);
		memberService.removeMemberships(repo.path());
	}

	private void deleteTasksOf(Repository repo) {
		taskService.getAllFor(repo).forEach(task -> {
			taskService.delete(task);
		});
	}

	public void deleteGroup(String name) {
		if (!accessService.canDelete(name))
			throw new ForbiddenAccessException(name, "DELETE");
		try (var result = repoService.getAll(0, 0, name + "/", false, false)) {
			for (Repository repo : result.data) {
				delete(repo);
			}
		}
		groupService.delete(name);
		memberService.removeMemberships(name);
	}

	public void deleteRestriction(String name) {
		try (var accessible = repoService.getAllAccessible()) {
			accessible.forEach(repo -> repoService.setRestriction(repo, name, null));
			restrictionService.delete(restrictionService.getForName(name));
		}
	}

}
