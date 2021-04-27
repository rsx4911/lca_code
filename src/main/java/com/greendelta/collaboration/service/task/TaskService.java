package com.greendelta.collaboration.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.model.task.TaskType;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;

public class TaskService {

	private Dao<Task> dao;

	@Inject
	public TaskService(Dao<Task> dao) {
		this.dao = dao;
	}

	public List<Task> getAllFor(User user) {
		List<Task> tasks = new ArrayList<>();
		for (TaskType type : TaskType.values()) {
			String jpql = "SELECT DISTINCT task FROM " + type.subclass.getSimpleName() + " task "
					+ "LEFT JOIN task.assignments assignment "
					+ "WHERE assignment.assignedTo = :user "
					+ "OR task.initiator = :user";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("user", user);
			tasks.addAll(dao.getAll(jpql, parameters));
		}
		return tasks;
	}

	public List<Task> getAllFor(Repository repo) {
		List<Task> tasks = new ArrayList<>();
		for (TaskType type : TaskType.values()) {
			String jpql = "SELECT DISTINCT task FROM " + type.subclass.getSimpleName() + " task "
					+ "WHERE task.repositoryPath = :repoId";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("repoId", repo.toId());
			tasks.addAll(dao.getAll(jpql, parameters));
		}
		return tasks;
	}

	public List<Task> getAllActiveFor(User user) {
		List<Task> all = getAllFor(user);
		List<Task> active = new ArrayList<>();
		for (Task task : all) {
			if ((task.state == TaskState.CREATED || task.state == TaskState.VERIFYING) && task.initiator.equals(user)) {
				active.add(task);
			} else if (task.state == TaskState.PROCESSING) {
				for (TaskAssignment assignment : task.assignments) {
					if (!assignment.assignedTo.equals(user))
						continue;
					if (assignment.endDate != null)
						continue;
					active.add(task);
					break;
				}
			}
		}
		return active;
	}

	public void move(Repository from, Repository to) {
		List<Task> tasks = getAllFor(from);
		for (Task task : tasks) {
			task.repositoryPath = to.toId();
			update(task);
		}
	}

	public Task update(Task task) {
		return dao.update(task);
	}

	public void delete(Task task) {
		dao.delete(task);
	}

}
