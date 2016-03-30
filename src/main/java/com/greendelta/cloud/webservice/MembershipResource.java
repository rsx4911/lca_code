package com.greendelta.cloud.webservice;

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

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.Membership;
import com.greendelta.cloud.model.Role;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.AccessService;
import com.greendelta.cloud.service.MembershipService;
import com.greendelta.cloud.service.PagedResult;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.TeamService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.mapper.MembershipMapper;

@Path("membership")
@Produces(MediaType.APPLICATION_JSON)
public class MembershipResource {

	private final MembershipService service;
	private final AccessService accessService;
	private final UserService userService;
	private final TeamService teamService;

	@Inject
	public MembershipResource(MembershipService service, AccessService accessService,
			UserService userService,
			TeamService teamService) {
		this.service = service;
		this.accessService = accessService;
		this.userService = userService;
		this.teamService = teamService;
	}

	@POST
	@Path("{group}/{repo}/user/{username}/{role}")
	public Response addUserRole(@PathParam("group") String group, @PathParam("repo") String repo,
			@PathParam("username") String username, @PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean added = service.addMembership(user, path, role);
		return Respond.ok(Collections.singletonMap("added", added));
	}

	@POST
	@Path("{group}/{repo}/team/{teamname}/{role}")
	public Response addTeamRole(@PathParam("group") String group, @PathParam("repo") String repo,
			@PathParam("teamname") String teamname, @PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean added = service.addMemberships(team, path, role);
		return Respond.ok(Collections.singletonMap("added", added));
	}

	@PUT
	@Path("{group}/{repo}/user/{username}/{role}")
	public Response setUserRole(@PathParam("group") String group, @PathParam("repo") String repo,
			@PathParam("username") String username, @PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean changed = service.setRole(user, path, role);
		return Respond.ok(Collections.singletonMap("changed", changed));
	}

	@PUT
	@Path("{group}/{repo}/team/{teamname}/{role}")
	public Response setTeamRole(@PathParam("group") String group, @PathParam("repo") String repo,
			@PathParam("teamname") String teamname, @PathParam("role") Role role) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean changed = service.setRole(team, path, role);
		return Respond.ok(Collections.singletonMap("changed", changed));
	}

	@DELETE
	@Path("{group}/{repo}/user/{username}")
	public Response removeUserRole(@PathParam("group") String group, @PathParam("repo") String repo,
			@PathParam("username") String username) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean removed = service.removeMembership(user, path);
		return Respond.ok(Collections.singletonMap("removed", removed));
	}

	@DELETE
	@Path("{group}/{repo}/team/{teamname}")
	public Response removeTeamRole(@PathParam("group") String group, @PathParam("repo") String repo,
			@PathParam("teamname") String teamname) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean removed = service.removeMemberships(team, path);
		return Respond.ok(Collections.singletonMap("removed", removed));
	}

	@GET
	@Path("{group}/{repo}")
	public Response getAll(@PathParam("group") String group, @PathParam("repo") String repo,
			@QueryParam("filter") @DefaultValue("") String filter) {
		String path = group;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			path = Repository.toId(group, repo);
		PagedResult<Membership> memberships = service.getMemberships(path, filter);
		return Respond.ok(memberships.toClient(new MembershipMapper()::map));
	}

	private String getAuthorizedPath(String group, String repo) {
		String path = group;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			path = Repository.toId(group, repo);
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canEditMembers(currentUser, path))
			throw new UnauthorizedAccessException(path, "CHANGE_ROLE");
		return path;
	}

}
