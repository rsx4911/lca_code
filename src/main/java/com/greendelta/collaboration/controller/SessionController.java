package com.greendelta.collaboration.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Users;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.JobService;
import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.util.Routes;

@RestController
@RequestMapping("ws/public")
public class SessionController {

	private final static Logger log = LogManager.getLogger(SessionController.class);

	private final UserService userService;
	private final GroupService groupService;
	private final TaskService taskService;
	private final SettingsService settings;
	private final JobService jobService;
	private final NotificationService notificationService;
	private final SessionService sessionService;

	@Autowired
	public SessionController(UserService userService, GroupService groupService, TaskService taskService,
			SettingsService settings, JobService jobService, NotificationService notificationService,
			SessionService sessionService) {
		this.userService = userService;
		this.groupService = groupService;
		this.taskService = taskService;
		this.settings = settings;
		this.jobService = jobService;
		this.notificationService = notificationService;
		this.sessionService = sessionService;
	}

	@GetMapping
	public Map<String, Object> getCurrentUser() {
		if (userService.isAnonymous())
			return Collections.singletonMap("id", 0);
		var user = userService.getCurrentUser();
		var mapped = Users.mapForSelf(user);
		String path = settings.get(ServerSetting.REPOSITORY_PATH);
		mapped.put("noOfTasks", taskService.getAllActiveFor(user).size());
		mapped.put("noOfRepositories", userService.getNoOfRepositories(user, path));
		return mapped;
	}

	// TODO deactivation and two factor auth via spring security directly?
	@PostMapping("login")
	public String login(
			@RequestBody Map<String, Object> form,
			@Autowired HttpServletRequest request) {
		var username = Maps.getString(form, "username");
		var password = Maps.getString(form, "password");
		log.info("User {} attempts to login", username);
		if (!userService.isAnonymous())
			throw Response.conflict("Already authenticated");
		if (Strings.nullOrEmpty(username))
			throw Response.unauthorized("Invalid credentials");
		if (Strings.nullOrEmpty(password))
			throw Response.unauthorized("Invalid credentials");
		if (userService.getForUsername(username) == null) {
			var user = userService.getForEmail(username);
			if (user == null)
				throw Response.unauthorized("Invalid credentials");
			username = user.username;
		}
		password = Password.getPasswordWithoutToken(password);
		var token = Password.getToken(password, (int) Maps.getLong(form, "token"));
		return login(request, username, password, token);
	}

	@PostMapping("register")
	public void register(
			@RequestBody Map<String, Object> form,
			@Autowired HttpServletRequest request) {
		if (!settings.is(ServerSetting.USER_REGISTRATION_ENABLED))
			throw Response.unavailable("User registration feature not enabled");
		var username = Maps.getString(form, "username");
		var name = Maps.getString(form, "name");
		var email = Maps.getString(form, "email");
		var password = Maps.getString(form, "password");
		var password2 = Maps.getString(form, "password2");
		log.info("User {} attempts to register", username);
		if (!userService.isAnonymous())
			throw Response.badRequest("Already authenticated");
		if (Strings.nullOrEmpty(username))
			throw Response.badRequest("username", "Missing input: Username");
		if (userService.getForUsername(username) != null)
			throw Response.badRequest("username", "Username is already in use");
		if (userService.getForEmail(username) != null)
			throw Response.badRequest("username", "Username is already in use");
		if (!Routes.isValid(username))
			throw Response.badRequest("username",
					"Username must consist of at least 4 characters and can only contain characters, numbers and _");
		if (groupService.exists(username, true)) // user or group exists
			throw Response.badRequest("username", "Name is already in use");
		if (Routes.isReserved(username))
			throw Response.badRequest("username", "This is a reserved word");
		if (Strings.nullOrEmpty(email))
			throw Response.badRequest("email", "Missing input: E-Mail");
		if (userService.getForEmail(email) != null)
			throw Response.badRequest("email", "Email is already in use");
		if (userService.getForUsername(email) != null)
			throw Response.badRequest("email", "Email is already in use");
		if (Strings.nullOrEmpty(name))
			throw Response.badRequest("name", "Missing input: Name");
		if (Strings.nullOrEmpty(password))
			throw Response.badRequest("password", "Missing input: Password");
		var passwordMessage = "Password must consist of at least 8 characters and must contain at least 1 digit, 2 different lowercase letters and 2 different uppercase letters";
		if (!Password.isValid(password))
			throw Response.badRequest("password", passwordMessage);
		if (Strings.nullOrEmpty(password2))
			throw Response.badRequest("password2", "Missing input: Password (repeat)");
		if (!password.equals(password2))
			throw Response.badRequest("password2", "Passwords do not match");
		var user = new User();
		user.username = username;
		user.name = name;
		user.email = email;
		var adminApproval = settings.is(ServerSetting.USER_REGISTRATION_APPROVAL_ENABLED);
		if (adminApproval) {
			var cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Dates.removeTimeInformation(cal);
			user.settings.activeUntil = cal.getTime();
		}
		userService.setPassword(user, password);
		user = userService.insert(user);
		if (adminApproval) {
			notificationService.userRegistered(user).send();
		} else {
			login(request, username, password, null);
		}
		log.info("User {} successfully registered", username);
	}

	private String login(HttpServletRequest request, String username, String password, Integer token) {
		var response = sessionService.login(request, username, password, token);
		if (response.status() == HttpStatus.BAD_REQUEST && response.message().equals("tokenRequired"))
			return response.message();
		if (response.status() != HttpStatus.OK)
			throw Response.status(response.status(), response.message());
		log.info("User {} successfully logged in", username);
		return "";
	}

	@PostMapping("request-password-reset")
	public void requestPasswordReset(@RequestBody Map<String, Object> data) {
		var email = data.get("email").toString();
		jobService.requestPasswordReset(email);
		log.info("Requested password reset for {}", email);
	}

	@PostMapping("run-job")
	public String runJob(@RequestBody Map<String, Object> data) {
		if (data.get("token") == null || data.get("token").toString().isEmpty())
			throw Response.badRequest("Invalid token");
		var token = data.get("token").toString();
		var result = jobService.run(token);
		return result.toString();
	}

}
