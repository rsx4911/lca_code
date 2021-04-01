package com.greendelta.collaboration.webservice;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.job.JobResult;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.JobService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Names;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.webservice.util.Users;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Path("public")
public class SessionResource {

	private final static Logger log = LogManager.getLogger(SessionResource.class);

	private final Provider<Subject> subjectProvider;
	private final UserService userService;
	private final GroupService groupService;
	private final TaskService taskService;
	private final SettingsService settingsService;
	private final JobService jobService;
	private final NotificationService notificationService;
	private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

	@Inject
	public SessionResource(Provider<Subject> subjectProvider, UserService userService, GroupService groupService,
			TaskService taskService, SettingsService settingsService, JobService jobService,
			NotificationService notificationService) {
		this.subjectProvider = subjectProvider;
		this.userService = userService;
		this.groupService = groupService;
		this.taskService = taskService;
		this.settingsService = settingsService;
		this.jobService = jobService;
		this.notificationService = notificationService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentUser() {
		Subject subject = subjectProvider.get();
		if (!subject.isAuthenticated())
			return Respond.ok(Collections.singletonMap("id", 0));
		User currentUser = userService.getCurrentUser();
		ObjectMap mapped = Users.mapForSelf(currentUser);
		mapped.put("noOfTasks", taskService.getAllActiveFor(currentUser).size());
		mapped.put("noOfRepositories", userService.getNoOfRepositories());
		return Respond.ok(mapped);
	}

	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response login(Map<String, Object> credentials) {
		ObjectMap form = ObjectMap.fromMap(credentials);
		String username = form.getString("username");
		String password = form.getString("password");
		log.info("User {} attempts to login", username);
		Subject subject = subjectProvider.get();
		if (subject.isAuthenticated())
			return Respond.conflict("Already authenticated");
		if (Strings.isNullOrEmpty(username))
			return Respond.unauthorized("Invalid credentials");
		if (Strings.isNullOrEmpty(password))
			return Respond.unauthorized("Invalid credentials");
		try {
			subject.login(new UsernamePasswordToken(username, password));
		} catch (IncorrectCredentialsException | UnknownAccountException e) {
			return Respond.unauthorized("Invalid credentials");
		}
		if (!subject.isAuthenticated())
			return Respond.unauthorized("Unknown error");
		User user = userService.getCurrentUser();
		if (user.isDeactivated()) {
			subject.logout();
			return Respond.unauthorized("User is deactivated or approval is pending");
		}
		if (!Strings.isNullOrEmpty(user.twoFactorSecret)) {
			Integer token = (int) form.getLong("token");
			if (token == null || token == 0) {
				subject.logout();
				return Respond.ok("tokenRequired");
			}
			boolean valid = authenticator.authorize(user.twoFactorSecret, token);
			if (!valid) {
				subject.logout();
				return Respond.unauthorized("Invalid token");
			}
		}
		boolean maintenanceMode = settingsService.is(ServerSetting.MAINTENANCE_MODE);
		if (maintenanceMode && !user.isAdmin()) {
			subject.logout();
			return Respond.forbidden(settingsService.get(ServerSetting.MAINTENANCE_MESSAGE));
		}
		log.info("User {} successfully logged in", username);
		return Respond.ok();
	}

	@POST
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(Map<String, Object> data) {
		if (!settingsService.is(ServerSetting.USER_REGISTRATION_ENABLED))
			return Respond.status(Status.SERVICE_UNAVAILABLE, "User registration feature not enabled");
		ObjectMap form = ObjectMap.fromMap(data);
		String username = form.getString("username");
		String name = form.getString("name");
		String email = form.getString("email");
		String password = form.getString("password");
		String password2 = form.getString("password2");
		log.info("User {} attempts to register", username);
		Subject subject = subjectProvider.get();
		if (subject.isAuthenticated())
			return Respond.conflict(null, "Already authenticated");
		if (Strings.isNullOrEmpty(username))
			return Respond.invalid("username", "Missing input: Username");
		User userWithSameUsername = userService.getForUsername(username);
		if (userWithSameUsername != null)
			return Respond.invalid("username", "Username is already in use");
		if (!Names.isValid(username))
			return Respond.invalid("username",
					"Username must consist of at least 4 characters and can only contain characters, numbers and _");
		if (groupService.exists(username, true)) // user or group exists
			return Respond.invalid("username", "Name is already in use");
		if (Names.isReserved(username))
			return Respond.invalid("username", "This is a reserved word");
		if (Strings.isNullOrEmpty(email))
			return Respond.invalid("email", "Missing input: E-Mail");
		User userWithSameMail = userService.getForEmail(email);
		if (userWithSameMail != null)
			return Respond.invalid("email", "Email is already in use");
		if (Strings.isNullOrEmpty(name))
			return Respond.invalid("name", "Missing input: Name");
		if (Strings.isNullOrEmpty(password))
			return Respond.invalid("password", "Missing input: Password");
		String passwordMessage = "Password must consist of at least 8 characters and must contain at least 1 digit, 2 different lowercase letters and 2 different uppercase letters";
		if (!Password.isValid(password))
			return Respond.invalid("password", passwordMessage);
		if (Strings.isNullOrEmpty(password2))
			return Respond.invalid("password2", "Missing input: Password (repeat)");
		if (!password.equals(password2))
			return Respond.invalid("password2", "Passwords do not match");
		User user = new User();
		user.username = username;
		user.name = name;
		user.email = email;
		boolean adminApproval = settingsService.is(ServerSetting.USER_REGISTRATION_APPROVAL_ENABLED);
		if (adminApproval) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Dates.removeTimeInformation(cal);
			user.settings.activeUntil = cal.getTime();
		}
		userService.setPassword(user, password);
		user = userService.insert(user);
		if (adminApproval) {
			notificationService.userRegistered(user).send();
		} else {
			try {
				subject.login(new UsernamePasswordToken(username, password));
			} catch (IncorrectCredentialsException | UnknownAccountException e) {
				return Respond.unauthorized("Invalid credentials");
			}
		}
		log.info("User {} successfully registered", username);
		return Respond.ok(new HashMap<>());
	}

	@POST
	@Path("logout")
	public Response logout() {
		User currentUser = userService.getCurrentUser();
		if (!userService.logout())
			return Respond.conflict("Not logged in");
		log.info("User {} logged out", currentUser.username);
		return Respond.ok();
	}

	@POST
	@Path("request-password-reset")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response requestPasswordReset(Map<String, Object> data) {
		String email = data.get("email").toString();
		jobService.requestPasswordReset(email);
		log.info("Requested password reset for {}", email);
		return Respond.ok();
	}

	@POST
	@Path("run-job")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response runJob(Map<String, Object> data) {
		if (data.get("token") == null || data.get("token").toString().isEmpty())
			return Respond.badRequest("Invalid token");
		String token = data.get("token").toString();
		JobResult result = jobService.run(token);
		return Respond.ok(result.toString());
	}

}
