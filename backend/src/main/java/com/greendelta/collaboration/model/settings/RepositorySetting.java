package com.greendelta.collaboration.model.settings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greendelta.collaboration.util.JacksonTypes;

public enum RepositorySetting implements SettingKey {

	AVATAR(byte[].class),
	PROHIBIT_COMMITS(Boolean.class, false),
	COMMENT_APPROVAL(Boolean.class, false),
	MAX_SIZE(Long.class, 0l),
	TAGS(JacksonTypes.STRING_LIST),
	LABEL(String.class),
	VERSION(String.class),
	DESCRIPTION(String.class),
	SOURCE_INFO(String.class),
	CONTACT_INFO(String.class),
	CHANGE_LOG(String.class),
	PROJECT_INFO(String.class),
	PROJECT_FUNDING(String.class),
	APPROPRIATE_USE(String.class),
	DQ_ASSESSMENT(String.class),
	CITATION(String.class),
	TYPE_OF_DATA(String.class),
	MAIN_MODEL_TYPE(String.class);

	private final Class<?> type;
	private final TypeReference<?> subType;
	private final Object defaultValue;

	private <T> RepositorySetting(Class<T> type) {
		this(type, null);
	}

	private <T> RepositorySetting(TypeReference<?> subType) {
		this.type = Object.class;
		this.subType = subType;
		this.defaultValue = null;
	}

	private <T> RepositorySetting(Class<T> type, T defaultValue) {
		this.type = type;
		this.subType = null;
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

	@SuppressWarnings("unchecked")
	@Override
	public <V> TypeReference<V> getSubType() {
		return (TypeReference<V>) subType;
	}

	@Override
	public boolean isPublicSetting() {
		return false;
	}

	@Override
	public boolean isAdminSetting() {
		return this == MAX_SIZE;
	}

}