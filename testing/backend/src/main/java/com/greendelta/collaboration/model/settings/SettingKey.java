package com.greendelta.collaboration.model.settings;

import com.fasterxml.jackson.core.type.TypeReference;

public interface SettingKey {

	String name();
	
	boolean isPublicSetting();
	
	boolean isAdminSetting();

	Class<?> getType();

	default <V> TypeReference<V> getSubType() {
		return null;
	}

	default <T> T getDefaultValue() {
		return null;
	}

}