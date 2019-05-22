package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

public class TeamService {

	private final Dao<Team> dao;
	private final MembershipService membershipService;
	private final UserService userService;

	@Inject
	public TeamService(Dao<Team> dao, MembershipService membershipService, UserService userService) {
		this.dao = dao;
		this.membershipService = membershipService;
		this.userService = userService;
	}

	public Team getForTeamname(String teamname) {
		return dao.getFirstForAttribute("teamname", teamname, true);
	}

	public Team insert(Team team) {
		return dao.insert(team);
	}

	public Team update(Team team) {
		return dao.update(team);
	}

	public boolean exists(String teamname) {
		return getForTeamname(teamname) != null;
	}

	public void delete(Team team) {
		dao.delete(team);
	}

	public boolean addMember(User user, Team team) {
		boolean added = team.users.add(user);
		if (!added)
			return false;
		team = dao.update(team);
		membershipService.addMemberships(user, team);
		return true;
	}

	public boolean removeMember(User user, Team team) {
		boolean removed = team.users.remove(user);
		if (!removed)
			return false;
		team = dao.update(team);
		membershipService.removeMemberships(user, team);
		return false;
	}

	public List<Team> getTeamsFor(User user) {
		String jpql = "SELECT team FROM Team team JOIN team.users user WHERE user = :user";
		return dao.getAll(jpql, java.util.Collections.singletonMap("user", user));
	}

	public long getCount() {
		return dao.getCount();
	}
	
	public SearchResult<Team> getVisible(int page, int pageSize, String filter) {
		User user = userService.getCurrentUser();
		if (user == null)
			return SearchResults.from(new ArrayList<>());
		Map<String, Object> parameters = new HashMap<>();
		if (!user.isUserManager())
			parameters.put("user", user);
		if (!Strings.isNullOrEmpty(filter))
			parameters.put("name", "%" + filter.toLowerCase() + "%");
		String query = createQuery(user, filter);
		List<Team> data = Collections.filterDuplicates(dao.getAll(query, parameters));
		java.util.Collections.sort(data, (t1, t2) -> t1.name.toLowerCase().compareTo(t2.name.toLowerCase()));
		return SearchResults.paged(page, pageSize, data);
	}

	private String createQuery(User user, String filter) {
		StringBuilder jpql = new StringBuilder();
		if (user.isUserManager()) {
			jpql.append("SELECT t FROM Team t");
			if (!Strings.isNullOrEmpty(filter)) {
				jpql.append(" WHERE LOWER(t.name) LIKE :name");
			}
		} else {
			jpql.append("SELECT t FROM Team t WHERE :user MEMBER OF t.users");
			if (!Strings.isNullOrEmpty(filter))
				jpql.append(" AND LOWER(t.name) LIKE :name");
		}
		return jpql.toString();
	}

}
