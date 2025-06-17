package com.greendelta.collaboration.model.settings;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public interface SettingKey {

	String name();
	
	boolean isPublicSetting();
	
	boolean isAdminSetting();

	Class<?> getType();

	default <V> TypeReference<V> getSubType() {
		return null;
	}

	default <T> T getDefaultValue(Map<String, String> defaultValues) {
		return null;
	}

}