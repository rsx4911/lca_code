package com.greendelta.collaboration.webservice.user;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.SettingService;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Settings;

@Path("settings")
@Produces(MediaType.APPLICATION_JSON)
public class SettingResource {


	private final SettingService settingService;

	@Inject
	public SettingResource(SettingService settingService) {
		this.settingService = settingService;
	}

	@GET
	public Response getSettings() {
		return Respond.ok(Client.map(settingService.getAll(), Settings::map));
	}

	
}
