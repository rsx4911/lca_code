package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting;
import com.greendelta.collaboration.model.Setting.Key;

public class SettingService {

	private final Dao<Setting> dao;

	@Inject
	public SettingService(Dao<Setting> dao) {
		this.dao = dao;
	}

	public void set(Key key, Object value) {
		Setting setting = dao.getFirstForAttribute("name", key);
		if (setting == null) {
			setting = new Setting();
			setting.name = key;
			setting.value = key.toString(value);
			dao.insert(setting);
		} else {
			setting.value = key.toString(value);
			dao.update(setting);
		}
	}

	public boolean is(Key key) {
		return get(key);
	}

	public <T> T get(Key key) {
		Setting setting = dao.getFirstForAttribute("name", key);
		if (setting == null)
			return key.getDefaultValue();
		return key.parse(setting.value);
	}

	public List<Setting> getAll() {
		List<Setting> settings = new ArrayList<>();
		for (Key key : Key.values()) {
			Setting setting = dao.getFirstForAttribute("name", key);
			if (setting == null) {
				setting = new Setting();
				setting.name = key;
				setting.value = key.toString(key.getDefaultValue());
			}
			settings.add(setting);
		}
		return settings;
	}

}
