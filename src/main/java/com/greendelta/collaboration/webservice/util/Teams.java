package com.greendelta.collaboration.webservice.util;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.Team;

public class Teams {

	private Teams() {
		// only static access
	}

	public static ObjectMap mapForAdmin(Team team) {
		ObjectMap map = ObjectMap.fromObject(team);
		map.removeAllBut("id", "teamname", "name");
		map.put("users", Client.map(team.users, Users::mapForOthers));
		return map;
	}

	public static ObjectMap mapForOthers(Team team) {
		ObjectMap map = ObjectMap.fromObject(team);
		map.removeAllBut("teamname", "name");
		map.put("users", Client.map(team.users, Users::mapForOthers));
		return map;
	}

}
