package com.greendelta.collaboration.webservice.user;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Beans;
import com.greendelta.collaboration.util.Bytes;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Module;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Users;
import com.greendelta.search.wrapper.SearchResult;
import com.sun.jersey.multipart.FormDataParam;

@Path("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserService service;
	private final MessagingService messagingService;
	private final AccessService accessService;

	@Inject
	public UserResource(UserService service, MessagingService messagingService, AccessService accessService) {
		this.service = service;
		this.messagingService = messagingService;
		this.accessService = accessService;
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("module") Module module,
			@QueryParam("repositoryPath") String repositoryPath) {
		SearchResult<User> result = service.getVisible(page, pageSize, filter);
		User currentUser = service.getCurrentUser();
		if (module == null)
			return Respond.ok(SearchResults.convert(result,
					currentUser.isUserManager()
							? Users::mapForAdmin
							: Users::mapForOthers));
		List<User> users = result.data;
		switch (module) {
		case MESSAGING:
			users = messagingService.filterUsers(result.data);
			break;
		case REVIEW:
			if (repositoryPath == null)
				return Respond.badRequest("No repository specified");
			users = Collections.filter(result.data, (user) -> !accessService.canReviewIn(user, repositoryPath));
			break;
		default:
			break;
		}

		return Respond.ok(Client.map(users, Users::mapForOthers));
	}

	@GET
	@Path("{username}")
	public Response get(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		User currentUser = service.getCurrentUser();
		Map<String, Object> userMap = currentUser.username.equals(username) || currentUser.isUserManager()
				? Users.mapForSelf(user)
				: Users.mapForOthers(user);
		if (user.isUserManager())
			userMap.put("lastAdmin", service.isLastAdmin(user));
		return Respond.ok(userMap);
	}

	@GET
	@Path("avatar/{username}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("username") String username) {
		if ("null".equals(username) || username == null)
			return Respond.ok(null, "avatar-user.png");
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.ok(null, "avatar-user.png");
		return Respond.ok(user.avatar, "avatar-user.png");
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
		userWithSameMail = service.getForUsername(user.email);
		if (userWithSameMail != null && !userWithSameMail.username.equals(username))
			return Respond.invalid("email", "Email is already in use");
		Beans.populateProperties(user, fromDb, "name", "email");
		User currentUser = service.getCurrentUser();
		if (currentUser.isAdmin()) {
			Beans.populateProperties(user.settings, fromDb.settings, "admin");
		}
		if (currentUser.isUserManager()) {
			Beans.populateProperties(user.settings, fromDb.settings, "userManager", "dataManager", "canCreateGroups",
					"canCreateRepositories", "noOfRepositories", "maxSize", "activeUntil");
		}
		fromDb = service.update(fromDb);
		return Respond.ok(Users.mapForSelf(fromDb));
	}

	@PUT
	@Path("avatar/{username}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(
			@PathParam("username") String username,
			@FormDataParam("file") InputStream file) {
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

	@PUT
	@Path("setpassword/{username}")
	public Response setPassword(@PathParam("username") String username, Map<String, Object> passwords) {
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
	@Path("twoFactorAuth/{username}/{enable}")
	public Response toggleTwoFactorAuthentication(
			@PathParam("username") String username,
			@PathParam("enable") boolean enable) {
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		if (!enable) {
			user.twoFactorSecret = null;
			user = service.update(user);
			return Respond.ok(new HashMap<>());
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
			if (!user.isUserManager())
				throw new UnauthorizedException("Not authorized to manage users");
			user = service.getForUsername(username);
		}
		return user;
	}

}
