package com.greendelta.collaboration.model.settings;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.greendelta.collaboration.util.GsonTypes;
import com.greendelta.collaboration.util.ModelTypes;

public enum ServerSetting implements SettingKey {

	// features
	MESSAGING_ENABLED(Boolean.class, true),
	TASKS_ENABLED(Boolean.class, true),
	COMMENTS_ENABLED(Boolean.class, true),
	PUBLIC_REPOSITORY_ENABLED(Boolean.class, true),
	NOTIFICATIONS_ENABLED(Boolean.class, true),
	USER_REGISTRATION_ENABLED(Boolean.class, true),
	USER_REGISTRATION_APPROVAL_ENABLED(Boolean.class, true),
	DASHBOARD_ACTIVITIES_ENABLED(Boolean.class, true),
	REPOSITORY_ACTIVITIES_ENABLED(Boolean.class, true),
	HOMEPAGE_ENABLED(Boolean.class, true),
	REPOSITORY_TAGS_ENABLED(Boolean.class, true),
	DATASET_TAGS_ENABLED(Boolean.class, true),
	DATASET_TAGS_ON_DASHBOARD_ENABLED(Boolean.class, true),
	DATASET_TAGS_ON_GROUPS_ENABLED(Boolean.class, true),
	DATASET_TAGS_ON_REPOSITORIES_ENABLED(Boolean.class, true),

	// basic settings
	SERVER_NAME(String.class, "LCA Collaboration Server"),
	SERVER_URL(String.class),
	REPOSITORY_PATH(String.class),
	LIBRARY_PATH(String.class),
	GLAD_URL(String.class),
	GLAD_API_KEY_HEADER(String.class, "api-key"),
	GLAD_API_KEY(String.class),

	// home settings
	HOME_TITLE(String.class, ""),
	HOME_TEXT(String.class, ""),

	// landing page/search settings
	REPOSITORIES_ORDER(GsonTypes.STRING_LIST, new ArrayList<>()),
	REPOSITORIES_HIDDEN(GsonTypes.STRING_LIST, new ArrayList<>()),
	MODEL_TYPES_ORDER(GsonTypes.STRING_LIST, ModelTypes.DEFAULT_MODEL_TYPES_ORDER),
	MODEL_TYPES_HIDDEN(GsonTypes.STRING_LIST, new ArrayList<>()),

	// maintenance
	MAINTENANCE_MODE(Boolean.class, false),
	MAINTENANCE_MESSAGE(String.class, "Server is in maintenance mode. Please try again later"),

	// announcements
	ANNOUNCEMENT_ID(String.class),
	ANNOUNCEMENT_MESSAGE(String.class),

	// license agreement
	LICENSE_AGREEMENT_TEXT(String.class);

	private final Class<?> type;
	private final Type subType;
	private final Object defaultValue;

	private <T> ServerSetting(Class<T> type) {
		this(type, null);
	}

	private <T> ServerSetting(Type subType, T defaultValue) {
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

	@Override
	public Type getSubType() {
		return subType;
	}

	public boolean isPublic() {
		return this != GLAD_API_KEY && this != REPOSITORY_PATH && this != LIBRARY_PATH;
	}

}