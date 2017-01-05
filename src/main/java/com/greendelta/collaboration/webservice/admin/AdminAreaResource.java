package com.greendelta.collaboration.webservice.admin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.TeamService;
import com.greendelta.collaboration.service.UpgradeService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("admin/area")
public class AdminAreaResource {

	private final RepositoryService repoService;
	private final UserService userService;
	private final GroupService groupService;
	private final TeamService teamService;
	private final UpgradeService upgradeService;

	@Inject
	public AdminAreaResource(RepositoryService repoService, UserService service, GroupService groupService,
			TeamService teamService, UpgradeService upgradeService) {
		this.repoService = repoService;
		this.userService = service;
		this.groupService = groupService;
		this.teamService = teamService;
		this.upgradeService = upgradeService;
	}

	@GET
	@Path("count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCounts() {
		Map<String, Object> result = new HashMap<>();
		result.put("repositories", repoService.getCount(true));
		result.put("users", userService.getCount());
		result.put("groups", groupService.getCount(true));
		result.put("teams", teamService.getCount());
		return Respond.ok(result);
	}

	@GET
	@Path("upgradeAvailable")
	@Produces(MediaType.TEXT_PLAIN)
	public Response upgradeAvailable() {
		if (!upgradeService.upgradeAvailable())
			return Respond.ok("false");
		return Respond.ok("true");
	}
}
