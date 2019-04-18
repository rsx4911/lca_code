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
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

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
	private final LibraryService libraryService;
	
	@Inject
	public DeleteService(UserService userService, TeamService teamService, MembershipService memberService,
			RepositoryService repoService, GroupService groupService, TaskService taskService,
			MessagingService messagingService, AccessService accessService, SearchService searchService,
			CommentService commentService, LibraryService libraryService) {
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
		this.libraryService = libraryService;
	}

	public void delete(User user) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.isUserManager())
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
		if (!currentUser.isUserManager())
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

	public void deleteGroup(String name) {
		if (!accessService.canDelete(name))
			throw new UnauthorizedAccessException(name, "DELETE");
		for (Repository repo : repoService.getAll(0, 0, name + "/", false).data) {
			delete(repo);
		}
		memberService.removeMemberships(name);
		groupService.delete(name);
	}

	public void deleteLibrary(String name) {
		for (Repository repo : repoService.getAllAccessible()) {
			repo.setRestriction(name, null);
		}
		libraryService.removeLibrary(name);
	}
	
}
