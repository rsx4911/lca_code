package com.greendelta.collaboration.model.settings;

public enum ImprintSetting implements SettingKey {

	COMPANY(String.class),
	CEO(String.class),
	STREET(String.class),
	ZIP_CODE(String.class),
	CITY(String.class),
	COUNTRY(String.class),
	PHONE(String.class),
	FAX(String.class),
	EMAIL(String.class),
	WEBSITE(String.class),
	REGISTRATION(String.class),
	VAT(String.class);

	private final Class<?> type;

	private <T> ImprintSetting(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean isPublicSetting() {
		return true;
	}
	
	@Override
	public boolean isAdminSetting() {
		return false;
	}

}