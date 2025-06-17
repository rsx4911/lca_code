package com.greendelta.collaboration.model.settings;

import java.util.Map;

public enum SearchIndex implements SettingKey {

	PRIVATE(SearchIndexType.SEARCH, false, "collaboration-server"),
	PUBLIC(SearchIndexType.SEARCH, true, "collaboration-server-public"),
	PRIVATE_USAGE(SearchIndexType.USAGE, false, "collaboration-server-usage"),
	PUBLIC_USAGE(SearchIndexType.USAGE, true, "collaboration-server-usage-public");

	public final SearchIndexType type;
	public final boolean isPublic;
	private final Object defaultValue;

	private <T> SearchIndex(SearchIndexType type, boolean isPublic, T defaultValue) {
		this.type = type;
		this.isPublic = isPublic;
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getDefaultValue(Map<String, String> values) {
		if (values != null && values.containsKey(this.name()))
			return (T) values.get(this.name());
		return (T) defaultValue;
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

	@Override
	public boolean isPublicSetting() {
		return false;
	}

	@Override
	public boolean isAdminSetting() {
		return true;
	}

	public enum SearchIndexType {

		SEARCH,

		USAGE;

	}

}