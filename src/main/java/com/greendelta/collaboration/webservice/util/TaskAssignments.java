package com.greendelta.collaboration.webservice.util;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.task.TaskAssignment;

public class TaskAssignments {

	private TaskAssignments() {
		// only static access
	}

	public static ObjectMap map(TaskAssignment assignment) {
		ObjectMap map = ObjectMap.fromObject(assignment);
		map.put("assignedTo", Users.mapForOthers(assignment.assignedTo));
		return map;
	}

}
