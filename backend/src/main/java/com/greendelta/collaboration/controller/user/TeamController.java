package com.greendelta.collaboration.controller.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Teams;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/team")
public class TeamController {

	private final TeamService service;
	private final UserService userService;
	private final MembershipService membershipService;
	private final MessagingService messagingService;

	public TeamController(TeamService service, UserService userService, MembershipService membershipService,
			MessagingService messagingService) {
		this.service = service;
		this.userService = userService;
		this.membershipService = membershipService;
		this.messagingService = messagingService;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) Module module) {
		var result = getVisible(module);
		return Response.ok(result.stream().map(Teams::mapForOthers).toList());
	}

	private List<Team> getVisible(Module module) {
		var currentUser = userService.getCurrentUser();
		if (currentUser.isAnonymous())
			return new ArrayList<>();
		if ((currentUser.isLibraryManager() && module == Module.TEAM_LIBRARIES) || currentUser.isUserManager()
				|| currentUser.isDataManager())
			return service.getAll(0, 0, null, module == Module.TEAM_LIBRARIES).data.stream()
					.filter(team -> !team.users.isEmpty())
					.toList();
		var teams = service.getTeamsFor(currentUser);
		var memberships = getMemberships(currentUser, teams);
		var fromConversations = messagingService.getConversations(currentUser).stream()
				.filter(c -> c.lastMessage.team != null)
				.map(c -> c.lastMessage.team)
				.toList();
		return join(Arrays.asList(teams, getMembers(memberships), fromConversations)).stream()
				.filter(team -> !team.users.isEmpty())
				.distinct().toList();
	}

	private List<Membership> getMemberships(User user, List<Team> teams) {
		var memberships = new ArrayList<Membership>();
		memberships.addAll(membershipService.getMemberships(user));
		for (var team : teams) {
			memberships.addAll(membershipService.getMemberships(team));
		}
		return memberships;
	}

	private List<Team> getMembers(List<Membership> memberships) {
		var members = new ArrayList<Team>();
		for (var membership : memberships) {
			for (var m : membershipService.getMemberships(membership.memberOf)) {
				if (m.team != null) {
					members.add(m.team);
				}
			}
		}
		return members;
	}

	private <T> List<T> join(List<List<T>> lists) {
		var list = new ArrayList<T>();
		lists.forEach(list::addAll);
		return list;
	}

	@GetMapping("avatar/{teamname}")
	public byte[] getAvatar(@PathVariable String teamname) {
		var team = service.getForTeamname(teamname);
		if (team == null)
			return Avatar.get("avatar-team.png");
		return Avatar.get(team.avatar, "avatar-team.png");
	}

	@PutMapping("avatar/{teamname}")
	public byte[] setAvatar(
			@PathVariable String teamname,
			@RequestParam(required = false) MultipartFile file) {
		var team = authorizedGetTeam(teamname);
		if (team == null)
			throw Response.notFound();
		try {
			team.avatar = file != null ? BlobProxy.generateProxy(file.getBytes()) : null;
			team = service.update(team);
		} catch (IOException e) {
			throw Response.error("Error reading avatar file");
		}
		return getAvatar(teamname);
	}

	private Team authorizedGetTeam(String teamname) {
		var user = userService.getCurrentUser();
		if (!user.isUserManager())
			throw Response.unauthorized("Not authorized to manage teams");
		return service.getForTeamname(teamname);
	}

}
