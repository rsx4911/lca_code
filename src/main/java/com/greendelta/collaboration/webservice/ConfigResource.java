package com.greendelta.collaboration.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.greendelta.collaboration.util.Names;

@Path("public/config")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource {

	@GET
	@Path("userRoutes")
	public Response getUserRoutes() {
		return Respond.ok(Names.getUserRoutes());
	}

}
