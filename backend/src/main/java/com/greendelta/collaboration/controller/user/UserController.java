package com.greendelta.collaboration.controller.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.engine.jdbc.BlobProxy;
import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Users;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.MessagingService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.Password;

@RestController
@RequestMapping("ws/user")
public class UserController {

	private final UserService service;
	private final TeamService teamService;
	private final PermissionsService permissions;
	private final MembershipService membershipService;
	private final MessagingService messagingService;
	private final SettingsService settings;

	public UserController(UserService service, TeamService teamService, PermissionsService permissions,
			MembershipService membershipService, MessagingService messagingService, SettingsService settings) {
		this.service = service;
		this.teamService = teamService;
		this.permissions = permissions;
		this.membershipService = membershipService;
		this.messagingService = messagingService;
		this.settings = settings;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(required = false) Module module,
			@RequestParam(required = false) String repositoryPath,
			@RequestParam(required = false) String filter) {
		var users = getUsers(module, repositoryPath).stream();
		if (!Strings.nullOrEmpty(filter)) {
			users = users.filter(u -> u.name.contains(filter));
		}
		return Response.ok(users.map(Users::mapForOthers).toList());
	}

	private List<User> getVisible(String repositoryPath) {
		var currentUser = service.getCurrentUser();
		if (currentUser.isAnonymous())
			return new ArrayList<>();
		if (currentUser.isUserManager() || currentUser.isDataManager())
			return service.getAll(0, 0, null).data;
		var teams = teamService.getTeamsFor(currentUser);
		var memberships = getMemberships(currentUser, teams, repositoryPath);
		if (!Strings.nullOrEmpty(repositoryPath) && repositoryPath.contains("/")) {
			var group = repositoryPath.substring(0, repositoryPath.indexOf("/"));
			var groupMembers = getMemberships(currentUser, teams, group);
			memberships = join(Arrays.asList(memberships, groupMembers));
		}
		var fromTeams = join(teams.stream()
				.map(team -> team.users)
				.toList());
		var fromConversations = messagingService.getConversations(currentUser).stream()
				.map(c -> c.getOtherUser(currentUser))
				.toList();
		return join(Arrays.asList(fromTeams, getMembers(memberships), fromConversations)).stream()
				.distinct().toList();
	}

	private List<Membership> getMemberships(User user, List<Team> teams, String repoOrGroup) {
		var memberships = new ArrayList<Membership>();
		memberships.addAll(membershipService.getMemberships(user, repoOrGroup));
		for (var team : teams) {
			memberships.addAll(membershipService.getMemberships(team, repoOrGroup));
		}
		return memberships;
	}

	private List<User> getMembers(List<Membership> memberships) {
		var members = new ArrayList<User>();
		for (var membership : memberships) {
			for (var m : membershipService.getMemberships(membership.memberOf)) {
				if (m.user != null) {
					members.add(m.user);
				}
				if (m.team != null) {
					members.addAll(m.team.users);
				}
			}
		}
		return members;
	}

	private <T> List<T> join(List<List<T>> lists) {
		var list = new ArrayList<T>();
		lists.forEach(list::addAll);
		return list;
	}

	private List<User> getUsers(Module module, String repositoryPath) {
		if (module == Module.REVIEW && Strings.nullOrEmpty(repositoryPath))
			return new ArrayList<>();
		var users = getVisible(repositoryPath);
		if (module == null)
			return users;
		return switch (module) {
			case MESSAGING -> users.stream()
					.filter(messagingService::canMessage)
					.toList();
			case REVIEW -> users.stream()
					.filter(user -> permissions.canReviewIn(user, repositoryPath))
					.toList();
			default -> users;
		};
	}

	@GetMapping("{username}")
	public Map<String, Object> get(@PathVariable String username) {
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
	public byte[] getAvatar(@PathVariable String username) {
		if ("null".equals(username) || username == null)
			return Avatar.get("avatar-user.png");
		var user = service.getForUsername(username);
		if (user == null)
			return Avatar.get("avatar-user.png");
		return Avatar.get(user.avatar, "avatar-user.png");
	}

	@GetMapping("twoFactorAuth/{username}")
	public Map<String, Object> showTwoFactorAuthentication(@PathVariable String username) {
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		var response = new HashMap<String, Object>();
		String servername = settings.get(ServerSetting.SERVER_NAME);
		response.put("url", service.getTwoFactorUrl(user, servername));
		response.put("key", user.twoFactorSecret);
		response.put("enabled", true);
		return response;
	}

	@PutMapping("{username}")
	public Map<String, Object> update(
			@PathVariable String username,
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
		userWithSameMail = service.getForUsername(user.email);
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
			if (currentUser.isDataManager()) {
				fromDb.settings.dataManager = user.settings.dataManager;
			}
			if (currentUser.isLibraryManager()) {
				fromDb.settings.libraryManager = user.settings.libraryManager;
			}
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
			@PathVariable String username,
			@RequestParam(required = false) MultipartFile file) {
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		try {
			user.avatar = file != null ? BlobProxy.generateProxy(file.getBytes()) : null;
			user = service.update(user);
		} catch (IOException e) {
			throw Response.error("Error reading avatar file");
		}
		return getAvatar(username);
	}

	@PutMapping("setpassword/{username}")
	public void setPassword(
			@PathVariable String username,
			@RequestBody Map<String, Object> map) {
		var password = Maps.getString(map, "password");
		var password2 = Maps.getString(map, "password2");
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
			@PathVariable String username,
			@PathVariable boolean enable) {
		var user = authorizedGetUser(username);
		if (user == null)
			throw Response.notFound();
		if (!enable) {
			user.twoFactorSecret = null;
			user = service.update(user);
			return new HashMap<>();
		}
		String servername = settings.get(ServerSetting.SERVER_NAME);
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
