package com.greendelta.cloud.webservice;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.openlca.cloud.util.ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.mapper.UserMapper;

@Path("public")
public class SessionResource {

	private final static Logger log = LoggerFactory
			.getLogger(SessionResource.class);

	private final Provider<Subject> subjectProvider;
	private final UserService userService;

	@Inject
	public SessionResource(Provider<Subject> subjectProvider,
			UserService userService) {
		this.subjectProvider = subjectProvider;
		this.userService = userService;
	}

	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
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
		log.info("User {} successfully logged in", username);
		return Respond.ok();
	}

	@POST
	@Path("logout")
	public Response logout() {
		if (!userService.logout())
			return Respond.conflict("Not logged in");
		return Respond.ok();
	}

	@GET
	public Response getCurrentUser() {
		Subject subject = subjectProvider.get();
		if (!subject.isAuthenticated())
			return Respond.conflict("Not logged in");
		return Respond.ok(new UserMapper().mapForSelf(userService.getCurrentUser()));
	}

}
