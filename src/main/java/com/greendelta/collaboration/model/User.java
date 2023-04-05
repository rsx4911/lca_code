package com.greendelta.collaboration.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table
public class User extends AbstractEntity implements UserDetails {

	private static final long serialVersionUID = -4989312202559805583L;

	@Column
	public String username;

	@Column
	public String name;

	@Column
	public String email;

	@Column
	public String password;

	@Column
	@Lob
	public byte[] avatar;

	@Column
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
		var now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		var activeUntil = Calendar.getInstance();
		activeUntil.setTime(settings.activeUntil);
		return now.after(activeUntil);
	}

	@Override
	public List<GrantedAuthority> getAuthorities() {
		if (isAdmin())
			return Arrays.asList(Authority.ADMIN, Authority.DATA_MANAGER, Authority.USER_MANAGER);
		if (isDataManager())
			return Collections.singletonList(Authority.DATA_MANAGER);
		if (isUserManager())
			return Collections.singletonList(Authority.USER_MANAGER);
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean isAnonymous() {
		return id == 0l;
	}

}
