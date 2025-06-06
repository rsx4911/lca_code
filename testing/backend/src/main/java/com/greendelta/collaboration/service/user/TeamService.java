package com.greendelta.collaboration.service.user;

import java.util.HashMap;
import java.util.List;

import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class TeamService {

	private final Dao<Team> dao;
	private final MembershipService membershipService;
	private final UserService userService;

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
		var added = team.users.add(user);
		if (!added)
			return false;
		team = dao.update(team);
		membershipService.addMemberships(user, team);
		return true;
	}

	public boolean removeMember(User user, Team team) {
		var removed = team.users.remove(user);
		if (!removed)
			return false;
		team = dao.update(team);
		membershipService.removeMemberships(user, team);
		return false;
	}

	public List<Team> getTeamsFor(User user) {
		var jpql = "SELECT team FROM Team team JOIN team.users user WHERE user = :user";
		return dao.getAll(jpql, java.util.Collections.singletonMap("user", user));
	}

	public long getCount() {
		return dao.getCount();
	}

	public SearchResult<Team> getAll(int page, int pageSize, String filter, boolean isTeamLibraries) {
		var user = userService.getCurrentUser();
		var parameters = new HashMap<String, Object>();
		if (isTeamLibraries || !user.isUserManager()) {
			parameters.put("user", user);
		}
		if (!Strings.nullOrEmpty(filter)) {
			parameters.put("name", "%" + filter.toLowerCase() + "%");
		}
		var query = createQuery(user, filter, isTeamLibraries);
		var data = dao.getAll(query, parameters).stream()
				.distinct()
				.sorted((t1, t2) -> t1.name.toLowerCase().compareTo(t2.name.toLowerCase()))
				.toList();
		return SearchResults.paged(page, pageSize, data);
	}

	private String createQuery(User user, String filter, boolean isTeamLibraries) {
		var jpql = new StringBuilder();
		if (isTeamLibraries || !user.isUserManager()) {
			jpql.append("SELECT t FROM Team t WHERE :user MEMBER OF t.users");
			if (!Strings.nullOrEmpty(filter)) {
				jpql.append(" AND LOWER(t.name) LIKE :name");
			}
		} else {
			jpql.append("SELECT t FROM Team t");
			if (!Strings.nullOrEmpty(filter)) {
				jpql.append(" WHERE LOWER(t.name) LIKE :name");
			}
		}
		return jpql.toString();
	}

}
