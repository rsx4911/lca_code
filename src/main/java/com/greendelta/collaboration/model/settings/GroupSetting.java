package com.greendelta.collaboration.model.settings;

public enum GroupSetting implements SettingKey {

	AVATAR(byte[].class),
	LABEL(String.class),
	DESCRIPTION(String.class);

	private final Class<?> type;

	private <T> GroupSetting(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

}