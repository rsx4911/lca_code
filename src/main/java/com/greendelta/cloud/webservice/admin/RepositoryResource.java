package com.greendelta.cloud.webservice.admin;

import static org.openlca.cloud.util.Strings.concat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.webservice.Respond;
import com.greendelta.cloud.webservice.mapper.RepositoryMapper;

@Path("admin/repository")
@Produces(MediaType.APPLICATION_JSON)
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
		// TODO check access to group
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

	@GET
	public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("filter") @DefaultValue("") String filter) {
		List<Repository> repos = service.getAll(page, filter, true);
		Map<String, Object> result = new HashMap<>();
		result.put("total", service.getCount(true));
		result.put("data", new RepositoryMapper().map(repos));
		return Respond.ok(result);
	}

}
