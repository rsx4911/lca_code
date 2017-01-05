package com.greendelta.collaboration.webservice;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.PagedResult;
import com.greendelta.collaboration.service.TeamService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.util.Bytes;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.webservice.util.Teams;
import com.sun.jersey.multipart.FormDataParam;

@Path("team")
public class TeamResource {

	private final TeamService service;
	private final UserService userService;

	@Inject
	public TeamResource(TeamService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("module") Module module) {
		PagedResult<Team> result = service.getAll(page, filter);
		if (module == null)
			return Respond.ok(result.toClient(Teams::mapForOthers));
		switch (module) {
		case MESSAGING:
			User currentUser = userService.getCurrentUser();
			if (currentUser.admin)
				return Respond.ok(Teams.mapForOthers(result.data));
			List<Team> teams = Collections.filter(result.data, (team) -> team.users.contains(currentUser));
			return Respond.ok(Teams.mapForOthers(teams));
		default:
			return Respond.ok(Teams.mapForOthers(result.data));
		}
	}

	@PUT
	@Path("avatar/{teamname}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(@PathParam("teamname") String teamname, @FormDataParam("file") InputStream file) {
		Team team = authorizedGetTeam(teamname);
		if (team == null)
			return Respond.notFound();
		if (file == null)
			team.avatar = null;
		else
			team.avatar = Bytes.readStream(file);
		team = service.update(team);
		return getAvatar(teamname);
	}

	@GET
	@Path("avatar/{teamname}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("teamname") String teamname) {
		Team team = service.getForTeamname(teamname);
		if (team == null)
			return Respond.notFound(teamname);
		return Respond.ok(team.avatar, "avatar-team.png");
	}

	private Team authorizedGetTeam(String teamname) {
		User user = userService.getCurrentUser();
		if (!user.admin)
			throw new UnauthorizedException("Only admin can change teams");
		return service.getForTeamname(teamname);
	}

}
