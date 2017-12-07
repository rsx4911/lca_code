package com.greendelta.collaboration.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonAnySetter;

@Embeddable
public class UserSettings {

	@Column(name = "can_create_groups")
	public boolean canCreateGroups;

	@Column(name = "can_create_repositories")
	public boolean canCreateRepositories;

	@Column(name = "no_of_repositories")
	public int noOfRepositories;

	// If not 0, the maximum size of all user group repositories in bytes
	@Column(name = "max_size")
	public long maxSize;

	// If true all users can initiate conversations, otherwise admins only
	@Column(name = "messaging_enabled")
	public boolean messagingEnabled;

	// If true only team members (and admins) can initiate conversations
	@Column(name = "messaging_restricted")
	public boolean messagingRestricted;

	@Column(name = "show_online_status")
	public boolean showOnlineStatus;

	@Column(name = "show_read_receipt")
	public boolean showReadReceipt;

	@OneToMany
	@JoinTable(name = "blocked_users", joinColumns = { @JoinColumn(name = "f_user") }, inverseJoinColumns = { @JoinColumn(name = "f_blocked") })
	public List<User> blockedUsers = new ArrayList<>();

	@Column(name = "notifications")
	int notifications;

	@JsonAnySetter
	public void handleUnknown(String name, Object value) {
		// do nothing
	}

}
