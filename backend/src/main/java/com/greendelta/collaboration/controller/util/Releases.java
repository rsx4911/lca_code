package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.model.ReleaseInfo;
import com.greendelta.collaboration.util.Maps;

public class Releases {

	public static Map<String, Object> map(ReleaseInfo release) {
		var map = Maps.of(release);
		map.put("id", map.get("commitId"));
		map.remove("commitId");
		return map;
	}
	
}
