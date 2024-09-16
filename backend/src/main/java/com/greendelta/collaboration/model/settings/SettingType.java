package com.greendelta.collaboration.model.settings;

public enum SettingType {

	SERVER_SETTING(ServerSetting.class, true),
	IMPRINT_SETTING(ImprintSetting.class, true),
	MAIL_SETTING(MailSetting.class, true),
	SEARCH_SETTING(SearchSetting.class, true),
	SEARCH_INDEX(SearchIndex.class, true),
	REPOSITORY_SETTING(RepositorySetting.class, false),
	GROUP_SETTING(GroupSetting.class, false),
	LIBRARY_SETTING(LibrarySetting.class, false);

	public final Class<? extends Enum<? extends SettingKey>> enumClass;
	public final boolean singleton;

	private <T extends Enum<? extends SettingKey>> SettingType(Class<T> enumClass, boolean singleton) {
		this.enumClass = enumClass;
		this.singleton = singleton;
	}

	@SuppressWarnings("unchecked")
	public <T extends SettingKey> T getSettingKey(String value) {
		if (value == null)
			return null;
		for (T constant : (T[]) enumClass.getEnumConstants())
			if (constant.name().equals(value))
				return constant;
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends SettingKey> SettingType getFor(SettingKey key) {
		for (SettingType type : values())
			for (T constant : (T[]) type.enumClass.getEnumConstants())
				if (constant == key)
					return type;
		return null;
	}

	public static enum SettingScope {

		SERVER,
		DATA;
		
	}

}