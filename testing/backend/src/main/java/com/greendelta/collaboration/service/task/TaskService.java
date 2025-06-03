package com.greendelta.collaboration.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.model.task.TaskType;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;

@Service
public class TaskService {

	private Dao<Task> dao;

	public TaskService(Dao<Task> dao) {
		this.dao = dao;
	}

	public List<Task> getAllFor(User user) {
		var tasks = new ArrayList<Task>();
		if (user == null)
			return tasks;
		for (var type : TaskType.values()) {
			var jpql = "SELECT DISTINCT task FROM " + type.subclass.getSimpleName() + " task "
					+ "LEFT JOIN task.assignments assignment "
					+ "WHERE assignment.assignedTo = :user "
					+ "OR task.initiator = :user";
			tasks.addAll(dao.getAll(jpql, Map.of("user", user)));
		}
		return tasks;
	}

	public List<Task> getAllFor(Repository repo) {
		var tasks = new ArrayList<Task>();
		if (repo == null)
			return tasks;
		for (var type : TaskType.values()) {
			var jpql = "SELECT DISTINCT task FROM " + type.subclass.getSimpleName() + " task "
					+ "WHERE task.repositoryPath = :repoId";
			tasks.addAll(dao.getAll(jpql, Map.of("repoId", repo.path())));
		}
		return tasks;
	}

	public List<Task> getAllActiveFor(User user) {
		var active = new ArrayList<Task>();
		getAllFor(user).forEach(task -> {
			if ((task.state == TaskState.CREATED || task.state == TaskState.VERIFYING) && task.initiator.equals(user)) {
				active.add(task);
			} else if (task.state == TaskState.PROCESSING) {
				for (var assignment : task.assignments) {
					if (!assignment.assignedTo.equals(user))
						continue;
					if (assignment.endDate != null)
						continue;
					active.add(task);
					break;
				}
			}
		});
		return active;
	}

	public void move(Repository from, Repository to) {
		getAllFor(from).forEach(task -> {
			task.repositoryPath = to.path();
			update(task);
		});
	}

	public Task update(Task task) {
		return dao.update(task);
	}

	public void delete(Task task) {
		dao.delete(task);
	}

}
