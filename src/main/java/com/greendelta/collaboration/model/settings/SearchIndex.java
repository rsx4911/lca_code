package com.greendelta.collaboration.model.settings;

public enum SearchIndex implements SettingKey {

	PRIVATE("collaboration-server"),
	PUBLIC("collaboration-server-public"),
	IO_DATA("collaboration-server-io-data");

	private final Object defaultValue;

	private <T> SearchIndex(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getDefaultValue() {
		return (T) defaultValue;
	}

	@Override
	public Class<?> getType() {
		return String.class;
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