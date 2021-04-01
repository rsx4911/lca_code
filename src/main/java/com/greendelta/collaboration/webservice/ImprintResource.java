package com.greendelta.collaboration.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.SettingsService;

@Path("public/imprint")
public class ImprintResource {

	private final SettingsService settingsService;

	@Inject
	public ImprintResource(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		return Respond.ok(settingsService.imprint.toMap());
	}

}
