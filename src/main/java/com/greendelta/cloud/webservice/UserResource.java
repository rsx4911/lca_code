package com.greendelta.cloud.webservice;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;
import org.openlca.cloud.util.ObjectMap;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.NotificationService;
import com.greendelta.cloud.service.NotificationService.NotificationJob;
import com.greendelta.cloud.service.PagedResult;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Beans;
import com.greendelta.cloud.util.Bytes;
import com.greendelta.cloud.util.Password;
import com.greendelta.cloud.webservice.mapper.UserMapper;
import com.sun.jersey.multipart.FormDataParam;

@Path("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserService service;
	private final RepositoryService repoService;
	private final NotificationService notificationService;

	@Inject
	public UserResource(UserService service, RepositoryService repoService, NotificationService notificationService) {
		this.service = service;
		this.repoService = repoService;
		this.notificationService = notificationService;
	}

	@GET
	@Path("{username}")
	public Response get(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		Map<String, Object> userMap = new UserMapper().mapForSelf(user);
		if (user.admin)
			userMap.put("lastAdmin", service.isLastAdmin(user));
		return Respond.ok(userMap);
	}

	@GET
	public Response getAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("filter") @DefaultValue("") String filter) {
		PagedResult<User> result = service.getAll(page, filter);
		return Respond.ok(result.toClient(new UserMapper()::mapForOthers));
	}

	@GET
	@Path("avatar/{username}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound(username);
		return Respond.ok(user.avatar, "avatar-user.png");
	}

	@PUT
	@Path("{username}")
	public Response update(@PathParam("username") String username, User user) {
		User fromDb = authorizedGetUser(username);
		if (fromDb == null)
			return Respond.notFound();
		if (Strings.isNullOrEmpty(user.name))
			return Respond.invalid("name", "Missing input: Name");
		if (Strings.isNullOrEmpty(user.email))
			return Respond.invalid("email", "Missing input: Email");
		User userWithSameMail = service.getForEmail(user.email);
		if (userWithSameMail != null && !userWithSameMail.username.equals(username))
			return Respond.invalid("email", "Email is already in use");
		Beans.populateProperties(user, fromDb, "name", "email");
		User currentUser = service.getCurrentUser();
		if (currentUser.admin)
			Beans.populateProperties(user, fromDb, "canCreateGroups", "canCreateRepositories", "admin");
		fromDb = service.update(fromDb);
		return Respond.ok(new UserMapper().mapForSelf(fromDb));
	}

	@DELETE
	public Response delete() {
		User user = service.getCurrentUser();
		if (user == null)
			return Respond.notFound();
		NotificationJob notification = notificationService.userDeleted(user);
		repoService.deleteAllFor(user);
		service.delete(user.getId());
		notification.send();
		service.logout();
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("setpassword/{username}")
	public Response setPassword(@PathParam("username") String username,
			Map<String, Object> passwords) {
		ObjectMap map = ObjectMap.fromMap(passwords);
		String password = map.get("password");
		String password2 = map.get("password2");
		if (Strings.isNullOrEmpty(password))
			return Respond.invalid("password", "Missing input: Password");
		if (!Password.isValid(password)) {
			String passwordMessage = "Password must consist of at least 8 characters and must contain at least 1 digit, 2 different lowercase letters and 2 different uppercase letters";
			return Respond.invalid("password", passwordMessage);
		}
		if (!password.equals(password2))
			return Respond.invalid("password2", "Passwords are not equal");
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		service.setPassword(user, password);
		service.update(user);
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("avatar/{username}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(@PathParam("username") String username, @FormDataParam("file") InputStream file) {
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		if (file == null)
			user.avatar = null;
		else
			user.avatar = Bytes.readStream(file);
		user = service.update(user);
		return getAvatar(username);
	}

	@GET
	@Path("twoFactorAuth/{username}")
	public Response showTwoFactorAuthentication(@PathParam("username") String username) {
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		Map<String, Object> response = new HashMap<>();
		response.put("url", service.getTwoFactorUrl(user));
		response.put("key", user.twoFactorSecret);
		response.put("enabled", true);
		return Respond.ok(response);
	}

	@PUT
	@Path("twoFactorAuth/{username}/{enable}")
	public Response toggleTwoFactorAuthentication(@PathParam("username") String username,
			@PathParam("enable") boolean enable) {
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		if (!enable) {
			user.twoFactorSecret = null;
			user = service.update(user);
			return Respond.ok(Collections.emptyMap());
		}
		String url = service.enableTwoFactorAuthentication(user);
		Map<String, Object> response = new HashMap<>();
		response.put("url", url);
		response.put("key", user.twoFactorSecret);
		response.put("enabled", true);
		return Respond.ok(response);
	}

	private User authorizedGetUser(String username) {
		User user = service.getCurrentUser();
		if (!Strings.isNullOrEmpty(username) && !username.equals(user.username)) {
			if (!user.admin)
				throw new UnauthorizedException("Only admin can change other users");
			user = service.getForUsername(username);
		}
		return user;
	}

}
