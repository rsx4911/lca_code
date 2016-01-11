package com.greendelta.cloud.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.service.BrowseService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private BrowseService service;
	private RepositoryService repoService;

	@Inject
	public BrowseResource(BrowseService service, RepositoryService repoService) {
		this.service = service;
		this.repoService = repoService;
	}

	@GET
	@Path("{group}/{name}")
	public Response getRootContent(@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = repoService.get(group, name);
		return Respond.ok(service.getRootContent(repo));
	}

	@GET
	@Path("{group}/{name}/{categoryRefId}")
	public Response getCategoryContent(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("categoryRefId") String categoryRefId) {
		Repository repo = repoService.get(group, name);
		for (ModelType type : ModelType.values())
			if (type.name().equals(categoryRefId))
				return Respond.ok(service.getCategoryContent(repo, type));
		return Respond.ok(service.getCategoryContent(repo, categoryRefId));
	}

}
