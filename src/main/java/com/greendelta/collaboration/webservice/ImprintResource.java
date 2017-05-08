package com.greendelta.collaboration.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.platform.Imprint;
import com.greendelta.collaboration.util.ObjectMap;

@Path("public/imprint")
public class ImprintResource {

	private final Imprint imprint;

	@Inject
	public ImprintResource(Imprint imprint) {
		this.imprint = imprint;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		return Respond.ok(ObjectMap.fromObject(imprint));
	}

}
