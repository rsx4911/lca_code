package com.greendelta.cloud.webservice.admin;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.TeamService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Beans;
import com.greendelta.cloud.util.Bytes;
import com.greendelta.cloud.util.Names;
import com.greendelta.cloud.webservice.Respond;
import com.greendelta.cloud.webservice.mapper.TeamMapper;
import com.sun.jersey.multipart.FormDataParam;

@Path("admin/team")
public class TeamResource {

	private final TeamService service;
	private final UserService userService;

	@Inject
	public TeamResource(TeamService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@GET
	@Path("{teamname}")
	public Response get(@PathParam("teamname") String teamname) {
		Team team = service.getForTeamname(teamname);
		if (team == null)
			return Respond.notFound();
		return Respond.ok(new TeamMapper().mapForAdmin(team));
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
		Beans.populateProperties(team, fromDb, "teamname", "name");
		fromDb = service.update(fromDb);
		return Respond.ok(new TeamMapper().mapForAdmin(fromDb));
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
	public Response getAvatar(@PathParam("username") String teamname) {
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
