package com.greendelta.collaboration.model.settings;

public enum SearchSetting implements SettingKey {

	SCHEMA(String.class, "http"),
	HOST(String.class, "localhost"),
	PORT(Integer.class, 9200);

	private final Class<?> type;
	private final Object defaultValue;

	private <T> SearchSetting(Class<T> type, T defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getDefaultValue() {
		return (T) defaultValue;
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