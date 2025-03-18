package com.greendelta.collaboration.controller.usermanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Teams;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.Routes;
import com.greendelta.collaboration.util.SearchResults;

@RestController("usermanagerTeamController")
@RequestMapping("ws/usermanager/team")
public class TeamController {

	private final TeamService service;
	private final UserService userService;
	private final MembershipService membershipService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;

	public TeamController(TeamService service, UserService userService, MembershipService membershipService,
			DeleteService deleteService, NotificationService notificationService) {
		this.service = service;
		this.userService = userService;
		this.membershipService = membershipService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) Module module) {
		var result = service.getAll(page, pageSize, filter, false);
		return Response.ok(SearchResults.convert(result, Teams::mapForOthers));
	}

	@GetMapping("{teamname}")
	public Map<String, Object> get(@PathVariable String teamname) {
		var team = service.getForTeamname(teamname);
		if (team == null)
			throw Response.notFound();
		return Teams.mapForManager(team);
	}

	@PostMapping("{teamname}")
	public ResponseEntity<Map<String, Object>> create(
			@PathVariable String teamname,
			@RequestBody Team team) {
		if (Strings.nullOrEmpty(teamname))
			throw Response.badRequest("teamname", "Missing input: Teamname");
		if (!Routes.isValid(teamname))
			throw Response.badRequest("teamname",
					"Teamname must consist of at least 4 characters and can only contain characters, numbers and _");
		if (Strings.nullOrEmpty(team.name))
			throw Response.badRequest("name", "Missing input: Name");
		if (service.exists(teamname))
			throw Response.badRequest("teamname", "Team already exists");
		if (Routes.isReserved(teamname))
			throw Response.badRequest("teamname", "This is a reserved word");
		team = service.insert(team);
		notificationService.teamCreated(team).send();
		return Response.created(Teams.mapForManager(team));
	}

	@PutMapping("{teamname}")
	public Map<String, Object> update(
			@PathVariable String teamname,
			@RequestBody Team team) {
		var fromDb = authorizedGetTeam(teamname);
		if (fromDb == null)
			throw Response.notFound();
		if (Strings.nullOrEmpty(team.teamname))
			throw Response.badRequest("teamname", "Missing input: Teamname");
		if (Routes.isReserved(team.teamname))
			throw Response.badRequest("teamname", "This is a reserved word");
		if (Strings.nullOrEmpty(team.name))
			throw Response.badRequest("name", "Missing input: Name");
		var notifications = updateUsers(team);
		fromDb.teamname = team.teamname;
		fromDb.name = team.name;
		fromDb.users.clear();
		fromDb.users.addAll(team.users);
		fromDb = service.update(fromDb);
		notifications.forEach(notification -> notification.send());
		return Teams.mapForManager(fromDb);
	}

	@PutMapping("{teamname}/activeuntil")
	public void setActiveUntil(
			@PathVariable String teamname,
			@RequestBody Map<String, Object> data) {
		var team = authorizedGetTeam(teamname);
		if (team == null)
			throw Response.notFound();
		var date = Maps.getString(data, "activeUntil");
		for (var user : team.users) {
			user.settings.activeUntil = Dates.fromString(date);
			userService.update(user);
		}
	}

	@DeleteMapping("{teamname}")
	public void delete(@PathVariable String teamname) {
		var team = service.getForTeamname(teamname);
		if (team == null)
			throw Response.notFound();
		deleteService.delete(team);
		notificationService.teamDeleted(team).send();
	}

	private List<NotificationJob> updateUsers(Team team) {
		var notifications = new ArrayList<NotificationJob>();
		var fromDb = service.getForTeamname(team.teamname);
		var users = new ArrayList<>(team.users);
		team.users.clear();
		for (var user : users) {
			user = userService.getForUsername(user.username);
			team.users.add(user);
			if (fromDb.users.contains(user))
				continue;
			var notification = notificationService.memberAdded(team, user);
			if (membershipService.addMemberships(user, team)) {
				notifications.add(notification);
			}
		}
		for (var user : fromDb.users) {
			if (team.users.contains(user))
				continue;
			user = userService.getForUsername(user.username);
			var notification = notificationService.memberRemoved(team, user);
			if (membershipService.removeMemberships(user, team)) {
				notifications.add(notification);
			}
		}
		return notifications;
	}

	private Team authorizedGetTeam(String teamname) {
		var user = userService.getCurrentUser();
		if (!user.isUserManager())
			throw Response.unauthorized("Not authorized to manage teams");
		return service.getForTeamname(teamname);
	}

}
