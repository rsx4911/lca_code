package com.greendelta.collaboration.controller.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.util.Maps;

public class Memberships {

	private Memberships() {
		// only static access
	}

	public static List<Map<String, Object>> map(List<Membership> memberships) {
		// each user of a team has a membership, but the teams also hold each
		// user so only one team membership needs to remain for display purposes
		var repoPlusTeam = new HashSet<String>();
		return memberships.stream()
				.filter(m -> {
					var key = m.memberOf + (m.team != null
							? m.team.teamname
							: m.user.username);
					if (repoPlusTeam.contains(key))
						return false;
					repoPlusTeam.add(key);
					return true;
				}).map(m -> map(m))
				.toList();
	}

	public static Map<String, Object> map(Membership membership) {
		var map = Maps.of(membership);
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
