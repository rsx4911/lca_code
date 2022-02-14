package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.util.Maps;

public class TaskAssignments {

	private TaskAssignments() {
		// only static access
	}

	public static Map<String, Object> map(TaskAssignment assignment) {
		var map = Maps.of(assignment);
		map.put("assignedTo", Users.mapForOthers(assignment.assignedTo));
		map.put("endedBy", Users.mapForOthers(assignment.endedBy));
		return map;
	}

}
