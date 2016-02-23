package com.greendelta.cloud.webservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;
import org.openlca.cloud.util.ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Beans;
import com.greendelta.cloud.webservice.mapper.UserMapper;
import com.sun.jersey.multipart.FormDataParam;

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
	@Path("{username}")
	public Response get(@PathParam("username") String username) {
		User user = service.getForUsername(username);
		if (user == null)
			return Respond.notFound();
		return Respond.ok(new UserMapper().map(user));
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

	@PUT
	@Path("{username}")
	public Response update(@PathParam("username") String username, User user) {
		User fromDb = authorizedGetUser(username);
		if (fromDb == null)
			return Respond.notFound();
		if (Strings.isNullOrEmpty(user.name))
			return Respond.invalid("name", "Missing input: Name");
		if (Strings.isNullOrEmpty(user.email))
			return Respond.invalid("email", "Missing input: Email");
		Beans.populateProperties(user, fromDb, "name", "email");
		fromDb = service.update(fromDb);
		return Respond.ok(new UserMapper().map(fromDb));
	}

	@PUT
	@Path("setpassword/{username}")
	public Response setPassword(@PathParam("username") String username,
			Map<String, Object> passwords) {
		ObjectMap map = ObjectMap.fromMap(passwords);
		String password = map.get("password");
		String password2 = map.get("password2");
		if (Strings.isNullOrEmpty(password))
			return Respond.invalid("password", "Missing input: Password");
		if (!password.equals(password2))
			return Respond.invalid("password2", "Passwords are not equal");
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		service.setPassword(user, password);
		service.update(user);
		return Respond.ok();
	}

	@PUT
	@Path("avatar/{username}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(@PathParam("username") String username, @FormDataParam("file") InputStream file) {
		User user = authorizedGetUser(username);
		if (user == null)
			return Respond.notFound();
		if (file == null)
			user.avatar = null;
		else
			user.avatar = readStream(file);
		user = service.update(user);
		return getAvatar(username);
	}

	private byte[] readStream(InputStream file) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteStreams.copy(file, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}

	private User authorizedGetUser(String username) {
		User user = service.getCurrentUser();
		if (!Strings.isNullOrEmpty(username) && !username.equals(user.username)) {
			if (!user.admin)
				throw new UnauthorizedException("Only admin can change other users");
			user = service.getForUsername(username);
		}
		return user;
	}

}
