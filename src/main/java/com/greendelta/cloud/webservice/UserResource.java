package com.greendelta.cloud.webservice;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.error.UserNotFoundException;
import org.openlca.cloud.util.ObjectMap;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.AccessService;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;

@Path("user")
public class UserResource {

	private UserService service;
	private AccessService sharingService;
	private RepositoryService repositoryService;
	private String adminKey;

	@Inject
	public UserResource(UserService service, AccessService sharingService,
			RepositoryService repositoryService,
			@Named("admin.key") String adminKey) {
		this.service = service;
		this.sharingService = sharingService;
		this.repositoryService = repositoryService;
		this.adminKey = adminKey;
	}

	@POST
	@Path("create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(Map<String, Object> credentials) {
		ObjectMap formMap = ObjectMap.fromMap(credentials);
		String adminKey = formMap.getString("adminKey");
		if (!this.adminKey.equals(adminKey))
			return Respond.unauthorized();
		String username = formMap.getString("username");
		String password = formMap.getString("password");
		if (Strings.isNullOrEmpty(username))
			return Respond.unauthorized("Insufficient credentials");
		if (Strings.isNullOrEmpty(password))
			return Respond.unauthorized("Insufficient credentials");
		if (service.getForName(username) != null)
			return Respond.conflict("User already exists");
		service.createNewUser(username, password);
		return Respond.created();
	}

	@DELETE
	@Path("delete/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("username") String username,
			Map<String, Object> data) {
		ObjectMap formMap = ObjectMap.fromMap(data);
		String adminKey = formMap.getString("adminKey");
		if (!this.adminKey.equals(adminKey))
			return Respond.unauthorized();
		User user = service.getForName(username);
		if (user == null)
			throw new UserNotFoundException(username);
		repositoryService.deleteAllFor(user);
		service.delete(user.getId());
		return Respond.ok();
	}

	@GET
	@Path("shared")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccessList() {
		User user = service.getCurrentUser();
		return Respond.ok(sharingService.getAccessListForUser(user.getName()));
	}

}
