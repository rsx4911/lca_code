package com.greendelta.cloud.webservice;

import static org.openlca.cloud.util.Strings.concat;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("repository")
public class RepositoryResource {

	private RepositoryService service;

	@Inject
	public RepositoryResource(RepositoryService service) {
		this.service = service;
	}

	@POST
	@Path("create/{group}/{name}")
	public Response create(@PathParam("group") String group,
			@PathParam("name") String name) {
		if (service.exists(group, name)) {
			String message = concat("Repository ", name, " already exists");
			return Respond.conflict(message);
		}
		service.create(group, name);
		return Respond.created();
	}

	@DELETE
	@Path("delete/{group}/{name}")
	public Response delete(@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		service.delete(repo);
		return Respond.ok();
	}

}
