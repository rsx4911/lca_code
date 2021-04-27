package com.greendelta.collaboration.webservice.util;

import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.ObjectMap;

public class Tasks {

	private Tasks() {
		// only static access
	}

	public static ObjectMap map(Task task, Repository repo) {
		ObjectMap map = ObjectMap.fromObject(task);
		map.put("repositoryLabel", repo.getLabel());
		map.put("initiator", Users.mapForOthers(task.initiator));
		map.put("assignments", Client.map(task.assignments, TaskAssignments::map));
		map.put("type", task.getType().name());
		return map;
	}

}
