package com.greendelta.cloud.webservice.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.cloud.model.Membership;

public class Memberships {
	
	private Memberships() {
		// only static access
	}

	public static List<Map<String, Object>> map(List<Membership> memberships) {
		memberships = filter(memberships);
		List<Map<String, Object>> maps = new ArrayList<>();
		for (Membership membership : memberships)
			maps.add(map(membership));
		return maps;
	}

	private static Map<String, Object> map(Membership membership) {
		ObjectMap map = ObjectMap.fromObject(membership);
		map.remove("id");
		if (membership.team != null) {
			map.put("team", Teams.mapForOthers(membership.team));
			map.remove("user");
		} else {
			map.put("user", Users.mapForOthers(membership.user));
			map.remove("team");
		}
		return map;
	}

	// each user of a team has a membership, but the teams also hold each user
	// so only one team membership needs to remain for frontend display purposes
	private static List<Membership> filter(List<Membership> memberships) {
		Set<String> repoPlusTeam = new HashSet<>();
		List<Membership> filtered = new ArrayList<>();
		for (Membership m : memberships) {
			String key = m.memberOf;
			if (m.team != null)
				key += m.team.teamname;
			else
				key += m.user.username;
			if (!repoPlusTeam.contains(key)) {
				filtered.add(m);
				repoPlusTeam.add(key);
			}
		}
		return filtered;
	}
}
