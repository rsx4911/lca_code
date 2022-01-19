package com.greendelta.collaboration.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
	
	USER_MANAGER,
	
	DATA_MANAGER,
	
	ADMIN;

	@Override
	public String getAuthority() {
		return name();
	}

}
