package com.greendelta.cloud.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.service.LibraryService;

@Path("library")
public class LibraryResource {

	private final LibraryService service;

	@Inject
	public LibraryResource(LibraryService service) {
		this.service = service;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAgainstLibraries(List<String> refIds) {
		Map<String, String> refIdToLibrary = new HashMap<>();
		for (String refId : refIds) {
			String library = service.getLibraryName(refId);
			if (library != null)
				refIdToLibrary.put(refId, library);
		}
		if (refIdToLibrary.isEmpty())
			return Respond.noContent();
		return Respond.ok(refIdToLibrary);
	}

}
