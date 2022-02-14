package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;

public class Tasks {

	private Tasks() {
		// only static access
	}

	public static Map<String, Object> map(Task task, Repository repo) {
		var map = Maps.of(task);
		map.put("repositoryLabel", repo.getLabel());
		map.put("initiator", Users.mapForOthers(task.initiator));
		map.put("assignments", task.assignments.stream().map(TaskAssignments::map).toList());
		map.put("type", task.getType().name());
		return map;
	}

}
