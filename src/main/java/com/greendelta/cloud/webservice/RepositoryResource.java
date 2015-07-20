package com.greendelta.cloud.webservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.error.RepositoryNotFoundException;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.service.repository.RepositoryService;
import com.greendelta.cloud.util.Strings;

@Path("repository")
public class RepositoryResource {

	private RepositoryService repositoryService;
	private UserService userService;

	@Inject
	public RepositoryResource(RepositoryService repositoryService, UserService userService) {
		this.repositoryService = repositoryService;
		this.userService = userService;
	}

	@POST
	@Path("create/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("name") String name) {
		if (repositoryService.exists(name))
			return Respond.conflict(Strings.concat("Repository ", name, " already exists"));
		repositoryService.create(name);
		return Respond.created();
	}

	@POST
	@Path("share/{name}/{with}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response share(@PathParam("name") String name, @PathParam("with") String with) {
		repositoryService.share(name, with);
		return Respond.ok();
	}

	@POST
	@Path("unshare/{name}/{with}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unshare(@PathParam("name") String name, @PathParam("with") String with) {
		repositoryService.unshare(name, with);
		return Respond.ok();
	}

	@GET
	@Path("shared/{repositoryOwner}/{repositoryName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSharedWithFor(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		return Respond.ok(repositoryService.getSharedWithFor(repositoryId));
	}

	@DELETE
	@Path("delete/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("name") String name) {
		checkRepositoryExists(name);
		repositoryService.delete(name);
		return Respond.ok();
	}

	private void checkRepositoryExists(String name) {
		if (repositoryService.exists(name))
			return;
		User user = userService.getCurrentUser();
		throw new RepositoryNotFoundException(Strings.concat(user.getName(), "/", name));
	}

}
