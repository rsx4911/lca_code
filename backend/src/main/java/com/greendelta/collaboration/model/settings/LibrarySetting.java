package com.greendelta.collaboration.model.settings;

import com.greendelta.collaboration.model.LibraryAccess;

public enum LibrarySetting implements SettingKey {

	ACCESS(LibraryAccess.class),
	
	OWNER(String.class);

	private final Class<?> type;

	private <T> LibrarySetting(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean isPublicSetting() {
		return false;
	}

	@Override
	public boolean isAdminSetting() {
		return true;
	}

}
