package com.greendelta.collaboration.webservice.user;

import java.util.Collections;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.service.user.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Memberships;
import com.greendelta.search.wrapper.SearchResult;

@Path("membership")
@Produces(MediaType.APPLICATION_JSON)
public class MembershipResource {

	private final MembershipService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final TeamService teamService;
	private final NotificationService notificationService;

	@Inject
	public MembershipResource(MembershipService service, RepositoryService repoService, UserService userService,
			TeamService teamService, NotificationService notificationService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.teamService = teamService;
		this.notificationService = notificationService;
	}

	@POST
	@Path("{group}/{repo}/user/{username}/{role}")
	public Response addUserRole(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("username") String username,
			@PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean added = service.addMembership(user, path, role);
		if (added)
			if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
				notificationService.memberAdded(repoService.get(group, repo), user).send();
			else
				notificationService.memberAdded(group, user).send();
		return Respond.ok(Collections.singletonMap("added", added));
	}

	@POST
	@Path("{group}/{repo}/team/{teamname}/{role}")
	public Response addTeamRole(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("teamname") String teamname,
			@PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean added = service.addMemberships(team, path, role);
		if (added)
			if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
				notificationService.memberAdded(repoService.get(group, repo), team).send();
			else
				notificationService.memberAdded(group, team).send();
		return Respond.ok(Collections.singletonMap("added", added));
	}

	@PUT
	@Path("{group}/{repo}/user/{username}/{role}")
	public Response updateUserRole(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("username") String username,
			@PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean updated = service.setRole(user, path, role);
		if (updated)
			if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
				notificationService.roleChanged(repoService.get(group, repo), user).send();
			else
				notificationService.roleChanged(group, user).send();
		return Respond.ok(Collections.singletonMap("updated", updated));
	}

	@PUT
	@Path("{group}/{repo}/team/{teamname}/{role}")
	public Response updateTeamRole(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("teamname") String teamname,
			@PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean updated = service.setRole(team, path, role);
		if (updated)
			if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
				notificationService.roleChanged(repoService.get(group, repo), team).send();
			else
				notificationService.roleChanged(group, team).send();
		return Respond.ok(Collections.singletonMap("updated", updated));
	}

	@DELETE
	@Path("{group}/{repo}/user/{username}")
	public Response removeUserRole(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("username") String username) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		NotificationJob notification = null;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notification = notificationService.memberRemoved(repoService.get(group, repo), user);
		else
			notification = notificationService.memberRemoved(group, user);
		boolean removed = service.removeMembership(user, path);
		if (removed)
			notification.send();
		return Respond.ok(Collections.singletonMap("removed", removed));
	}

	@DELETE
	@Path("{group}/{repo}/team/{teamname}")
	public Response removeTeamRole(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("teamname") String teamname) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		NotificationJob notification = null;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notification = notificationService.memberRemoved(repoService.get(group, repo), team);
		else
			notification = notificationService.memberRemoved(group, team);
		boolean removed = service.removeMemberships(team, path);
		if (removed)
			notification.send();
		return Respond.ok(Collections.singletonMap("removed", removed));
	}

	@GET
	@Path("{group}/{repo}")
	public Response getAll(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@QueryParam("filter") @DefaultValue("") String filter) {
		String path = group;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			path = Repository.toId(group, repo);
		SearchResult<Membership> memberships = service.getMemberships(path, filter);
		return Respond.ok(SearchResults.convert(memberships, Memberships::map));
	}

	private String getAuthorizedPath(String group, String repo) {
		String path = group;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			path = Repository.toId(group, repo);
		return path;
	}

}
