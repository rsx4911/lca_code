package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.Team;

public class Teams {

	private Teams() {
		// only static access
	}

	public static List<ObjectMap> mapForOthers(List<Team> teams) {
		List<ObjectMap> maps = new ArrayList<>();
		for (Team team : teams)
			maps.add(mapForOthers(team));
		return maps;
	}

	public static ObjectMap mapForAdmin(Team team) {
		ObjectMap map = ObjectMap.fromObject(team);
		map.removeAllBut("id", "teamname", "name");
		map.put("users", Users.mapForOthers(team.users));
		return map;
	}

	public static ObjectMap mapForOthers(Team team) {
		ObjectMap map = ObjectMap.fromObject(team);
		map.removeAllBut("teamname", "name");
		map.put("users", Users.mapForOthers(team.users));
		return map;
	}

}
