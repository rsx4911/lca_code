package com.greendelta.cloud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "username")
	public String username;

	@Column(name = "name")
	public String name;

	@Column(name = "email")
	public String email;

	@Column(name = "hash", length = 64)
	public String hash;

	@Column(name = "salt", length = 16)
	public String salt;

	@Column(name = "admin")
	public boolean admin;

	@Column(name = "can_create_groups")
	public boolean canCreateGroups;

	@Column(name = "can_create_repositories")
	public boolean canCreateRepositories;

	@Column(name = "avatar")
	public byte[] avatar;

	@Column(name = "notifications")
	private int notifications;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
			return false;
		if (obj == this)
			return true;
		return ((User) obj).username.equals(username);
	}

	public void enable(Notification notification) {
		if (isEnabled(notification))
			return;
		int e = (int) Math.pow(2, notification.ordinal());
		notifications += e;
	}

	public void disable(Notification notification) {
		if (!isEnabled(notification))
			return;
		int e = (int) Math.pow(2, notification.ordinal());
		notifications -= e;
	}

	public boolean isEnabled(Notification notification) {
		int e = (int) Math.pow(2, notification.ordinal());
		return (notifications | e) == notifications;
	}

}
