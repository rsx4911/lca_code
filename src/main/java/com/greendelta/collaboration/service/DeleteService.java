package com.greendelta.collaboration.service;

import java.util.ArrayList;

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;

public class DeleteService {

	private final UserService userService;
	private final TeamService teamService;
	private final MembershipService memberService;
	private final RepositoryService repoService;
	private final GroupService groupService;
	private final TaskService taskService;
	private final MessagingService messagingService;
	private final AccessService accessService;
	private final SearchService searchService;
	private final CommentService commentService;

	@Inject
	public DeleteService(UserService userService, TeamService teamService, MembershipService memberService,
			RepositoryService repoService, GroupService groupService, TaskService taskService,
			MessagingService messagingService, AccessService accessService, SearchService searchService,
			CommentService commentService) {
		this.userService = userService;
		this.teamService = teamService;
		this.memberService = memberService;
		this.repoService = repoService;
		this.groupService = groupService;
		this.taskService = taskService;
		this.messagingService = messagingService;
		this.accessService = accessService;
		this.searchService = searchService;
		this.commentService = commentService;
	}

	public void delete(User user) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin)
			throw new UnauthorizedAccessException("User " + user.getId(), "DELETE");
		for (Repository repository : repoService.getAll(0, 0, user.username + "/", false).data) {
			delete(repository);
		}
		groupService.delete(user.username);
		for (Team team : teamService.getTeamsFor(user)) {
			teamService.removeMember(user, team);
		}
		memberService.removeMemberships(user);
		deleteTasksAndAssignmentsOf(user);
		for (Message message : messagingService.getMessages(user)) {
			messagingService.delete(message);
		}
		commentService.clearUser(user);
		userService.delete(user);
	}

	private void deleteTasksAndAssignmentsOf(User user) {
		for (Task task : taskService.getAllFor(user)) {
			if (user.equals(task.initiator)) {
				taskService.delete(task);
			} else {
				for (TaskAssignment assignment : new ArrayList<>(task.assignments)) {
					if (assignment.assignedTo.equals(user)) {
						task.assignments.remove(assignment);
					} else if (assignment.endedBy.equals(user)) {
						task.assignments.remove(assignment);
					}

				}
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
		}
	}

	public void delete(Team team) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin)
			throw new UnauthorizedAccessException("Team " + team.getId(), "DELETE");
		memberService.removeMemberships(team);
		teamService.delete(team);
		for (Message message : messagingService.getMessages(team)) {
			messagingService.delete(message);
		}
	}

	public void delete(Repository repo) {
		if (!accessService.canDelete(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "DELETE");
		memberService.removeMemberships(repo.toId());
		searchService.remove(searchService.getAll(repo));
		deleteTasksOf(repo);
		repoService.delete(repo);
	}

	private void deleteTasksOf(Repository repo) {
		for (Task task : taskService.getAllFor(repo)) {
			taskService.delete(task);
		}
	}

	public void delete(String group) {
		if (!accessService.canDelete(group))
			throw new UnauthorizedAccessException(group, "DELETE");
		for (Repository repo : repoService.getAll(0, 0, group + "/", false).data) {
			delete(repo);
		}
		memberService.removeMemberships(group);
		groupService.delete(group);
	}

}
