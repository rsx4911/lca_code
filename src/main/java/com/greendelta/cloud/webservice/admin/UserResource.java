package com.greendelta.cloud.webservice.admin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.util.ObjectMap;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.BeanUtils;
import com.greendelta.cloud.webservice.Respond;
import com.greendelta.cloud.webservice.client.UserMapper;

@Path("admin/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private UserService service;
	private RepositoryService repoService;

	@Inject
	public UserResource(UserService service, RepositoryService repoService) {
		this.service = service;
		this.repoService = repoService;
	}

	@POST
	@Path("{username}")
	public Response create(@PathParam("username") String username, User user) {
		if (Strings.isNullOrEmpty(username))
			return Respond.badRequest("Missing input: Username");
		if (Strings.isNullOrEmpty(user.name))
			return Respond.badRequest("Missing input: Name");
		if (Strings.isNullOrEmpty(user.email))
			return Respond.badRequest("Missing input: Email");
		if (service.getForUsername(username) != null)
			return Respond.conflict("User already exists");
		String password = generatePassword();
		service.setPassword(user, password);
		user = service.insert(user);
		return Respond.created(new UserMapper().map(user));
	}

	@PUT
	@Path("{username}")
	public Response update(@PathParam("username") String username, User user) {
		User fromDb = service.getForUsername(username);
		if (fromDb == null)
			return Respond.notFound();
		if (Strings.isNullOrEmpty(user.name))
			return Respond.badRequest("Missing input: Name");
		if (Strings.isNullOrEmpty(user.email))
			return Respond.badRequest("Missing input: Email");
		BeanUtils.populateProperties(user, fromDb, "name", "email");
		fromDb = service.update(fromDb);
		return Respond.ok(new UserMapper().map(fromDb));
	}

	private String generatePassword() {
		// TODO
		return "12345sechs";
	}

	@PUT
	@Path("{username}/setpassword")
	public Response setPassword(@PathParam("username") String username,
			Map<String, Object> passwords) {
		ObjectMap map = ObjectMap.fromMap(passwords);
		String password = map.get("password");
		String password2 = map.get("password");
		if (Strings.isNullOrEmpty(password))
			return Respond.badRequest("Missing input: Password");
		if (!password.equals(password2))
			return Respond.badRequest("Passwords are not equal");
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		service.setPassword(user, password);
		service.update(user);
		return Respond.ok();
	}

	@DELETE
	@Path("{username}")
	public Response delete(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		repoService.deleteAllFor(user);
		service.delete(user.getId());
		return Respond.ok(new HashMap<>());
	}

	@GET
	@Path("{username}")
	public Response get(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		return Respond.ok(new UserMapper().map(user));
	}

	@GET
	public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("filter") @DefaultValue("") String filter) {
		Map<String, Object> result = new HashMap<>();
		result.put("total", service.getCount());
		result.put("data", new UserMapper().map(service.getAll(page, filter)));
		return Respond.ok(result);
	}

}
