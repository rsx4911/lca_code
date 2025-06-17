package com.greendelta.collaboration.model.settings;

import java.util.Map;

public enum MailSetting implements SettingKey {

	USER(String.class),
	PASS(String.class),
	PROTO(String.class, "smtps"),
	HOST(String.class),
	PORT(Integer.class, 465),
	SSL(Boolean.class, true),
	TLS(Boolean.class, false),
	DEFAULT_FROM(String.class),
	DEFAULT_REPLY_TO(String.class);

	private final Class<?> type;
	private final Object defaultValue;

	private <T> MailSetting(Class<T> type) {
		this(type, null);
	}

	private <T> MailSetting(Class<T> type, T defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getDefaultValue(Map<String, String> values) {
		return Setting.getDefaultValue(this.name(), type, null, values, (T) defaultValue);
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