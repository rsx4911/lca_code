package com.greendelta.collaboration.webservice.usermanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Beans;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Names;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Teams;

@Path("usermanager/team")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeamResource {

	private final TeamService service;
	private final UserService userService;
	private final MembershipService membershipService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;

	@Inject
	public TeamResource(TeamService service, UserService userService, MembershipService membershipService,
			DeleteService deleteService, NotificationService notificationService) {
		this.service = service;
		this.userService = userService;
		this.membershipService = membershipService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
	}

	@GET
	@Path("{teamname}")
	public Response get(@PathParam("teamname") String teamname) {
		Team team = service.getForTeamname(teamname);
		if (team == null)
			return Respond.notFound();
		return Respond.ok(Teams.mapForManager(team));
	}

	@POST
	@Path("{teamname}")
	public Response create(@PathParam("teamname") String teamname, Team team) {
		if (Strings.isNullOrEmpty(teamname))
			return Respond.invalid("teamname", "Missing input: Teamname");
		if (!Names.isValid(teamname))
			return Respond.invalid("teamname",
					"Teamname must consist of at least 4 characters and can only contain characters, numbers and _");
		if (Strings.isNullOrEmpty(team.name))
			return Respond.invalid("name", "Missing input: Name");
		if (service.exists(teamname))
			return Respond.invalid("teamname", "Team already exists");
		if (Names.isReserved(teamname))
			return Respond.invalid("teamname", "This is a reserved word");
		team = service.insert(team);
		notificationService.teamCreated(team).send();
		return Respond.created(Teams.mapForManager(team));
	}

	@PUT
	@Path("{teamname}")
	public Response update(@PathParam("teamname") String teamname, Team team) {
		Team fromDb = authorizedGetTeam(teamname);
		if (fromDb == null)
			return Respond.notFound();
		if (Strings.isNullOrEmpty(team.teamname))
			return Respond.invalid("teamname", "Missing input: Teamname");
		if (Names.isReserved(team.teamname))
			return Respond.invalid("teamname", "This is a reserved word");
		if (Strings.isNullOrEmpty(team.name))
			return Respond.invalid("name", "Missing input: Name");
		List<NotificationJob> notifications = updateUsers(team);
		Beans.populateProperties(team, fromDb, "teamname", "name", "users");
		fromDb = service.update(fromDb);
		for (NotificationJob notification : notifications)
			notification.send();
		return Respond.ok(Teams.mapForManager(fromDb));
	}

	@PUT
	@Path("{teamname}/activeuntil")
	public Response setActiveUntil(@PathParam("teamname") String teamname, Map<String, Object> data) {
		Team team = authorizedGetTeam(teamname);
		if (team == null)
			return Respond.notFound();
		String date = ObjectMap.fromMap(data).getString("activeUntil");
		for (User user : team.users) {
			user.settings.activeUntil = Dates.fromString(date);
			userService.update(user);
		}
		return Respond.ok(new HashMap<>());
	}

	@DELETE
	@Path("{teamname}")
	public Response delete(@PathParam("teamname") String teamname) {
		Team team = service.getForTeamname(teamname);
		if (team == null)
			return Respond.notFound();
		deleteService.delete(team);
		notificationService.teamDeleted(team).send();
		return Respond.ok(new HashMap<>());
	}

	private List<NotificationJob> updateUsers(Team team) {
		List<NotificationJob> notifications = new ArrayList<>();
		Team fromDb = service.getForTeamname(team.teamname);
		List<User> users = new ArrayList<>(team.users);
		team.users.clear();
		for (User user : users) {
			user = userService.getForUsername(user.username);
			team.users.add(user);
			if (fromDb.users.contains(user))
				continue;
			NotificationJob notification = notificationService.memberAdded(team, user);
			if (membershipService.addMemberships(user, team))
				notifications.add(notification);
		}
		for (User user : fromDb.users) {
			if (team.users.contains(user))
				continue;
			user = userService.getForUsername(user.username);
			NotificationJob notification = notificationService.memberRemoved(team, user);
			if (membershipService.removeMemberships(user, team))
				notifications.add(notification);
		}
		return notifications;
	}

	private Team authorizedGetTeam(String teamname) {
		User user = userService.getCurrentUser();
		if (!user.isUserManager())
			throw new UnauthorizedException("Not authorized to manage teams");
		return service.getForTeamname(teamname);
	}

}
