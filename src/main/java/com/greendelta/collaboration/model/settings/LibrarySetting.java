package com.greendelta.collaboration.model.settings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greendelta.collaboration.util.JacksonTypes;

public enum LibrarySetting implements SettingKey {

	ACCESS(JacksonTypes.STRING_LIST);

	private final Class<?> type;
	private final TypeReference<?> subType;
	private final Object defaultValue;

	private <T> LibrarySetting(TypeReference<?> subType) {
		this.type = Object.class;
		this.subType = subType;
		this.defaultValue = null;
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
}
