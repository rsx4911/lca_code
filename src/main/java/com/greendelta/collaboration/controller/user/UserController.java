package com.greendelta.collaboration.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Users;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.util.SearchResults;

@RestController
@RequestMapping("ws/user")
public class UserController {

	private final UserService service;
	private final MessagingService messagingService;
	private final AccessService accessService;
	private final SettingsService settingsService;

	@Autowired
	public UserController(UserService service, MessagingService messagingService, AccessService accessService,
			SettingsService settingsService) {
		this.service = service;
		this.messagingService = messagingService;
		this.accessService = accessService;
		this.settingsService = settingsService;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "module", required = false) Module module,
			@RequestParam(name = "repositoryPath", required = false) String repositoryPath) {
		var result = service.getVisible(page, pageSize, filter);
		var currentUser = service.getCurrentUser();
		if (module == null)
			return Response.ok(SearchResults.convert(result,
					currentUser.isUserManager()
							? Users::mapForAdmin
							: Users::mapForOthers));
		var users = result.data;
		switch (module) {
		case MESSAGING:
			users = messagingService.filterVisible(result.data);
			break;
		case REVIEW:
			if (repositoryPath == null)
				throw Response.badRequest("No repository specified");
			users = result.data.stream().filter(user -> accessService.canReviewIn(user, repositoryPath)).toList();
			break;
		default:
			break;
		}
		return Response.ok(users.stream().map(Users::mapForOthers).toList());
	}

	@GetMapping("{username}")
	public Map<String, Object> get(@PathVariable("username") String username) {
		var user = service.getForUsername(username);
		if (user == null)
			throw Response.notFound();
		var currentUser = service.getCurrentUser();
		var userMap = currentUser.username.equals(username) || currentUser.isUserManager()
				? Users.mapForSelf(user)
				: Users.mapForOthers(user);
		if (user.isUserManager()) {
			userMap.put("lastAdmin", service.isLastAdmin(user));
		}
		return userMap;
	}

	@GetMapping("avatar/{username}")
	public byte[] getAvatar(@PathVariable("username") String username) {
		if ("null".equals(username) || username == null)
			return Avatar.get("avatar-user.png");
		var user = service.getForUsername(username);
		if (user == null)
			return Avatar.get("avatar-user.png");
		return Avatar.get(user.avatar, "avatar-user.png");
	}

	@GetMapping("twoFactorAuth/{username}")
	public Map<String, Object> showTwoFactorAuthentication(@PathVariable("username") String username) {
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		var response = new HashMap<String, Object>();
		String servername = settingsService.get(ServerSetting.SERVER_NAME);
		response.put("url", service.getTwoFactorUrl(user, servername));
		response.put("key", user.twoFactorSecret);
		response.put("enabled", true);
		return response;
	}

	@PutMapping("{username}")
	public Map<String, Object> update(
			@PathVariable("username") String username,
			@RequestBody User user) {
		var fromDb = authorizedGetUser(username);
		if (fromDb == null)
			throw Response.notFound();
		if (Strings.nullOrEmpty(user.name))
			throw Response.badRequest("name", "Missing input: Name");
		if (Strings.nullOrEmpty(user.email))
			throw Response.badRequest("email", "Missing input: Email");
		var userWithSameMail = service.getForEmail(user.email);
		if (userWithSameMail != null && !userWithSameMail.username.equals(username))
			throw Response.badRequest("email", "Email is already in use");
		fromDb.name = user.name;
		fromDb.email = user.email;
		var currentUser = service.getCurrentUser();
		if (currentUser.isAdmin()) {
			fromDb.settings.admin = user.settings.admin;
		}
		if (currentUser.isUserManager()) {
			fromDb.settings.userManager = user.settings.userManager;
			fromDb.settings.dataManager = user.settings.dataManager;
			fromDb.settings.canCreateGroups = user.settings.canCreateGroups;
			fromDb.settings.canCreateRepositories = user.settings.canCreateRepositories;
			fromDb.settings.noOfRepositories = user.settings.noOfRepositories;
			fromDb.settings.maxSize = user.settings.maxSize;
			fromDb.settings.activeUntil = user.settings.activeUntil;
		}
		fromDb = service.update(fromDb);
		return Users.mapForSelf(fromDb);
	}

	@PutMapping("avatar/{username}")
	public byte[] setAvatar(
			@PathVariable("username") String username,
			@RequestParam(name = "file", required = false) byte[] file) {
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		user.avatar = file;
		user = service.update(user);
		return getAvatar(username);
	}

	@PutMapping("setpassword/{username}")
	public void setPassword(
			@PathVariable("username") String username,
			@RequestBody Map<String, Object> passwords) {
		var map = ObjectMap.fromMap(passwords);
		var password = map.getString("password");
		var password2 = map.getString("password2");
		if (Strings.nullOrEmpty(password))
			throw Response.badRequest("password", "Missing input: Password");
		if (!Password.isValid(password)) {
			String passwordMessage = "Password must consist of at least 8 characters and must contain at least 1 digit, 2 different lowercase letters and 2 different uppercase letters";
			throw Response.badRequest("password", passwordMessage);
		}
		if (!password.equals(password2))
			throw Response.badRequest("password2", "Passwords are not equal");
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		service.setPassword(user, password);
		service.update(user);
	}

	@PutMapping("twoFactorAuth/{username}/{enable}")
	public Map<String, Object> toggleTwoFactorAuthentication(
			@PathVariable("username") String username,
			@PathVariable("enable") boolean enable) {
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		if (!enable) {
			user.twoFactorSecret = null;
			user = service.update(user);
			return new HashMap<>();
		}
		String servername = settingsService.get(ServerSetting.SERVER_NAME);
		var url = service.enableTwoFactorAuthentication(user, servername);
		var response = new HashMap<String, Object>();
		response.put("url", url);
		response.put("key", user.twoFactorSecret);
		response.put("enabled", true);
		return response;
	}

	private User authorizedGetUser(String username) {
		var user = service.getCurrentUser();
		if (!Strings.nullOrEmpty(username) && !username.equals(user.username)) {
			if (!user.isUserManager())
				throw Response.unauthorized("Not authorized to manage users");
			user = service.getForUsername(username);
		}
		return user;
	}

}
