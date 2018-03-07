package com.greendelta.collaboration.webservice.util;

import com.greendelta.collaboration.model.Setting;
import com.greendelta.collaboration.util.ObjectMap;

public class Settings {

	private Settings() {
		// only static access
	}

	public static ObjectMap map(Setting setting) {
		ObjectMap map = ObjectMap.fromObject(setting);
		map.remove("id");
		return map;
	}
}
