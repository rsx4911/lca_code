package com.greendelta.cloud.webservice.admin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.Respond;

@Path("admin/area")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAreaResource {

	private UserService userService;
	private RepositoryService repoService;

	@Inject
	public AdminAreaResource(UserService service, RepositoryService repoService) {
		this.userService = service;
		this.repoService = repoService;
	}

	@GET
	@Path("count")
	public Response getCounts() {
		Map<String, Object> result = new HashMap<>();
		result.put("users", userService.getCount());
		result.put("repositories", repoService.getCount(true));
		return Respond.ok(result);
	}

}
