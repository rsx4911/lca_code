package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.util.Maps;

public class Teams {

	private Teams() {
		// only static access
	}

	public static Map<String, Object> mapForManager(Team team) {
		var map = Maps.of(team);
		Maps.removeAllBut(map, "id", "teamname", "name");
		map.put("users", team.users.stream().map(Users::mapForOthers).toList());
		return map;
	}

	public static Map<String, Object> mapForOthers(Team team) {
		var map = Maps.of(team);
		Maps.removeAllBut(map, "teamname", "name");
		map.put("users", team.users.stream().map(Users::mapForOthers).toList());
		return map;
	}

}
