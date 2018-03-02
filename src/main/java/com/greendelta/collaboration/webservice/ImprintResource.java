package com.greendelta.collaboration.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.Imprint;
import com.greendelta.collaboration.util.ObjectMap;

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
		Imprint imprint = settingsService.getImprint();
		if (imprint == null)
			return Respond.ok(new HashMap<>());
		return Respond.ok(ObjectMap.fromObject(imprint));
	}

}
