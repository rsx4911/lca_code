package com.greendelta.collaboration.controller.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openlca.git.model.Commit;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Maps;

public class Activities {

	private Activities() {
		// only static access
	}

	public static Map<String, Object> map(Commit commit, Repository repo) {
		var map = Maps.create();
		map.put("type", ActivityType.COMMIT);
		map.put("timestamp", commit.timestamp);
		map.put("id", commit.id);
		map.put("repositoryPath", repo.path());
		map.put("label", repo.getLabel());
		map.put("message", commit.message);
		map.put("user", commit.user);
		return map;
	}

	public static Map<String, Object> map(Comment comment, Repository repo) {
		var map = Maps.create();
		map.put("type", ActivityType.COMMENT);
		map.put("timestamp", comment.date.getTime());
		map.put("id", comment.id);
		map.put("repositoryPath", comment.repositoryPath);
		map.put("label", repo.getLabel());
		map.put("message", comment.text);
		if (comment.user != null) {
			map.put("user", comment.user.username);
			map.put("userDisplayName", comment.user.name);
		} else {
			map.put("user", "anonymous");
			map.put("userDisplayName", "Anonymous");
		}
		map.put("field", Maps.of(comment.field));
		map.put("reply", comment.replyTo != null);
		return map;
	}

	public static List<Map<String, Object>> map(Task task, Repository repo) {
		var activities = new ArrayList<Map<String, Object>>();
		activities.add(map(task, task.startDate, ActivityType.TASK_STARTED, repo));
		// previously tasks were missing to set the end date, so check for
		// status
		if (task.state == TaskState.COMPLETED || task.state == TaskState.CANCELED) {
			var type = task.state == TaskState.CANCELED
					? ActivityType.TASK_CANCELED
					: ActivityType.TASK_COMPLETED;
			activities.add(map(task, task.endDate, type, repo));
		}
		task.assignments.forEach(assignment -> activities.addAll(map(task, assignment, repo)));
		return activities;
	}

	private static Map<String, Object> map(Task task, Date date, ActivityType type, Repository repo) {
		var map = Maps.create();
		map.put("type", type);
		// previously tasks were missing to set the end date, take the latest
		// date and add a second
		map.put("timestamp", date != null ? date.getTime() : getLatestDate(task));
		map.put("id", task.id);
		map.put("repositoryPath", task.repositoryPath);
		map.put("label", repo.getLabel());
		map.put("message", task.name);
		map.put("taskType", task.getType());
		map.put("user", task.initiator.username);
		map.put("userDisplayName", task.initiator.name);
		return map;
	}

	private static long getLatestDate(Task task) {
		var dates = new ArrayList<Date>();
		dates.add(task.startDate);
		dates.add(task.endDate);
		task.assignments.forEach(assignment -> {
			dates.add(assignment.startDate);
			dates.add(assignment.endDate);
		});
		var date = Dates.getLatest(dates.toArray(new Date[dates.size()]));
		var cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
			cal.add(Calendar.SECOND, 1);
		}
		return cal.getTime().getTime();
	}

	private static List<Map<String, Object>> map(Task task, TaskAssignment assignment, Repository repo) {
		var activities = new ArrayList<Map<String, Object>>();
		activities.add(map(task, assignment, assignment.startDate, ActivityType.TASK_ASSIGNED, repo));
		if (assignment.endDate != null) {
			ActivityType type = assignment.canceled ? ActivityType.TASK_ASSIGNMENT_CANCELED
					: ActivityType.TASK_ASSIGNMENT_COMPLETED;
			activities.add(map(task, assignment, assignment.endDate, type, repo));
		}
		return activities;
	}

	private static Map<String, Object> map(Task task, TaskAssignment assignment, Date date, ActivityType type, Repository repo) {
		var map = Maps.create();
		map.put("type", type);
		map.put("timestamp", date.getTime());
		map.put("id", task.id);
		map.put("repositoryPath", task.repositoryPath);
		map.put("label", repo.getLabel());
		map.put("message", task.name);
		map.put("taskType", task.getType());
		if (assignment.assignedTo.equals(assignment.endedBy)
				&& (type == ActivityType.TASK_ASSIGNMENT_CANCELED || type == ActivityType.TASK_ASSIGNMENT_COMPLETED)) {
			map.put("user", assignment.assignedTo.username);
			map.put("userDisplayName", assignment.assignedTo.name);
		} else {
			map.put("user", task.initiator.username);
			map.put("userDisplayName", task.initiator.name);
			map.put("assignedTo", assignment.assignedTo.name);
		}
		return map;
	}

	public static enum ActivityType {

		COMMIT,
		COMMENT,
		TASK_STARTED,
		TASK_COMPLETED,
		TASK_CANCELED,
		TASK_ASSIGNED,
		TASK_ASSIGNMENT_COMPLETED,
		TASK_ASSIGNMENT_CANCELED;

	}

}
