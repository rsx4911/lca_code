package com.greendelta.cloud.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.AccessService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;

@Path("access")
public class AccessResource {

	private AccessService service;
	private RepositoryService repoService;
	private UserService userService;

	@Inject
	public AccessResource(AccessService service, RepositoryService repoService,
			UserService userService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
	}

	@POST
	@Path("share/{group}/{name}/{with}")
	public Response share(@PathParam("group") String group,
			@PathParam("name") String name, @PathParam("with") String with) {
		Repository repo = repoService.get(group, name);
		service.share(repo, with);
		return Respond.ok();
	}

	@POST
	@Path("unshare/{group}/{name}/{with}")
	public Response unshare(@PathParam("group") String group,
			@PathParam("name") String name, @PathParam("with") String with) {
		Repository repo = repoService.get(group, name);
		service.unshare(repo, with);
		return Respond.ok();
	}

	@GET
	@Path("shared/{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccessListForRepository(
			@PathParam("group") String group, @PathParam("name") String name) {
		Repository repo = repoService.get(group, name);
		return Respond.ok(service.getAccessListForRepository(repo));
	}

	@GET
	@Path("shared/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccessList() {
		User user = userService.getCurrentUser();
		return Respond.ok(service.getAccessListForUser(user.username));
	}

	@GET
	@Path("{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAccess(@PathParam("group") String group,
			@PathParam("name") String name) {
		// implicitly checks for existence and access
		repoService.get(group, name);
		return Respond.ok();
	}

}
