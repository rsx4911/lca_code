package com.greendelta.collaboration.webservice.user;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Settings;

@Path("settings")
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
	public Response getSettings() {
		List<Setting> settings = service.getAll();
		User user = userService.getCurrentUser();
		if (!user.isAdmin()) {
			settings = Collections.filter(settings, (setting) -> !setting.name.isPublic());
		}
		return Respond.ok(Client.map(settings, Settings::map));
	}

}
