package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.util.Collections;

public class Memberships {

	private Memberships() {
		// only static access
	}

	public static List<ObjectMap> map(List<Membership> memberships) {
		// each user of a team has a membership, but the teams also hold each
		// user so only one team membership needs to remain for display purposes
		Set<String> repoPlusTeam = new HashSet<>();
		memberships = Collections.filter(memberships, (m) -> {
			String key = m.memberOf;
			if (m.team != null) {
				key += m.team.teamname;
			} else {
				key += m.user.username;
			}
			if (repoPlusTeam.contains(key))
				return true;
			repoPlusTeam.add(key);
			return false;
		});
		List<ObjectMap> maps = new ArrayList<>();
		for (Membership membership : memberships)
			maps.add(map(membership));
		return maps;
	}

	private static ObjectMap map(Membership membership) {
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

}
