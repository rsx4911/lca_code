package com.greendelta.collaboration.service.task;

import java.util.Calendar;

import javax.ws.rs.core.Response.Status;

import org.openlca.cloud.error.ServerException;
import org.openlca.cloud.error.UnauthorizedAccessException;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;

public abstract class TaskExecutionService<T extends Task> {

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

	public void start(T task) {
		Repository repo = repoService.get(task.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		if (!task.assignments.isEmpty() || task.hasId())
			throw new ServerException(Status.CONFLICT, "Review object already exists");
		User user = userService.getCurrentUser();
		task.initiator = user;
		task.startDate = Calendar.getInstance().getTime();
		task.state = TaskState.CREATED;
		insert(task);
	}

	public void merge(T task) {
		T fromDb = get(task.getId());
		Repository repo = repoService.get(fromDb.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		fromDb.name = task.name;
		fromDb.comment = task.comment;
		update(fromDb);
	}

	public TaskAssignment startAssignment(T task, String username, TaskAssignmentCheck accessCheck) {
		User user = userService.getForUsername(username);
		Repository repo = repoService.get(task.repositoryPath);
		if (!accessCheck.canBeAssigned(user, repo))
			throw new UnauthorizedAccessException(repo.toId(), task.getClass().getSimpleName().toUpperCase());
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
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
		update(task);
		return assignment;
	}

	public TaskAssignment endAssignment(T task, String username, boolean canceled) {
		if (task.state != TaskState.PROCESSING)
			throw new ServerException(Status.CONFLICT, "Task is not in process state");
		User user = userService.getForUsername(username);
		User currentUser = userService.getCurrentUser();
		Repository repo = repoService.get(task.repositoryPath);
		if (!user.equals(currentUser) && !accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
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
		update(task);
		return assignment;
	}

	public void end(T task, TaskState state) {
		Repository repo = repoService.get(task.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		task.state = state;
		task.endDate = Calendar.getInstance().getTime();
		User currentUser = userService.getCurrentUser();
		for (TaskAssignment assignment : task.assignments) {
			if (assignment.endedBy != null)
				continue;
			assignment.endDate = task.endDate;
			assignment.canceled = true;
			assignment.endedBy = currentUser;
		}
		update(task);
	}

	public T get(long id) {
		T task = dao.get(id);
		User currentUser = userService.getCurrentUser();
		if (currentUser.equals(task.initiator))
			return task;
		for (TaskAssignment assignment : task.assignments)
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
		long lastId = dao.getLastId(TaskAssignment.class);
		for (TaskAssignment assignment : task.assignments) {
			if (assignment.hasId())
				continue;
			assignment.setId(++lastId);
		}
	}

	public static interface TaskAssignmentCheck {

		public boolean canBeAssigned(User user, Repository repo);

	}

}
