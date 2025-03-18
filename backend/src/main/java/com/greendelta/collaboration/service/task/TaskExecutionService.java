package com.greendelta.collaboration.service.task;

import java.util.Calendar;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.UserService;

public abstract class TaskExecutionService<T extends Task> {

	private final Dao<T> dao;
	private final Dao<TaskAssignment> assignmentDao;
	private final UserService userService;
	private final RepositoryService repoService;
	private final PermissionsService permissions;

	protected TaskExecutionService(Dao<T> dao, Dao<TaskAssignment> assignmentDao, UserService userService, RepositoryService repoService,
			PermissionsService permissions) {
		this.dao = dao;
		this.assignmentDao = assignmentDao;
		this.userService = userService;
		this.repoService = repoService;
		this.permissions = permissions;
	}

	public void start(T task) {
		try (var repo = repoService.get(task.repositoryPath)) {
			if (!permissions.canManageTaskIn(repo.path()))
				throw Response.forbidden(repo.path(), Permission.MANAGE_TASK);
		}
		var user = userService.getCurrentUser();
		task.initiator = user;
		task.startDate = Calendar.getInstance().getTime();
		task.state = TaskState.CREATED;
		insert(task);
	}

	public void merge(T task) {
		var fromDb = get(task.id);
		try (var repo = repoService.get(fromDb.repositoryPath)) {
			if (!permissions.canManageTaskIn(repo.path()))
				throw Response.forbidden(repo.path(), Permission.MANAGE_TASK);
		}
		fromDb.name = task.name;
		fromDb.comment = task.comment;
		update(fromDb);
	}

	public TaskAssignment startAssignment(T task, String username, TaskAssignmentCheck accessCheck) {
		var user = userService.getForUsername(username);
		try (var repo = repoService.get(task.repositoryPath)) {
			if (!accessCheck.canBeAssigned(user, repo))
				throw Response.forbidden(repo.path(), Permission.MANAGE_TASK);
			if (!permissions.canManageTaskIn(repo.path()))
				throw Response.forbidden(repo.path(), Permission.MANAGE_TASK);
		}
		var assignment = new TaskAssignment();
		assignment.assignedTo = user;
		assignment.startDate = Calendar.getInstance().getTime();
		assignment.iteration = 1;
		for (var a : task.assignments) {
			if (!a.assignedTo.equals(user))
				continue;
			if (a.endDate == null)
				throw Response.conflict("User " + user.username + " already has an active assignment");
			assignment.iteration++;
		}
		task.assignments.add(assignment);
		task.state = TaskState.PROCESSING;
		update(task);
		return assignment;
	}

	public TaskAssignment endAssignment(T task, String username, boolean canceled) {
		var user = userService.getForUsername(username);
		var currentUser = userService.getCurrentUser();
		try (var repo = repoService.get(task.repositoryPath)) {
			if (!user.equals(currentUser) && !permissions.canManageTaskIn(repo.path()))
				throw Response.forbidden(repo.path(), Permission.MANAGE_TASK);
		}
		TaskAssignment assignment = null;
		var isLastOpen = true;
		for (var a : task.assignments) {
			if (a.endDate != null)
				continue;
			if (!a.assignedTo.equals(user)) {
				isLastOpen = false;
				continue;
			}
			assignment = a;
		}
		if (assignment == null)
			return null;
		assignment.endDate = Calendar.getInstance().getTime();
		assignment.canceled = canceled;
		assignment.endedBy = userService.getCurrentUser();
		if (isLastOpen) {
			task.state = TaskState.VERIFYING;
		}
		update(task);
		return assignment;
	}

	public void end(T task, TaskState state) {
		try (var repo = repoService.get(task.repositoryPath)) {
			if (!permissions.canManageTaskIn(repo.path()))
				throw Response.forbidden(repo.path(), Permission.MANAGE_TASK);
		}
		task.state = state;
		task.endDate = Calendar.getInstance().getTime();
		var currentUser = userService.getCurrentUser();
		task.assignments.stream()
				.filter(a -> a.endedBy == null)
				.forEach(a -> {
					a.endDate = task.endDate;
					a.canceled = true;
					a.endedBy = currentUser;
				});
		update(task);
	}

	public T get(long id) {
		var task = dao.get(id);
		var currentUser = userService.getCurrentUser();
		if (currentUser.equals(task.initiator))
			return task;
		for (var assignment : task.assignments)
			if (assignment.assignedTo.equals(currentUser))
				return task;
		return null;
	}

	private T insert(T task) {
		setTaskAssignmentIds(task);
		return dao.insert(task);
	}

	private T update(T task) {
		setTaskAssignmentIds(task);
		return dao.update(task);
	}

	private void setTaskAssignmentIds(T task) {
		var lastId = assignmentDao.getLastId();
		for (var assignment : task.assignments) {
			if (assignment.id != 0)
				continue;
			assignment.id = ++lastId;
		}
	}

	public static interface TaskAssignmentCheck {

		public boolean canBeAssigned(User user, Repository repo);

	}

}
