package com.greendelta.collaboration.webservice;

import java.util.Collections;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.UserService;

@Path("public/error")
public class ClientErrorResource {

	private final Logger log = LogManager.getLogger(ClientErrorResource.class);
	private final UserService userService;

	@Inject
	public ClientErrorResource(UserService userService) {
		this.userService = userService;
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response handleClientError(String stacktrace) {
		log.error("Client error [user=" + getUserInfo() + "]", createException(stacktrace));
		return Respond.ok(Collections.singletonMap("logged", true));
	}

	private String getUserInfo() {
		User user = userService.getCurrentUser();
		if (user == null)
			return "anonymous";
		String info = "{";
		info += "id: " + user.getId();
		info += ", name: " + user.username;
		info += ", email: " + user.email;
		info += "}";
		return info;
	}

	private Throwable createException(String error) {
		String[] parts = null;
		if (error.contains("\t"))
			parts = error.split("\tat ");
		else
			parts = error.split(" at ");
		Throwable t = new Throwable(parts[0].trim());
		StackTraceElement[] stackTrace = new StackTraceElement[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			String clazz = "";
			String method = "";
			String ref = null;
			String part = parts[i].trim();
			if (part.contains("(")) {
				String clazzAndMethod = part.substring(0, part.indexOf("(")).trim();
				if (clazzAndMethod.contains(".")) {
					clazz = clazzAndMethod.substring(0, clazzAndMethod.lastIndexOf(".")).trim();
					method = clazzAndMethod.substring(clazzAndMethod.lastIndexOf(".") + 1).trim();
				} else {
					method = clazzAndMethod;
				}
				ref = part.substring(part.indexOf("(") + 1).trim();
			} else {
				ref = part.trim();
			}
			if (ref.indexOf(':') != -1) {
				ref = ref.substring(0, ref.lastIndexOf(":"));
			}
			int index = ref.lastIndexOf(":");
			int line = 0;
			String file = null;
			if (index != -1) {
				file = ref.substring(0, index);
				String linePart = ref.substring(index + 1);
				line = Integer.parseInt(linePart);
			}
			stackTrace[i - 1] = new StackTraceElement(clazz, method, file, line);
		}
		t.setStackTrace(stackTrace);
		return t;
	}

}
