package com.greendelta.cloud.webservice;

import static org.openlca.cloud.util.Strings.concat;

import java.io.IOException;
import java.util.HashMap;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.greendelta.cloud.service.PagedResult;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.webservice.mapper.RepositoryMapper;

@Path("repository")
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryResource {

	private static final Logger log = LoggerFactory
			.getLogger(UserResource.class);
	private RepositoryService service;

	@Inject
	public RepositoryResource(RepositoryService service) {
		this.service = service;
	}

	@POST
	@Path("{group}/{name}")
	public Response create(@PathParam("group") String group,
			@PathParam("name") String name) {
		// TODO check access to group
		if (Strings.isNullOrEmpty(group))
			return Respond.invalid("group", "Missing input: Group");
		if (Strings.isNullOrEmpty(name))
			return Respond.invalid("name", "Missing input: Name");
		if (service.exists(group, name)) {
			String message = concat("Repository ", name, " already exists");
			return Respond.conflict(message);
		}
		Repository repo = service.create(group, name);
		return Respond.created(new RepositoryMapper().map(repo));
	}

	@DELETE
	@Path("{group}/{name}")
	public Response delete(@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		service.delete(repo);
		return Respond.ok(new HashMap<>());
	}

	@GET
	public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("filter") @DefaultValue("") String filter) {
		PagedResult<Repository> result = service.getAll(page, filter, true);
		return Respond.ok(result.toClient(new RepositoryMapper()::map));
	}

	@GET
	@Path("avatar/{group}/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("group") String group,
			@PathParam("name") String name) {
		byte[] avatar = service.getAvatar(group, name);
		if (avatar == null)
			return Respond.ok(loadDefaultAvatar());
		return Respond.ok(avatar);
	}

	private byte[] loadDefaultAvatar() {
		try {
			return Resources.toByteArray(getClass().getResource(
					"avatar-repository.png"));
		} catch (IOException e) {
			log.error("Error loading default avatar", e);
			return null;
		}
	}

}
