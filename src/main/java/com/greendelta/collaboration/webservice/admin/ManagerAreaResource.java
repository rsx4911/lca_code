package com.greendelta.collaboration.webservice.admin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("manager/area")
@Produces(MediaType.APPLICATION_JSON)
public class ManagerAreaResource {

	private final RepositoryService repoService;
	private final UserService userService;
	private final GroupService groupService;
	private final TeamService teamService;

	@Inject
	public ManagerAreaResource(RepositoryService repoService, UserService service, GroupService groupService,
			TeamService teamService) {
		this.repoService = repoService;
		this.userService = service;
		this.groupService = groupService;
		this.teamService = teamService;
	}

	@GET
	@Path("count")
	public Response getCounts(@Context HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		User currentUser = userService.getCurrentUser();
		if (currentUser.isAdmin()) {
			result.put("repositories", repoService.getCount(true));
			result.put("groups", groupService.getCount(true));
		}
		result.put("users", userService.getCount());
		result.put("teams", teamService.getCount());
		return Respond.ok(result);
	}

}
