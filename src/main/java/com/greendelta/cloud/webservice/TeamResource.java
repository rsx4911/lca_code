package com.greendelta.cloud.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.service.TeamService;

@Path("team")
public class TeamResource {

	private final TeamService service;

	@Inject
	public TeamResource(TeamService service) {
		this.service = service;
	}

	@GET
	@Path("avatar/{teamname}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("username") String teamname) {
		Team team = service.getForTeamname(teamname);
		if (team == null)
			return Respond.notFound(teamname);
		return Respond.ok(team.avatar, "avatar-team.png");
	}

}
