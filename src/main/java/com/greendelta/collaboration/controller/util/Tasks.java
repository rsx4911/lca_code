package com.greendelta.collaboration.controller.util;

import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.ObjectMap;

public class Tasks {

	private Tasks() {
		// only static access
	}

	public static ObjectMap map(Task task, Repository repo) {
		var map = ObjectMap.fromObject(task);
		map.put("repositoryLabel", repo.getLabel());
		map.put("initiator", Users.mapForOthers(task.initiator));
		map.put("assignments", task.assignments.stream().map(TaskAssignments::map).toList());
		map.put("type", task.getType().name());
		return map;
	}

}
