package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;

public class MembershipService {

	private final Dao<Membership> dao;
	private final AccessService accessService;

	@Inject
	public MembershipService(@Named("repository.path") String repositoryPath, Dao<Membership> dao,
			UserService userService) {
		this.dao = dao;
		// cannot inject access service - would result in a dependency loop
		this.accessService = new AccessService(repositoryPath, userService, this);
	}

	public boolean addMembership(User user, String groupOrRepo, Role role) {
		return addMembership(user, groupOrRepo, role, false);
	}

	public boolean addMembership(User user, String groupOrRepo, Role role, boolean skipAccessCheck) {
		if (!skipAccessCheck)
			checkCanEdit(groupOrRepo);
		if (getDirectMembership(user, groupOrRepo) != null)
			return false;
		Membership member = new Membership();
		member.user = user;
		member.memberOf = groupOrRepo;
		member.role = role;
		dao.insert(member);
		return true;
	}

	public boolean addMemberships(Team team, String groupOrRepo, Role role) {
		checkCanEdit(groupOrRepo);
		boolean added = false;
		for (User user : team.users)
			added = addMembership(user, team, groupOrRepo, role) || added;
		added = addMembership(null, team, groupOrRepo, role) || added;
		return added;
	}

	public boolean addMemberships(User user, Team team) {
		boolean added = false;
		List<Membership> all = getMemberships(team);
		checkCanEdit(all);
		Set<String> addedFor = new HashSet<>();
		for (Membership member : all) {
			String groupOrRepo = member.memberOf;
			// all members of a team have the same role
			if (addedFor.contains(groupOrRepo))
				continue;
			addMembership(user, team, groupOrRepo, member.role);
			addedFor.add(groupOrRepo);
		}
		return added;
	}

	private boolean addMembership(User user, Team team, String groupOrRepo, Role role) {
		Membership teamMember = getTeamMembership(user, team, groupOrRepo);
		if (teamMember != null)
			return false;
		Membership member = new Membership();
		member.user = user;
		member.team = team;
		member.memberOf = groupOrRepo;
		member.role = role;
		dao.insert(member);
		return true;
	}

	public boolean removeMembership(User user, String groupOrRepo) {
		checkCanEdit(groupOrRepo);
		Membership member = getDirectMembership(user, groupOrRepo);
		if (member == null)
			return false;
		dao.delete(member);
		return true;
	}

	public boolean removeMemberships(User user) {
		List<Membership> members = getMemberships(user);
		if (members.isEmpty())
			return false;
		checkCanEdit(members);
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(Team team) {
		List<Membership> members = getMemberships(team);
		if (members.isEmpty())
			return false;
		checkCanEdit(members);
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(User user, Team team) {
		List<Membership> members = getMemberships(user, team);
		if (members.isEmpty())
			return false;
		checkCanEdit(members);
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(String groupOrRepo) {
		checkCanEdit(groupOrRepo);
		List<Membership> members = getMemberships(groupOrRepo);
		if (members.isEmpty())
			return false;
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(Team team, String groupOrRepo) {
		checkCanEdit(groupOrRepo);
		List<Membership> members = getMemberships(team, groupOrRepo);
		if (members.isEmpty())
			return false;
		dao.delete(members);
		return true;
	}

	public boolean setRole(User user, String groupOrRepo, Role role) {
		checkCanEdit(groupOrRepo);
		if (role == Role.NONE)
			return false;
		Membership member = getDirectMembership(user, groupOrRepo);
		if (member == null)
			return false;
		member.role = role;
		dao.update(member);
		return true;
	}

	public boolean setRole(Team team, String groupOrRepo, Role role) {
		checkCanEdit(groupOrRepo);
		boolean updated = false;
		for (User user : team.users)
			updated = setRole(user, team, groupOrRepo, role) || updated;
		return updated;
	}

	private boolean setRole(User user, Team team, String groupOrRepo, Role role) {
		if (role == Role.NONE)
			return false;
		Membership member = getTeamMembership(user, team, groupOrRepo);
		if (member == null)
			return false;
		member.role = role;
		dao.update(member);
		return true;
	}

	public Role getRole(User user, String groupOrRepo) {
		Membership member = getBestMembership(user, groupOrRepo);
		if (member == null)
			return Role.NONE;
		return member.role;
	}

	public boolean hasMembershipInAnyRepoInGroup(User user, String group) {
		String jpql = "SELECT count(m.id) FROM Membership m WHERE m.user = :user AND m.memberOf LIKE :memberOf";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("user", user);
		parameters.put("memberOf", group + "/%");
		return dao.getCount(jpql, parameters) > 0;
	}

	private Membership getBestMembership(User user, String groupOrRepo) {
		List<Membership> members = getMemberships(user, groupOrRepo);
		if (groupOrRepo.contains(File.separator)) {
			String group = groupOrRepo.substring(0, groupOrRepo.indexOf(File.separator));
			members.addAll(getMemberships(user, group));
		}
		if (members.isEmpty())
			return null;
		// user can be added as part of team or independently, so "highest" role
		// counts, if is both independent member and member as part of team
		// return independent membership
		Membership member = members.get(0);
		for (Membership m : members) {
			if (m.role == member.role) {
				if (member.team != null) {
					member = m;
				}
			} else if (m.role == Role.best(m.role, member.role)) {
				member = m;
			}
		}
		return member;
	}

	private Membership getDirectMembership(User user, String groupOrRepo) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("memberOf", groupOrRepo);
		attributes.put("team", null);
		return dao.getFirstForAttributes(attributes);
	}

	private Membership getTeamMembership(User user, Team team, String groupOrRepo) {
		Map<String, Object> attributes = new HashMap<>();
		if (user != null)
			attributes.put("user", user);
		attributes.put("memberOf", groupOrRepo);
		attributes.put("team", team);
		return dao.getFirstForAttributes(attributes);
	}

	public PagedResult<Membership> getMemberships(String groupOrRepo, String filter) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("memberOf", groupOrRepo);
		List<Membership> result = dao.getForAttributes(attributes);
		filter = filter.toLowerCase();
		if (!Strings.isNullOrEmpty(filter))
			for (Membership m : new ArrayList<>(result))
				if (m.team != null && !m.team.name.toLowerCase().contains(filter))
					result.remove(m);
				else if (m.user != null && !m.user.name.toLowerCase().contains(filter))
					result.remove(m);
		return new PagedResult<Membership>(0, filter, result.size(), result.size(), result);
	}

	public List<Membership> getMemberships(String groupOrRepo) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("memberOf", groupOrRepo);
		List<Membership> result = dao.getForAttributes(attributes);
		return result;
	}

	private List<Membership> getMemberships(User user) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		return dao.getForAttributes(attributes);
	}

	private List<Membership> getMemberships(User user, String groupOrRepo) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("memberOf", groupOrRepo);
		return dao.getForAttributes(attributes);
	}

	private List<Membership> getMemberships(User user, Team team) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("team", team);
		return dao.getForAttributes(attributes);
	}

	private List<Membership> getMemberships(Team team) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("team", team);
		return dao.getForAttributes(attributes);
	}

	private List<Membership> getMemberships(Team team, String groupOrRepo) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("memberOf", groupOrRepo);
		attributes.put("team", team);
		return dao.getForAttributes(attributes);
	}

	private void checkCanEdit(List<Membership> members) {
		for (Membership member : members) {
			checkCanEdit(member.memberOf);
		}
	}

	private void checkCanEdit(String path) {
		if (!accessService.canEditMembersOf(path))
			throw new UnauthorizedAccessException(path, "CHANGE_ROLE");
	}

}
