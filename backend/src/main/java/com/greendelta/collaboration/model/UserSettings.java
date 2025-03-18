package com.greendelta.collaboration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonAnySetter;

@Embeddable
public class UserSettings implements Serializable {

	private static final long serialVersionUID = 7222742243498126396L;

	@Column
	public boolean canCreateGroups;

	@Column
	public boolean canCreateRepositories;

	@Column
	public int noOfRepositories;

	@Column
	public long maxSize;

	@Column
	public boolean messagingEnabled;

	@Column
	public boolean showOnlineStatus;

	@Column
	public boolean showReadReceipt;

	@Column
	public boolean showTaskActivities;

	@Column
	public boolean showCommentActivities;

	@Column
	public boolean showCommitActivities;

	@Column
	public boolean admin;

	@Column
	public boolean userManager;

	@Column
	public boolean dataManager;

	@Column
	public boolean libraryManager;

	@Column
	@Temporal(TemporalType.DATE)
	public Date activeUntil;

	@Column
	long notifications;

	@ManyToMany
	@JoinTable
	public List<User> blockedUsers = new ArrayList<>();

	public void setDefaults() {
		messagingEnabled = true;
		showCommitActivities = true;
		showCommentActivities = true;
		showTaskActivities = true;
		for (var notification : Notification.values()) {
			enable(notification);
		}
	}

	public void enable(Notification notification) {
		if (isEnabled(notification))
			return;
		var e = (long) Math.pow(2, notification.ordinal());
		notifications += e;
	}

	public void disable(Notification notification) {
		if (!isEnabled(notification))
			return;
		var e = (long) Math.pow(2, notification.ordinal());
		notifications -= e;
	}

	public boolean isEnabled(Notification notification) {
		var e = (long) Math.pow(2, notification.ordinal());
		return (notifications | e) == notifications;
	}

	@JsonAnySetter
	public void handleUnknown(String name, Object value) {
		// do nothing
	}

	@PostLoad
	private void postLoad() {
		// avoid recursion issue if two users block each other
		var blocked = new ArrayList<User>();
		for (var u : blockedUsers) {
			var user = new User();
			user.id = u.id;
			user.username = u.username;
			user.name = u.name;
			blocked.add(user);
		}
		blockedUsers = blocked;
	}

}
