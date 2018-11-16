package com.greendelta.collaboration.webservice.datamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.webservice.Respond;

@Path("datamanager/library")
public class LibraryResource {

	private final LibraryService service;

	@Inject
	public LibraryResource(LibraryService service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLibraries() {
		List<Map<String, Object>> libraries = new ArrayList<>();
		for (String name : service.getLibraryNames()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", name);
			map.put("count", service.getRefIds(name).size());
			libraries.add(map);
		}
		return Respond.ok(libraries);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{name}")
	public Response getLibraryRefIds(@PathParam("name") String name) {
		return Respond.ok(service.getRefIds(name));
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{name}")
	public Response putLibrary(@PathParam("name") String name, List<String> refIds) {
		service.putLibrary(name, refIds);
		return Respond.ok(new HashMap<>());
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{name}")
	public Response removeLibrary(@PathParam("name") String name) {
		service.removeLibrary(name);
		return Respond.ok(new HashMap<>());
	}

}
