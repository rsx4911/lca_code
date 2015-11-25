package com.greendelta.cloud.webservice;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.error.RepositoryNotFoundException;

import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;

import static org.openlca.cloud.util.Strings.concat;

@Path("repository")
public class RepositoryResource {

	private RepositoryService repoService;
	private UserService userService;

	@Inject
	public RepositoryResource(RepositoryService repoService,
			UserService userService) {
		this.repoService = repoService;
		this.userService = userService;
	}

	@POST
	@Path("create/{name}")
	public Response create(@PathParam("name") String name) {
		if (repoService.exists(name)) {
			String message = concat("Repository ", name, " already exists");
			return Respond.conflict(message);
		}
		repoService.create(name);
		return Respond.created();
	}

	@GET
	@Path("exists/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response exists(@PathParam("name") String name) {
		return Respond.ok(String.valueOf(repoService.exists(name)));
	}

	@DELETE
	@Path("delete/{name}")
	public Response delete(@PathParam("name") String name) {
		checkRepositoryExists(name);
		repoService.delete(name);
		return Respond.ok();
	}

	private void checkRepositoryExists(String name) {
		if (repoService.exists(name))
			return;
		User user = userService.getCurrentUser();
		String repoId = concat(user.getName(), "/", name);
		throw new RepositoryNotFoundException(repoId);
	}

}
