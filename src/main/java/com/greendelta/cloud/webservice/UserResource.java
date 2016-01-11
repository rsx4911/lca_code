package com.greendelta.cloud.webservice;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;

@Path("user")
public class UserResource {

	private static final Logger log = LoggerFactory
			.getLogger(UserResource.class);
	private UserService service;

	@Inject
	public UserResource(UserService service) {
		this.service = service;
	}

	@GET
	@Path("avatar/{username}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound(username);
		if (user.avatar == null)
			return Respond.ok(loadDefaultAvatar());
		return Respond.ok(user.avatar);
	}

	private byte[] loadDefaultAvatar() {
		try {
			return Resources.toByteArray(getClass().getResource(
					"avatar-user.png"));
		} catch (IOException e) {
			log.error("Error loading default avatar", e);
			return null;
		}
	}
}
