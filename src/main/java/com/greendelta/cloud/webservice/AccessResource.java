package com.greendelta.cloud.webservice;

import java.util.Set;

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
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;

import static org.openlca.cloud.util.Strings.concat;

@Path("access")
public class AccessResource {

	private AccessService accessService;
	private RepositoryService repoService;
	private UserService userService;

	@Inject
	public AccessResource(AccessService accessService,
			RepositoryService repoService, UserService userService) {
		this.accessService = accessService;
		this.repoService = repoService;
		this.userService = userService;
	}

	@POST
	@Path("share/{name}/{with}")
	public Response share(@PathParam("name") String name,
			@PathParam("with") String with) {
		accessService.share(name, with);
		return Respond.ok();
	}

	@POST
	@Path("unshare/{name}/{with}")
	public Response unshare(@PathParam("name") String name,
			@PathParam("with") String with) {
		accessService.unshare(name, with);
		return Respond.ok();
	}

	@GET
	@Path("shared/{repoOwner}/{repoName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccessListForRepository(
			@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName) {
		String repoId = concat(repoOwner, "/", repoName);
		// implicitly checks if repository exists and user has access
		repoService.getForId(repoId);
		return Respond.ok(accessService.getAccessListForRepository(repoId));
	}

	@GET
	@Path("shared/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccessList() {
		User user = userService.getCurrentUser();
		return Respond.ok(accessService.getAccessListForUser(user.getName()));
	}

	@GET
	@Path("{repoOwner}/{repoName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAccess(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName) {
		String repoId = concat(repoOwner, "/", repoName);
		User user = userService.getCurrentUser();
		// implicitly checks if repository exists and user has access
		repoService.getForId(repoId);
		Set<String> access = accessService.getAccessListForRepository(repoId);
		if (!access.contains(user.getName()))
			return Respond.forbidden();
		return Respond.ok();
	}

}
