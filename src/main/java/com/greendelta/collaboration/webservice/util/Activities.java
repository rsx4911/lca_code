package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openlca.cloud.model.data.Commit;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.ObjectMap;

public class Activities {

	private Activities() {
		// only static access
	}

	public static ObjectMap map(Commit commit, Repository repo) {
		ObjectMap map = new ObjectMap();
		map.put("type", ActivityType.COMMIT);
		map.put("timestamp", commit.timestamp);
		map.put("id", commit.id);
		map.put("repositoryPath", repo.toId());
		map.put("label", repo.getLabel());
		map.put("message", commit.message);
		map.put("user", commit.user);
		return map;
	}

	public static ObjectMap map(Comment comment, Repository repo) {
		ObjectMap map = new ObjectMap();
		map.put("type", ActivityType.COMMENT);
		map.put("timestamp", comment.date.getTime());
		map.put("id", comment.getId());
		map.put("repositoryPath", comment.repositoryPath);
		map.put("label", repo.getLabel());
		map.put("message", comment.text);
		map.put("user", comment.user.username);
		map.put("userDisplayName", comment.user.name);
		map.put("field", ObjectMap.fromObject(comment.field));
		map.put("reply", comment.replyTo != null);
		return map;
	}

	public static List<ObjectMap> map(Task task, Repository repo) {
		List<ObjectMap> activities = new ArrayList<>();
		activities.add(map(task, task.startDate, ActivityType.TASK_STARTED, repo));
		// previously tasks were missing to set the end date, so check for
		// status
		if (task.state == TaskState.COMPLETED || task.state == TaskState.CANCELED) {
			ActivityType type = task.state == TaskState.CANCELED ? ActivityType.TASK_CANCELED
					: ActivityType.TASK_COMPLETED;
			activities.add(map(task, task.endDate, type, repo));
		}
		for (TaskAssignment assignment : task.assignments) {
			activities.addAll(map(task, assignment, repo));
		}
		return activities;
	}

	private static ObjectMap map(Task task, Date date, ActivityType type, Repository repo) {
		ObjectMap map = new ObjectMap();
		map.put("type", type);
		// previously tasks were missing to set the end date, take the latest
		// date and add a second
		map.put("timestamp", date != null ? date.getTime() : getLatestDate(task));
		map.put("id", task.getId());
		map.put("repositoryPath", task.repositoryPath);
		map.put("label", repo.getLabel());
		map.put("message", task.name);
		map.put("taskType", task.getType());
		map.put("user", task.initiator.username);
		map.put("userDisplayName", task.initiator.name);
		return map;
	}

	private static long getLatestDate(Task task) {
		List<Date> dates = new ArrayList<>();
		dates.add(task.startDate);
		dates.add(task.endDate);
		for (TaskAssignment assignment : task.assignments) {
			dates.add(assignment.startDate);
			dates.add(assignment.endDate);
		}
		Date date = Dates.getLatest(dates.toArray(new Date[dates.size()]));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, 1);
		date = cal.getTime();
		return date.getTime();
	}

	private static List<ObjectMap> map(Task task, TaskAssignment assignment, Repository repo) {
		List<ObjectMap> activities = new ArrayList<>();
		activities.add(map(task, assignment, assignment.startDate, ActivityType.TASK_ASSIGNED, repo));
		if (assignment.endDate != null) {
			ActivityType type = assignment.canceled ? ActivityType.TASK_ASSIGNMENT_CANCELED
					: ActivityType.TASK_ASSIGNMENT_COMPLETED;
			activities.add(map(task, assignment, assignment.endDate, type, repo));
		}
		return activities;
	}

	private static ObjectMap map(Task task, TaskAssignment assignment, Date date, ActivityType type, Repository repo) {
		ObjectMap map = new ObjectMap();
		map.put("type", type);
		map.put("timestamp", date.getTime());
		map.put("id", task.getId());
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
