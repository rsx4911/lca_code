package com.greendelta.collaboration.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

@Path("public/settings")
@Produces(MediaType.APPLICATION_JSON)
public class SettingsResource {

	private final SettingsService service;
	private final UserService userService;

	@Inject
	public SettingsResource(SettingsService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@GET
	public Response getServerSettings() {
		User user = userService.getCurrentUser();
		boolean isAdmin = user != null && user.isAdmin();
		return Respond.ok(service.serverConfig.toMap(setting -> isAdmin || setting.isPublic()));
	}

}
