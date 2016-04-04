package com.greendelta.cloud.webservice.admin;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.GroupService;
import com.greendelta.cloud.service.NotificationService;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Names;
import com.greendelta.cloud.webservice.Respond;
import com.greendelta.cloud.webservice.mapper.UserMapper;

@Path("admin/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserService service;
	private final GroupService groupService;
	private final RepositoryService repoService;
	private final NotificationService notificationService;

	@Inject
	public UserResource(UserService service, GroupService groupService, RepositoryService repoService,
			NotificationService notificationService) {
		this.service = service;
		this.groupService = groupService;
		this.repoService = repoService;
		this.notificationService = notificationService;
	}

	@POST
	@Path("{username}")
	public Response create(@PathParam("username") String username, User user) {
		if (Strings.isNullOrEmpty(username))
			return Respond.invalid("username", "Missing input: Username");
		if (Names.isValid(username))
			return Respond.invalid("username",
					"Username must consist of at least 4 characters and can only contain characters, numbers and _");
		if (groupService.exists(username)) // user or group exists
			return Respond.invalid("username", "Name is already in use");
		if (Names.isReserved(username))
			return Respond.invalid("username", "This is a reserved word");
		if (Strings.isNullOrEmpty(user.name))
			return Respond.invalid("name", "Missing input: Name");
		if (Strings.isNullOrEmpty(user.email))
			return Respond.invalid("email", "Missing input: Email");
		String password = generatePassword();
		service.setPassword(user, password);
		user = service.insert(user);
		notificationService.userCreated(user).send();
		return Respond.created(new UserMapper().mapForSelf(user));
	}

	private String generatePassword() {
		// TODO
		return "12345sechs";
	}

	@DELETE
	@Path("{username}")
	public Response delete(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		repoService.deleteAllFor(user);
		service.delete(user.getId());
		notificationService.userDeleted(user).send();
		return Respond.ok(new HashMap<>());
	}

}
