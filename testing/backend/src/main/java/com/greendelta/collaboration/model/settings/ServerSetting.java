package com.greendelta.collaboration.model.settings;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.util.JacksonTypes;
import com.greendelta.collaboration.util.ModelTypes;

public enum ServerSetting implements SettingKey {

	// features
	MESSAGING_ENABLED(Boolean.class, false),
	TASKS_ENABLED(Boolean.class, true),
	COMMENTS_ENABLED(Boolean.class, true),
	RELEASES_ENABLED(Boolean.class, false),
	NOTIFICATIONS_ENABLED(Boolean.class, false),
	USER_REGISTRATION_ENABLED(Boolean.class, false),
	USER_REGISTRATION_APPROVAL_ENABLED(Boolean.class, false),
	CHANGE_LOG_ENABLED(Boolean.class, false),
	DASHBOARD_ACTIVITIES_ENABLED(Boolean.class, true),
	REPOSITORY_ACTIVITIES_ENABLED(Boolean.class, true),
	HOMEPAGE_ENABLED(Boolean.class, false),
	SEARCH_ENABLED(Boolean.class, false),
	USAGE_SEARCH_ENABLED(Boolean.class, false),
	REPOSITORY_TAGS_ENABLED(Boolean.class, true),
	DATASET_TAGS_ENABLED(Boolean.class, true),
	DATASET_TAGS_ON_DASHBOARD_ENABLED(Boolean.class, false),
	DATASET_TAGS_ON_GROUPS_ENABLED(Boolean.class, false),
	DATASET_TAGS_ON_REPOSITORIES_ENABLED(Boolean.class, false),

	// basic settings
	SERVER_NAME(String.class, "LCA Collaboration Server"),
	SERVER_URL(String.class),
	REPOSITORY_PATH(String.class),
	LIBRARY_PATH(String.class),

	// GLAD settings
	GLAD_URL(String.class),
	GLAD_API_KEY(String.class),
	GLAD_DATAPROVIDER(String.class),

	// home settings
	HOME_TITLE(String.class, ""),
	HOME_TEXT(String.class, ""),

	// landing page/search settings
	REPOSITORIES_ORDER(JacksonTypes.STRING_LIST, new ArrayList<>()),
	REPOSITORIES_HIDDEN(JacksonTypes.STRING_LIST, new ArrayList<>()),
	MODEL_TYPES_ORDER(JacksonTypes.STRING_LIST, ModelTypes.DEFAULT_ORDER),
	MODEL_TYPES_HIDDEN(JacksonTypes.STRING_LIST, new ArrayList<>()),
	TYPES_OF_DATA(JacksonTypes.STRING_LIST, new ArrayList<>(Arrays.asList(
			"Unit processes", "System processes", "Impact methods", "I/O", "Hybrid"))),

	// maintenance
	MAINTENANCE_MODE(Boolean.class, false),
	MAINTENANCE_MESSAGE(String.class, "Server is in maintenance mode. Please try again later"),

	// announcements
	ANNOUNCEMENT_ID(String.class),
	ANNOUNCEMENT_MESSAGE(String.class),

	// license agreement
	LICENSE_AGREEMENT_TEXT(String.class),

	// library access
	LIBRARY_ACCESS(LibraryAccess.class);

	private final Class<?> type;
	private final TypeReference<?> subType;
	private final Object defaultValue;

	private ServerSetting(Class<?> type) {
		this(type, null);
	}

	private ServerSetting(TypeReference<?> subType, Object defaultValue) {
		this.type = Object.class;
		this.subType = subType;
		this.defaultValue = defaultValue;
	}

	private <T> ServerSetting(Class<T> type, T defaultValue) {
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
		return !this.name().startsWith("GLAD_")
				&& this != LIBRARY_PATH
				&& this != REPOSITORY_PATH;
	}

	@Override
	public boolean isAdminSetting() {
		return !isPublicSetting();
	}

}