package com.greendelta.cloud.webservice.admin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.service.GroupService;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.TeamService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.Respond;

@Path("admin/area")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAreaResource {

	private final RepositoryService repoService;
	private final UserService userService;
	private final GroupService groupService;
	private final TeamService teamService;

	@Inject
	public AdminAreaResource(RepositoryService repoService, UserService service, GroupService groupService,
			TeamService teamService) {
		this.repoService = repoService;
		this.userService = service;
		this.groupService = groupService;
		this.teamService = teamService;
	}

	@GET
	@Path("count")
	public Response getCounts() {
		Map<String, Object> result = new HashMap<>();
		result.put("repositories", repoService.getCount(true));
		result.put("users", userService.getCount());
		result.put("groups", groupService.getCount(true));
		result.put("teams", teamService.getCount());
		return Respond.ok(result);
	}

}
