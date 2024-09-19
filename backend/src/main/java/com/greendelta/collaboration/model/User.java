package com.greendelta.collaboration.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greendelta.collaboration.util.Dates;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user")
public class User extends AbstractEntity implements UserDetails, OidcUser {

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
	@JsonIgnore
	public Blob avatar;

	@Column
	public String twoFactorSecret;

	@Embedded
	public UserSettings settings = new UserSettings();

	@Transient
	public OidcIdToken idToken;

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

	public boolean isLibraryManager() {
		if (settings == null)
			return false;
		return settings.admin || settings.libraryManager;
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

	public void deactivate() {
		var cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Dates.removeTimeInformation(cal);
		settings.activeUntil = cal.getTime();
	}

	@Override
	public List<GrantedAuthority> getAuthorities() {
		var authorities = new ArrayList<GrantedAuthority>();
		if (isAdmin()) {
			authorities.add(Authority.ADMIN);
		}
		if (isUserManager()) {
			authorities.add(Authority.USER_MANAGER);
		}
		if (isDataManager()) {
			authorities.add(Authority.DATA_MANAGER);
		}
		if (isLibraryManager()) {
			authorities.add(Authority.LIBRARY_MANAGER);
		}
		return authorities;
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

	@Override
	public Map<String, Object> getAttributes() {
		return getClaims();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public Map<String, Object> getClaims() {
		if (idToken == null)
			return new HashMap<>();
		return idToken.getClaims();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		if (idToken == null)
			return null;
		return new OidcUserInfo(getClaims());
	}

	@Override
	public OidcIdToken getIdToken() {
		return idToken;
	}

}
