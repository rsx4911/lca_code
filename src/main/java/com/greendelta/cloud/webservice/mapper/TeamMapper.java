package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.Team;

public class TeamMapper {

	private final UserMapper userMapper = new UserMapper();

	public List<Map<String, Object>> mapForOthers(List<Team> teams) {
		List<Map<String, Object>> maps = new ArrayList<>();
		for (Team team : teams)
			maps.add(mapForOthers(team));
		return maps;
	}

	public Map<String, Object> mapForAdmin(Team team) {
		ObjectMap map = ObjectMap.fromObject(team);
		map.removeAllBut("id", "teamname", "name");
		map.put("users", userMapper.mapForOthers(team.users));
		return map;
	}

	public Map<String, Object> mapForOthers(Team team) {
		ObjectMap map = ObjectMap.fromObject(team);
		map.removeAllBut("teamname", "name");
		map.put("users", userMapper.mapForOthers(team.users));
		return map;
	}

}
