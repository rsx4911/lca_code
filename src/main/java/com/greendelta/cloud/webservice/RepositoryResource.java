package com.greendelta.cloud.webservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.AccessService;
import com.greendelta.cloud.service.GroupService;
import com.greendelta.cloud.service.NotificationService;
import com.greendelta.cloud.service.NotificationService.NotificationJob;
import com.greendelta.cloud.service.PagedResult;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Names;
import com.greendelta.cloud.webservice.mapper.RepositoryMapper;
import com.sun.jersey.multipart.FormDataParam;

@Path("repository")
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryResource {

	private final RepositoryService service;
	private final UserService userService;
	private final GroupService groupService;
	private final AccessService accessService;
	private final NotificationService notificationService;

	@Inject
	public RepositoryResource(RepositoryService service, UserService userService, GroupService groupService,
			AccessService accessService, NotificationService notificationService) {
		this.service = service;
		this.userService = userService;
		this.groupService = groupService;
		this.accessService = accessService;
		this.notificationService = notificationService;
	}

	@POST
	@Path("{group}/{name}")
	public Response create(@PathParam("group") String group,
			@PathParam("name") String name) {
		if (Strings.isNullOrEmpty(group))
			return Respond.invalid("group", "Missing input: Group");
		if (Strings.isNullOrEmpty(name))
			return Respond.invalid("name", "Missing input: Name");
		if (Names.isValid(name))
			return Respond.invalid("name",
					"Name must consist of at least 4 characters and can only contain characters, numbers and _");
		if (Names.isReserved(name))
			return Respond.invalid("name", "This is a reserved word");
		if (service.exists(group, name))
			return Respond.conflict("Repository " + name + " already exists");
		if (!groupService.exists(group))
			return Respond.invalid("group", "Specified group does not exist");
		Repository repo = service.create(group, name);
		notificationService.repositoryCreated(repo).send();
		return Respond.created(new RepositoryMapper().map(repo, groupService.isUserNamespace(group)));
	}

	@DELETE
	@Path("{group}/{name}")
	public Response delete(@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		NotificationJob notification = notificationService.repositoryDeleted(repo);
		service.delete(repo);
		notification.send();
		return Respond.ok(new HashMap<>());
	}

	@GET
	@Path("{group}/{name}")
	public Response get(@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		Map<String, Object> mappedRepo = new RepositoryMapper().map(repo, groupService.isUserNamespace(group));
		User currentUser = userService.getCurrentUser();
		mappedRepo.put("userCanDelete", currentUser.admin || accessService.canDelete(currentUser, repo.toId()));
		mappedRepo.put("userCanWrite", currentUser.admin || accessService.canWrite(currentUser, repo.toId()));
		mappedRepo.put("userCanEditMembers",
				currentUser.admin || accessService.canEditMembers(currentUser, repo.toId()));
		return Respond.ok(mappedRepo);
	}

	@GET
	public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("group") @DefaultValue("") String group) {
		PagedResult<Repository> result = service.getAll(page, filter, true);
		return Respond.ok(result.toClient(new RepositoryMapper()::map));
	}

	@PUT
	@Path("avatar/{group}/{name}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(@PathParam("group") String group, @PathParam("name") String name,
			@FormDataParam("file") InputStream file) {
		service.get(group, name); // to ensure repo exists and user has access
		service.setAvatar(group, name, file);
		return getAvatar(group, name);
	}

	@GET
	@Path("avatar/{group}/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("group") String group,
			@PathParam("name") String name) {
		byte[] avatar = service.getAvatar(group, name);
		return Respond.ok(avatar, "avatar-repository.png");
	}

}
