package com.greendelta.collaboration.webservice;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

import org.openlca.cloud.util.ObjectMap;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.service.AccessService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.NotificationService;
import com.greendelta.collaboration.service.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.PagedResult;
import com.greendelta.collaboration.util.Names;
import com.sun.jersey.multipart.FormDataParam;

@Path("group")
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {

	private final GroupService service;
	private final AccessService accessService;
	private final NotificationService notificationService;

	@Inject
	public GroupResource(GroupService service, AccessService accessService, NotificationService notificationService) {
		this.service = service;
		this.accessService = accessService;
		this.notificationService = notificationService;
	}

	@POST
	@Path("{name}")
	public Response create(@PathParam("name") String name) {
		if (Strings.isNullOrEmpty(name))
			return Respond.invalid("name", "Missing input: Name");
		if (!Names.isValid(name))
			return Respond
					.invalid("name",
							"Name must consist of at least 4 characters and can only contain characters, numbers and underscore");
		if (Names.isReserved(name))
			return Respond.invalid("name", "This is a reserved word");
		if (service.exists(name)) {
			String message = "Group " + name + " already exists";
			return Respond.conflict(message);
		}
		service.create(name);
		notificationService.groupCreated(name).send();
		return Respond.created(Collections.singletonMap("name", name));
	}

	@GET
	@Path("{name}")
	public Response get(@PathParam("name") String name) {
		if (!service.exists(name) || service.isUserNamespace(name))
			return Respond.notFound("Group " + name + " not found");
		Map<String, Object> group = new HashMap<>();
		group.put("userCanDelete", accessService.canDelete(name));
		group.put("userCanWrite", accessService.canWrite(name));
		group.put("userCanEditMembers", accessService.canEditMembersOf(name));
		return Respond.ok(group);
	}

	@GET
	public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("onlyIfCanWrite") @DefaultValue("false") boolean onlyIfCanWrite) {
		PagedResult<String> result = service.getAll(page, filter, true);
		return Respond.ok(result.toClient2((groups) -> {
			List<ObjectMap> maps = new ArrayList<>();
			for (String group : groups) {
				if (onlyIfCanWrite && !accessService.canWrite(group))
					continue;
				maps.add(ObjectMap.fromMap(Collections.singletonMap("name", group)));
			}
			return maps;
		}));
	}

	@DELETE
	@Path("{name}")
	public Response delete(@PathParam("name") String name) {
		NotificationJob notification = notificationService.groupDeleted(name);
		service.delete(name);
		notification.send();
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("avatar/{name}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(@PathParam("name") String name,
			@FormDataParam("file") InputStream file) {
		if (!service.exists(name))
			return Respond.notFound();
		service.setAvatar(name, file);
		return getAvatar(name);
	}

	@GET
	@Path("avatar/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(@PathParam("name") String name) {
		boolean exists = service.exists(name);
		if (!exists)
			return Respond.notFound(name);
		return Respond.ok(service.getAvatar(name), "avatar-group.png");
	}

}
