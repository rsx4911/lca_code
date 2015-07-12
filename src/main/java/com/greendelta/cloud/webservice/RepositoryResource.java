package com.greendelta.cloud.webservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

	@DELETE
	@Path("delete/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("name") String name) {
		if (!repositoryService.exists(name)) {
			User user = userService.getCurrentUser();
			throw new RepositoryNotFoundException(Strings.concat(user.getName(), "/", name));
		}
		repositoryService.delete(name);
		return Respond.ok();
	}

}
