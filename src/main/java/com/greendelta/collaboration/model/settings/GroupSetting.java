package com.greendelta.collaboration.model.settings;

public enum GroupSetting implements SettingKey {

	AVATAR(byte[].class),
	LABEL(String.class),
	DESCRIPTION(String.class),
	NO_OF_REPOSITORIES(Integer.class, true),
	MAX_SIZE(Long.class, true);

	public final boolean isAdminSetting;
	private final Class<?> type;
	
	private <T> GroupSetting(Class<T> type) {
		this(type, false);
	}

	private <T> GroupSetting(Class<T> type, boolean isAdminSetting) {
		this.type = type;
		this.isAdminSetting = isAdminSetting;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

}