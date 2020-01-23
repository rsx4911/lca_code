package com.greendelta.collaboration.webservice;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.job.JobResult;
import com.greendelta.collaboration.service.JobService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.util.Users;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Path("public")
public class SessionResource {

	private final static Logger log = LogManager.getLogger(SessionResource.class);

	private final Provider<Subject> subjectProvider;
	private final UserService userService;
	private final TaskService taskService;
	private final SettingsService settingsService;
	private final JobService jobService;
	private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

	@Inject
	public SessionResource(Provider<Subject> subjectProvider, UserService userService, TaskService taskService,
			SettingsService settingsService, JobService jobService) {
		this.subjectProvider = subjectProvider;
		this.userService = userService;
		this.taskService = taskService;
		this.settingsService = settingsService;
		this.jobService = jobService;
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
		ObjectMap formMap = ObjectMap.fromMap(credentials);
		String username = formMap.getString("username");
		String password = formMap.getString("password");
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
			return Respond.unauthorized("User is deactivated");
		}
		if (!Strings.isNullOrEmpty(user.twoFactorSecret)) {
			Integer token = (int) formMap.getLong("token");
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
		boolean maintenanceMode = settingsService.is(Key.MAINTENANCE_MODE);
		if (maintenanceMode && !user.isAdmin()) {
			subject.logout();
			return Respond.forbidden(settingsService.get(Key.MAINTENANCE_MESSAGE));
		}
		log.info("User {} successfully logged in", username);
		return Respond.ok();
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
