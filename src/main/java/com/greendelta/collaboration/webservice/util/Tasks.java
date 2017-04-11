package com.greendelta.collaboration.webservice.util;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.task.Task;

public class Tasks {

	private Tasks() {
		// only static access
	}

	public static ObjectMap map(Task task) {
		ObjectMap map = ObjectMap.fromObject(task);
		map.put("initiator", Users.mapForOthers(task.initiator));
		map.put("assignments", Client.map(task.assignments, TaskAssignments::map));
		map.put("type", task.getType().name());
		return map;
	}

}
