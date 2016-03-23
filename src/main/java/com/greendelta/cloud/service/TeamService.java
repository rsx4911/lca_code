package com.greendelta.cloud.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresRoles;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;

public class TeamService {

	private final Dao<Team> dao;
	private final MembershipService membershipService;

	@Inject
	public TeamService(Dao<Team> dao, MembershipService membershipService) {
		this.dao = dao;
		this.membershipService = membershipService;
	}

	public Team getForTeamname(String teamname) {
		return dao.getFirstForAttribute("teamname", teamname);
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

	public boolean delete(long id) {
		Team team = dao.get(id);
		if (team == null)
			return false;
		membershipService.removeMemberships(team);
		dao.delete(team);
		return true;
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

	@RequiresRoles("admin")
	public long getCount() {
		return dao.getCount();
	}

	public PagedResult<Team> getAll(int page, String filter) {
		Map<String, Object> parameters = new HashMap<>();
		if (!Strings.isNullOrEmpty(filter))
			parameters.put("name", "%" + filter.toLowerCase() + "%");
		long total = dao.getCount();
		String query = createQuery(page, filter, true);
		long subTotal = dao.getCount(query, parameters);
		int start = 1 + (page - 1) * 10;
		query = createQuery(page, filter, false);
		List<Team> data = dao.getAll(query, parameters, start, 10);
		return new PagedResult<>(page, filter, total, subTotal, data);
	}

	private String createQuery(int page, String filter, boolean forCount) {
		StringBuilder jpql = new StringBuilder();
		if (forCount)
			jpql.append("SELECT count(t) FROM Team t");
		else
			jpql.append("SELECT t FROM Team t");
		if (!Strings.isNullOrEmpty(filter))
			jpql.append(" WHERE LOWER(t.name) LIKE :name");
		return jpql.toString();
	}

}
