package com.greendelta.collaboration.controller.usermanager;

import java.util.Map;

import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Users;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.util.Routes;
import com.greendelta.collaboration.util.SearchResults;

@RestController("usermanagerUserController")
@RequestMapping("ws/usermanager/user")
public class UserController {

	private final UserService service;
	private final GroupService groupService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;

	public UserController(UserService service, GroupService groupService, DeleteService deleteService,
			NotificationService notificationService) {
		this.service = service;
		this.groupService = groupService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) String filter) {
		var result = service.getAll(page, pageSize, filter);
		return Response.ok(SearchResults.convert(result, Users::mapForAdmin));
	}

	@PostMapping("{username}")
	public ResponseEntity<Map<String, Object>> create(
			@PathVariable String username,
			@RequestBody User user) {
		if (Strings.nullOrEmpty(username))
			throw Response.badRequest("username", "Missing input: Username");
		if (!Routes.isValid(username))
			throw Response.badRequest("username",
					"Username must consist of at least 4 characters and can only contain characters, numbers and _");
		if (groupService.exists(username, true)) // user or group exists
			throw Response.badRequest("username", "Name is already in use");
		if (service.getForEmail(user.email) != null)
			throw Response.badRequest("email", "Email is already in use");
		if (Routes.isReserved(username))
			throw Response.badRequest("username", "This is a reserved word");
		if (Strings.nullOrEmpty(user.name))
			throw Response.badRequest("name", "Missing input: Name");
		if (Strings.nullOrEmpty(user.email))
			throw Response.badRequest("email", "Missing input: Email");
		var password = Password.generate();
		service.setPassword(user, password);
		user.settings.setDefaults();
		user = service.insert(user);
		notificationService.userCreated(user, password).send();
		return Response.created(Users.mapForSelf(user));
	}

	@DeleteMapping("{username}")
	public void delete(@PathVariable String username) {
		var user = service.getForUsername(username);
		if (user == null)
			throw Response.notFound();
		var notification = notificationService.userDeleted(user);
		deleteService.delete(user);
		notification.send();
	}

}
