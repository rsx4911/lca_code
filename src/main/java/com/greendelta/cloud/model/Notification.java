package com.greendelta.cloud.model;

public enum Notification {

	GROUP_DELETED,
	REPOSITORY_CREATED,
	REPOSITORY_DELETED,
	DATA_COMMITTED,
	ADDED_TO_GROUP_MEMBERS,
	REMOVED_FROM_GROUP_MEMBERS,
	ADDED_TO_REPOSITORY_MEMBERS,
	REMOVED_FROM_REPOSITORY_MEMBERS,
	ADDED_TO_TEAM_MEMBERS,
	REMOVED_FROM_TEAM_MEMBERS,
	USER_CREATED,
	USER_DELETED,
	TEAM_CREATED,
	TEAM_DELETED,
	GROUP_CREATED,
	NOTIFY_FOR_ALL; // also notify about above events when not member

	public boolean isOneOf(Notification... notifications) {
		if (notifications == null)
			return false;
		for (Notification notification : notifications)
			if (notification == this)
				return true;
		return false;
	}

}
