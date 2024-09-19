package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class MembershipService {

	private final Dao<Membership> dao;
	private final PermissionsService permissions;

	public MembershipService(Dao<Membership> dao, UserService userService, SettingsService settings) {
		this.dao = dao;
		// cannot inject access service - would result in a dependency loop
		this.permissions = new PermissionsService(userService, this, settings);
	}

	public boolean addMembership(User user, String groupOrRepo, Role role) {
		return addMembership(user, groupOrRepo, role, false);
	}

	public boolean addMembership(User user, String groupOrRepo, Role role, boolean skipAccessCheck) {
		if (!skipAccessCheck)
			checkCanEdit(groupOrRepo);
		if (getDirectMembership(user, groupOrRepo) != null)
			return false;
		var member = new Membership();
		member.user = user;
		member.memberOf = groupOrRepo;
		member.role = role;
		dao.insert(member);
		return true;
	}

	public boolean addMemberships(Team team, String groupOrRepo, Role role) {
		checkCanEdit(groupOrRepo);
		var added = false;
		for (User user : team.users) {
			added = addMembership(user, team, groupOrRepo, role) || added;
		}
		added = addMembership(null, team, groupOrRepo, role) || added;
		return added;
	}

	public boolean addMemberships(User user, Team team) {
		var added = false;
		var all = getMemberships(team);
		checkCanEdit(all);
		var addedFor = new HashSet<String>();
		for (var member : all) {
			var groupOrRepo = member.memberOf;
			// all members of a team have the same role
			if (addedFor.contains(groupOrRepo))
				continue;
			addMembership(user, team, groupOrRepo, member.role);
			addedFor.add(groupOrRepo);
		}
		return added;
	}

	private boolean addMembership(User user, Team team, String groupOrRepo, Role role) {
		var teamMember = getTeamMembership(user, team, groupOrRepo);
		if (teamMember != null)
			return false;
		var member = new Membership();
		member.user = user;
		member.team = team;
		member.memberOf = groupOrRepo;
		member.role = role;
		dao.insert(member);
		return true;
	}

	public boolean removeMembership(User user, String groupOrRepo) {
		checkCanEdit(groupOrRepo);
		var member = getDirectMembership(user, groupOrRepo);
		if (member == null)
			return false;
		dao.delete(member);
		return true;
	}

	public boolean removeMemberships(User user) {
		var members = getMemberships(user);
		if (members.isEmpty())
			return false;
		checkCanEdit(members);
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(Team team) {
		var members = getMemberships(team);
		if (members.isEmpty())
			return false;
		checkCanEdit(members);
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(User user, Team team) {
		var members = getMemberships(user, team);
		if (members.isEmpty())
			return false;
		checkCanEdit(members);
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(String groupOrRepo) {
		checkCanEdit(groupOrRepo);
		var members = getMemberships(groupOrRepo);
		if (members.isEmpty())
			return false;
		dao.delete(members);
		return true;
	}

	public boolean removeMemberships(Team team, String groupOrRepo) {
		checkCanEdit(groupOrRepo);
		var members = getMemberships(team, groupOrRepo);
		if (members.isEmpty())
			return false;
		dao.delete(members);
		return true;
	}

	public boolean setRole(User user, String groupOrRepo, Role role) {
		checkCanEdit(groupOrRepo);
		if (role == Role.NONE)
			return false;
		var member = getDirectMembership(user, groupOrRepo);
		if (member == null)
			return false;
		member.role = role;
		dao.update(member);
		return true;
	}

	public boolean setRole(Team team, String groupOrRepo, Role role) {
		checkCanEdit(groupOrRepo);
		var updated = false;
		for (var user : team.users)
			updated = setRole(user, team, groupOrRepo, role) || updated;
		return updated;
	}

	private boolean setRole(User user, Team team, String groupOrRepo, Role role) {
		if (role == Role.NONE)
			return false;
		var member = getTeamMembership(user, team, groupOrRepo);
		if (member == null)
			return false;
		member.role = role;
		dao.update(member);
		return true;
	}

	public Role getRole(User user, String groupOrRepo) {
		var member = getBestMembership(user, groupOrRepo);
		if (member == null)
			return Role.NONE;
		return member.role;
	}

	public boolean hasMembershipInAnyRepoInGroup(User user, String group) {
		var jpql = "SELECT count(m.id) FROM Membership m WHERE m.user = :user AND m.memberOf LIKE :memberOf";
		Map<String, Object> properties = Map.of("user", user, "memberOf", group + "/%");
		return dao.getCount(jpql, properties) > 0;
	}

	private Membership getBestMembership(User user, String groupOrRepo) {
		var members = getMemberships(user, groupOrRepo);
		if (groupOrRepo.contains("/")) {
			String group = groupOrRepo.substring(0, groupOrRepo.indexOf("/"));
			members.addAll(getMemberships(user, group));
		}
		if (members.isEmpty())
			return null;
		var member = members.get(0);
		for (var m : members) {
			if (m.role == Role.best(m.role, member.role)) {
				member = m;
			}
		}
		// user can be added as part of team or independently. if the user is
		// both independent member and member as part of a team, return
		// the independent membership
		for (var m : members) {
			if (m.role == member.role) {
				if (member.team != null) {
					member = m;
				}
			}
		}
		return member;
	}

	private Membership getDirectMembership(User user, String groupOrRepo) {
		Map<String, Object> attributes = new HashMap<>(Map.of("user", user, "memberOf", groupOrRepo));
		attributes.put("team", null);
		return dao.getFirstForAttributes(attributes);
	}

	private Membership getTeamMembership(User user, Team team, String groupOrRepo) {
		var attributes = new HashMap<String, Object>();
		if (user != null) {
			attributes.put("user", user);
		}
		attributes.put("memberOf", groupOrRepo);
		attributes.put("team", team);
		return dao.getFirstForAttributes(attributes);
	}

	public SearchResult<Membership> getMemberships(String groupOrRepo, String filter) {
		var attributes = new HashMap<String, Object>();
		attributes.put("memberOf", groupOrRepo);
		var members = dao.getForAttributes(attributes);
		if (Strings.nullOrEmpty(filter))
			return SearchResults.from(members);
		filter = filter.toLowerCase();
		for (var m : new ArrayList<>(members)) {
			if (m.team != null && !m.team.name.toLowerCase().contains(filter)) {
				members.remove(m);
			} else if (m.user != null && !m.user.name.toLowerCase().contains(filter)) {
				members.remove(m);
			}
		}
		return SearchResults.from(members);
	}

	public List<Membership> getMemberships(String groupOrRepo) {
		return dao.getForAttributes(Map.of("memberOf", groupOrRepo));
	}

	public List<Membership> getMemberships(User user) {
		return dao.getForAttributes(Map.of("user", user));
	}

	public List<Membership> getMemberships(User user, String groupOrRepo) {
		if (Strings.nullOrEmpty(groupOrRepo))
			return getMemberships(user);
		return dao.getForAttributes(Map.of("user", user, "memberOf", groupOrRepo));
	}

	public List<Membership> getMemberships(User user, Team team) {
		return dao.getForAttributes(Map.of("user", user, "team", team));
	}

	public List<Membership> getMemberships(Team team) {
		return dao.getForAttributes(Map.of("team", team));
	}

	public List<Membership> getMemberships(Team team, String groupOrRepo) {
		if (Strings.nullOrEmpty(groupOrRepo))
			return getMemberships(team);
		return dao.getForAttributes(Map.of("team", team, "memberOf", groupOrRepo));
	}

	private void checkCanEdit(List<Membership> members) {
		for (Membership member : members) {
			checkCanEdit(member.memberOf);
		}
	}

	private void checkCanEdit(String path) {
		if (!permissions.canEditMembersOf(path))
			throw Response.forbidden(path, Permission.EDIT_MEMBERS);
	}

}
