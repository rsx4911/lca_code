package com.greendelta.collaboration.model.settings;

import java.lang.reflect.Type;

import com.greendelta.collaboration.util.GsonTypes;

public enum RepositorySetting implements SettingKey {

	AVATAR(byte[].class),
	PUBLIC_ACCESS(Boolean.class, false),
	PROHIBIT_COMMITS(Boolean.class, false),
	COMMENT_APPROVAL(Boolean.class, false),
	JSON_FILE_GENERATION(Boolean.class, false),
	MAX_SIZE(Long.class, 0l),
	LABEL(String.class),
	VERSION(String.class),
	TAGS(GsonTypes.STRING_LIST),
	DESCRIPTION(String.class),
	SOURCE_INFO(String.class),
	CONTACT_INFO(String.class),
	PROJECT_INFO(String.class),
	PROJECT_FUNDING(String.class),
	APPROPRIATE_USE(String.class),
	DQ_ASSESSMENT(String.class),
	CITATION(String.class),
	TYPE_OF_DATA(String.class),
	LIBRARY_RESTRICTIONS(GsonTypes.ROLE_MAP);

	private final Class<?> type;
	private final Type subType;
	private final Object defaultValue;

	private <T> RepositorySetting(Class<T> type) {
		this(type, null);
	}

	private <T> RepositorySetting(Type subType) {
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

	@Override
	public Type getSubType() {
		return subType;
	}

}