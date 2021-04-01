package com.greendelta.collaboration.model.settings;

import java.lang.reflect.Type;

public interface SettingKey {

	String name();

	Class<?> getType();

	default Type getSubType() {
		return null;
	}

	default <T> T getDefaultValue() {
		return null;
	}

}