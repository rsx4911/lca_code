package com.greendelta.collaboration.webservice.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.ReindexService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.service.TeamService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("admin/area")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAreaResource {

	private final RepositoryService repoService;
	private final ReindexService reindexService;
	private final SearchService searchService;
	private final UserService userService;
	private final GroupService groupService;
	private final TeamService teamService;

	@Inject
	public AdminAreaResource(RepositoryService repoService, ReindexService reindexService, SearchService searchService,
			UserService service, GroupService groupService, TeamService teamService) {
		this.repoService = repoService;
		this.reindexService = reindexService;
		this.searchService = searchService;
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

	@PUT
	@Path("reindex")
	public Response reindex() {
		searchService.clearIndex();
		List<Repository> repos = repoService.getAllAccessible();
		for (Repository repo : repos) {
			reindexService.reindex(repo);
		}
		return Respond.ok(new HashMap<>());
	}
}
