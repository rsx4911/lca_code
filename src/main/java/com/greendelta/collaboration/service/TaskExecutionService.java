package com.greendelta.collaboration.service;

import java.util.Calendar;

import javax.ws.rs.core.Response.Status;

import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.cloud.error.ServerException;
import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.common.base.Strings;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;

abstract class TaskExecutionService<T extends Task> {

	private final Dao<T> dao;
	private final UserService userService;
	private final RepositoryService repoService;
	private final AccessService accessService;

	protected TaskExecutionService(Dao<T> dao, UserService userService, RepositoryService repoService,
			AccessService accessService) {
		this.dao = dao;
		this.userService = userService;
		this.repoService = repoService;
		this.accessService = accessService;
	}

	public T start(T task) {
		Repository repo = getRepository(task.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		if (!task.assignments.isEmpty() || task.hasId())
			throw new ServerException(Status.CONFLICT, "Review object already exists");
		User user = userService.getCurrentUser();
		task.initiator = user;
		task.startDate = Calendar.getInstance().getTime();
		task.state = TaskState.CREATED;
		return insert(task);
	}

	public T merge(T task) {
		T fromDb = get(task.getId());
		Repository repo = getRepository(fromDb.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		fromDb.name = task.name;
		fromDb.comment = task.comment;
		return update(fromDb);
	}

	public T startAssignment(T task, String username, TaskAssignmentCheck accessCheck) {
		User user = userService.getForUsername(username);
		Repository repo = getRepository(task.repositoryPath);
		if (!accessCheck.canBeAssigned(user, repo))
			throw new UnauthorizedAccessException(repo.toId(), task.getClass().getSimpleName().toUpperCase());
		TaskAssignment assignment = new TaskAssignment();
		assignment.assignedTo = user;
		assignment.startDate = Calendar.getInstance().getTime();
		assignment.iteration = 1;
		for (TaskAssignment a : task.assignments) {
			if (!a.assignedTo.equals(user))
				continue;
			if (a.endDate == null)
				throw new ServerException(Status.CONFLICT, "User " + user.username
						+ " already has an active assignment");
			assignment.iteration++;
		}
		task.assignments.add(assignment);
		task.state = TaskState.PROCESSING;
		return update(task);
	}

	public T endAssignment(T task, String username, boolean canceled) {
		if (task.state != TaskState.PROCESSING)
			throw new ServerException(Status.CONFLICT, "Task is not in process state");
		User user = userService.getForUsername(username);
		TaskAssignment assignment = null;
		boolean isLastOpen = true;
		for (TaskAssignment a : task.assignments) {
			if (a.endDate != null)
				continue;
			if (!a.assignedTo.equals(user)) {
				isLastOpen = false;
				continue;
			}
			assignment = a;
		}
		if (assignment == null)
			throw new ServerException(Status.NOT_FOUND, "User " + user.username + " has no active assignment");
		assignment.endDate = Calendar.getInstance().getTime();
		assignment.canceled = canceled;
		assignment.endedBy = userService.getCurrentUser();
		if (isLastOpen) {
			task.state = TaskState.VERIFYING;
		}
		return update(task);
	}

	public T end(T task, TaskState state) {
		Repository repo = getRepository(task.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		task.state = state;
		User currentUser = userService.getCurrentUser();
		for (TaskAssignment assignment: task.assignments) {
			assignment.endDate = Calendar.getInstance().getTime();
			assignment.canceled = true;
			assignment.endedBy = currentUser;
		}
		return update(task);
	}

	private Repository getRepository(String path) {
		if (Strings.isNullOrEmpty(path))
			throw new RepositoryNotFoundException("");
		if (!path.contains("/"))
			throw new RepositoryNotFoundException(path);
		String[] split = path.split("/");
		return repoService.get(split[0], split[1]);
	}

	public T get(long id) {
		return dao.get(id);
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
		for (TaskAssignment assignment : task.assignments) {
			if (assignment.hasId())
				continue;
			assignment.setId(dao.getNewId(TaskAssignment.class));
		}
	}

	public static interface TaskAssignmentCheck {

		public boolean canBeAssigned(User user, Repository repo);

	}

}
