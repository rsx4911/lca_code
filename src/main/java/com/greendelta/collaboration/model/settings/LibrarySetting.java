package com.greendelta.collaboration.model.settings;

import com.greendelta.collaboration.model.LibraryAccess;

public enum LibrarySetting implements SettingKey {

	ACCESS(LibraryAccess.class);

	private final Class<?> type;

	private <T> LibrarySetting(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

}
