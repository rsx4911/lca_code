package com.greendelta.collaboration.webservice;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.UserService;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger log = LogManager.getLogger(GenericExceptionMapper.class);

	@Inject
	private UserService userService;

	@Override
	public Response toResponse(Throwable e) {
		if (e instanceof WebApplicationException)
			return ((WebApplicationException) e).getResponse();
		if (e instanceof UnsupportedSchemaException)
			return Response.status(Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
		if (e instanceof AuthorizationException)
			return Response.status(Status.FORBIDDEN).build();
		log.error("Server error [user=" + getUserInfo() + "]", e);
		return Response.status(getStatus(e)).entity(getMessage(e)).type(MediaType.APPLICATION_JSON).build();
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

	private int getStatus(Throwable e) {
		return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}

	private String getMessage(Throwable e) {
		if (e instanceof WebApplicationException)
			return e.getMessage();
		if (!isAdmin())
			return "Server error, please contact your admin";
		StringWriter trace = new StringWriter();
		PrintWriter writer = new PrintWriter(trace);
		e.printStackTrace(writer);
		return trace.toString();
	}

	private boolean isAdmin() {
		User user = userService.getCurrentUser();
		return user != null && user.admin;
	}

}
