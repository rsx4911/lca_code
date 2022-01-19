package com.greendelta.collaboration.controller.util;

import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.util.ObjectMap;

public class Teams {

	private Teams() {
		// only static access
	}

	public static ObjectMap mapForManager(Team team) {
		var map = ObjectMap.fromObject(team);
		map.removeAllBut("id", "teamname", "name");
		map.put("users", team.users.stream().map(Users::mapForOthers).toList());
		return map;
	}

	public static ObjectMap mapForOthers(Team team) {
		var map = ObjectMap.fromObject(team);
		map.removeAllBut("teamname", "name");
		map.put("users", team.users.stream().map(Users::mapForOthers).toList());
		return map;
	}

}
