package com.greendelta.collaboration.webservice.user;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.webservice.Respond;

@Path("search")
public class SearchResource {

	private final SearchService service;

	@Inject
	public SearchResource(SearchService service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("query") @DefaultValue("") String query,
			@QueryParam("page") @DefaultValue("1") int page) {
		return Respond.ok(service.search(query, page));
	}
}
