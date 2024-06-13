package com.greendelta.collaboration.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
	
	ADMIN,

	USER_MANAGER,
	
	DATA_MANAGER,

	LIBRARY_MANAGER;
	
	@Override
	public String getAuthority() {
		return name();
	}

}
