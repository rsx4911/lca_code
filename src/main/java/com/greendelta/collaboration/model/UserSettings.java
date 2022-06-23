package com.greendelta.collaboration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

	// If not 0, the maximum size of all user group repositories in bytes
	@Column
	public long maxSize;

	// If true all users can initiate conversations, otherwise admins only
	@Column
	public boolean messagingEnabled;

	// If true only team members (and admins) can initiate conversations
	@Column
	public boolean messagingRestricted;

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
	@Temporal(TemporalType.DATE)
	public Date activeUntil;
	
	@OneToMany
	@JoinTable
	public List<User> blockedUsers = new ArrayList<>();

	@Column
	long notifications;

	@JsonAnySetter
	public void handleUnknown(String name, Object value) {
		// do nothing
	}

}
