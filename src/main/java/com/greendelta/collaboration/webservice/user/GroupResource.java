package com.greendelta.collaboration.webservice.user;

import java.io.InputStream;
import java.util.Collections;
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

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.GroupService.GroupSettings;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Names;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Module;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.search.wrapper.SearchResult;
import com.sun.jersey.multipart.FormDataParam;

@Path("group")
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {

	private final GroupService service;
	private final UserService userService;
	private final AccessService accessService;
	private final MembershipService membershipService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;

	@Inject
	public GroupResource(GroupService service, UserService userService, AccessService accessService,
			MembershipService membershipService, DeleteService deleteService, NotificationService notificationService) {
		this.service = service;
		this.userService = userService;
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("module") Module module,
			@QueryParam("onlyIfCanWrite") @DefaultValue("false") boolean onlyIfCanWrite) {
		SearchResult<String> result = service.getAll(page, pageSize, filter, true, onlyIfCanWrite);
		User user = userService.getCurrentUser();
		return Respond.ok(SearchResults.convert(result, group -> {
			ObjectMap map = ObjectMap.fromMap(Collections.singletonMap("name", group));
			GroupSettings settings = service.getSettings(group);
			map.put("settings", settings);
			map.put("label", settings.label != null ? settings.label : group);
			if (module != Module.DASHBOARD)
				return map;
			map.put("role", membershipService.getRole(user, group));
			map.put("repositories", service.getRepositoryCount(group));
			map.put("members", membershipService.getMemberships(group).size());
			return map;
		}));
	}

	@GET
	@Path("{name}")
	public Response get(@PathParam("name") String name) {
		User user = userService.getCurrentUser();
		if (!service.exists(name) || (service.isUserNamespace(name) && (user == null || !name.equals(user.username))))
			return Respond.notFound("Group " + name + " not found");
		if (!accessService.canRead(name))
			throw new UnauthorizedAccessException(name, "READ");
		Map<String, Object> group = new HashMap<>();
		group.put("userCanDelete", accessService.canDelete(name));
		group.put("userCanWrite", accessService.canWrite(name));
		group.put("userCanCreate", accessService.canCreateRepositoryIn(name));
		group.put("userCanSetSettings", accessService.canSetSettings(name));
		group.put("settings", service.getSettings(name));
		boolean isUserspace = user != null && name.equals(user.username);
		group.put("userCanEditMembers", !isUserspace && accessService.canEditMembersOf(name));
		return Respond.ok(group);
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

	@POST
	@Path("{name}")
	public Response create(@PathParam("name") String name) {
		if (Strings.isNullOrEmpty(name))
			return Respond.invalid("name", "Missing input: Name");
		if (!Names.isValid(name))
			return Respond.invalid("name",
					"Name must consist of at least 4 characters and can only contain characters, numbers and underscore");
		if (Names.isReserved(name))
			return Respond.invalid("name", "This is a reserved word");
		if (service.exists(name)) {
			String message = "Group " + name + " already exists";
			return Respond.conflict(message);
		}
		service.create(name, false);
		notificationService.groupCreated(name).send();
		return Respond.created(Collections.singletonMap("name", name));
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

	@PUT
	@Path("settings/{name}/{setting}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setSetting(
			@PathParam("name") String name,
			@PathParam("setting") String setting,
			Map<String, Object> data) {
		String value = data.get("value").toString();
		if (!service.exists(name))
			return Respond.notFound();
		service.setSetting(name, setting, value);
		return Respond.ok(new HashMap<>());
	}
	
	@DELETE
	@Path("{name}")
	public Response delete(@PathParam("name") String name) {
		if (!service.exists(name) || service.isUserNamespace(name))
			return Respond.notFound("Group " + name + " not found");
		NotificationJob notification = notificationService.groupDeleted(name);
		deleteService.deleteGroup(name);
		notification.send();
		return Respond.ok(new HashMap<>());
	}

}
