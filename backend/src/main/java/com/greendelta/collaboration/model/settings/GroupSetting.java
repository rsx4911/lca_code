package com.greendelta.collaboration.model.settings;

public enum GroupSetting implements SettingKey {

	AVATAR(byte[].class),
	LABEL(String.class),
	DESCRIPTION(String.class),
	NO_OF_REPOSITORIES(Integer.class),
	MAX_SIZE(Long.class);

	private final Class<?> type;
	
	private <T> GroupSetting(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
		
	@Override
	public boolean isPublicSetting() {
		return this == LABEL;
	}

	@Override
	public boolean isAdminSetting() {
		return this == NO_OF_REPOSITORIES
				|| this == MAX_SIZE;
	}

}