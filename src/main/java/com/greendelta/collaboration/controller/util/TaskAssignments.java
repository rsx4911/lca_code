package com.greendelta.collaboration.controller.util;

import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.util.ObjectMap;

public class TaskAssignments {

	private TaskAssignments() {
		// only static access
	}

	public static ObjectMap map(TaskAssignment assignment) {
		var map = ObjectMap.fromObject(assignment);
		map.put("assignedTo", Users.mapForOthers(assignment.assignedTo));
		map.put("endedBy", Users.mapForOthers(assignment.endedBy));
		return map;
	}

}
