package com.greendelta.collaboration.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

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

	@Column(name = "avatar")
	public byte[] avatar;

	@Column(name = "two_factor_secret")
	public String twoFactorSecret;

	@Embedded
	public UserSettings settings = new UserSettings();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
			return false;
		if (obj == this)
			return true;
		return ((User) obj).username.equals(username);
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	public void enable(Notification notification) {
		if (isEnabled(notification))
			return;
		long e = (long) Math.pow(2, notification.ordinal());
		settings.notifications += e;
	}

	public void disable(Notification notification) {
		if (!isEnabled(notification))
			return;
		long e = (long) Math.pow(2, notification.ordinal());
		settings.notifications -= e;
	}

	public boolean isEnabled(Notification notification) {
		long e = (long) Math.pow(2, notification.ordinal());
		return (settings.notifications | e) == settings.notifications;
	}

	public boolean isAdmin() {
		if (settings == null)
			return false;
		return settings.admin;
	}

	public boolean isUserManager() {
		if (settings == null)
			return false;
		return settings.admin || settings.userManager;
	}

	public boolean isDataManager() {
		if (settings == null)
			return false;
		return settings.admin || settings.dataManager;
	}

	public boolean isDeactivated() {
		if (settings == null)
			return false;
		if (settings.activeUntil == null)
			return false;
		if (isAdmin())
			return false;
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		Calendar activeUntil = Calendar.getInstance();
		activeUntil.setTime(settings.activeUntil);
		return now.after(activeUntil);
	}

}
