package com.greendelta.collaboration.webservice.user;

import java.io.InputStream;
import java.util.ArrayList;
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

import org.openlca.cloud.model.data.Commit;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.AccessService;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.NotificationService;
import com.greendelta.collaboration.service.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.Names;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Module;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Repositories;
import com.greendelta.search.wrapper.SearchResult;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.multipart.FormDataParam;

@Path("repository")
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryResource {

	private final RepositoryService service;
	private final GroupService groupService;
	private final AccessService accessService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;

	@Inject
	public RepositoryResource(RepositoryService service, GroupService groupService, AccessService accessService,
			HistoryService historyService, SearchService searchService, DeleteService deleteService,
			NotificationService notificationService) {
		this.service = service;
		this.groupService = groupService;
		this.accessService = accessService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
	}

	@POST
	@Path("{group}/{name}")
	public Response create(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Response response = _create(group, name);
		if (response.getStatus() != Status.CREATED.getStatusCode())
			return response;
		Repository repo = service.get(group, name);
		notificationService.repositoryCreated(repo).send();
		return response;
	}

	private Response _create(String group, String name) {
		if (Strings.isNullOrEmpty(group))
			return Respond.invalid("group", "Missing input: Group");
		if (Strings.isNullOrEmpty(name))
			return Respond.invalid("name", "Missing input: Name");
		if (!Names.isValid(name))
			return Respond.invalid("name",
					"Name must consist of at least 4 characters and can only contain characters, numbers and _");
		if (Names.isReserved(name))
			return Respond.invalid("name", "This is a reserved word");
		if (service.exists(group, name))
			return Respond.conflict("Repository " + name + " already exists");
		if (!groupService.exists(group))
			return Respond.invalid("group", "Specified group does not exist");
		Repository repo = service.create(group, name);
		return Respond.created(Repositories.map(repo, groupService.isUserNamespace(group)));
	}

	@POST
	@Path("move/{group}/{name}/{newGroup}/{newName}")
	public Response move(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("newGroup") String newGroup,
			@PathParam("newName") String newName) {
		if (!group.equals(newGroup))
			if (!groupService.exists(newGroup))
				return Respond.invalid("newGroup", "Specified group does not exist");
		if (service.exists(newGroup, newName))
			return Respond.conflict("Specified repository does already exist");
		Repository repo = service.get(group, name);
		if (!service.move(repo, newGroup, newName))
			return Respond.error("Repository could not be moved");
		Repository newRepo = service.get(newGroup, newName);
		updateRepoId(repo, newRepo);
		notificationService.repositoryMoved(repo, newRepo).send();
		return Respond.ok(newRepo);
	}

	private void updateRepoId(Repository oldRepo, Repository newRepo) {
		List<IndexEntry> entries = searchService.getAll(oldRepo);
		searchService.remove(entries);
		for (IndexEntry entry : entries) {
			entry.repositoryId = newRepo.toId();
		}
		searchService.index(newRepo.toId(), entries);
	}

	@DELETE
	@Path("{group}/{name}")
	public Response delete(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		NotificationJob notification = notificationService.repositoryDeleted(repo);
		deleteService.delete(repo);
		notification.send();
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("settings/{group}/{name}/{setting}/{value}")
	public Response toggleSetting(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("setting") String setting,
			@PathParam("value") String value) {
		Repository repo = service.get(group, name);
		service.setSetting(repo, setting, value);
		return Respond.ok(new HashMap<>());
	}

	@GET
	@Path("{group}/{name}")
	public Response get(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		Map<String, Object> mappedRepo = Repositories.map(repo, groupService.isUserNamespace(group));
		String id = repo.toId();
		mappedRepo.put("userCanDelete", accessService.canDelete(id));
		mappedRepo.put("userCanWrite", accessService.canWrite(id));
		mappedRepo.put("userCanMove", accessService.canMove(id));
		mappedRepo.put("userCanClone", accessService.canWrite(repo.group));
		mappedRepo.put("userCanEditMembers", accessService.canEditMembersOf(id));
		mappedRepo.put("userCanSetSettings", accessService.canSetSettings(id));
		return Respond.ok(mappedRepo);
	}

	@GET
	@Path("avatar/{group}/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		byte[] avatar = service.getAvatar(group, name);
		return Respond.ok(avatar, "avatar-repository.png");
	}

	@GET
	@Path("meta/{group}/{name}")
	public Response getMeta(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		return Respond.ok("{\"schemaVersion\": \"" + repo.getSchemaVersion() + "\"}");
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("group") @DefaultValue("") String group,
			@QueryParam("module") Module module) {
		SearchResult<Repository> result = service.getAll(page, pageSize, filter, true);
		if (module == null)
			return Respond.ok(SearchResults.convert(result, Repositories::map));
		List<Repository> repositories = result.data;
		switch (module) {
		case REVIEW:
			repositories = Collections.filter(repositories, (repo) -> !accessService.canManageTaskIn(repo.toId()));
		default:
			break;
		}
		return Respond.ok(Client.map(repositories, Repositories::map));
	}

	@POST
	@Path("clone/{group}/{name}/{commitId}/{newGroup}/{newName}")
	public Response clone(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId,
			@PathParam("newGroup") String newGroup,
			@PathParam("newName") String newName) {
		Response response = _create(newGroup, newName);
		if (response.getStatus() != Status.CREATED.getStatusCode())
			return response;
		Repository from = service.get(group, name);
		Repository to = service.get(newGroup, newName);
		List<Commit> commits = historyService.getCommitsUntil(from, commitId);
		if (!service.clone(from, to, commits)) {
			deleteService.delete(to);
			return Respond.error("Unexpected error during cloning");
		}
		List<IndexEntry> entries = searchService.getAll(from);
		List<IndexEntry> cloned = new ArrayList<>();
		for (IndexEntry entry : entries) {
			IndexEntry clone = entry.clone();
			clone.repositoryId = to.toId();
			cloned.add(clone);
		}
		searchService.index(to.toId(), cloned);
		return response;
	}

	@PUT
	@Path("avatar/{group}/{name}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAvatar(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@FormDataParam("file") InputStream file) {
		service.get(group, name); // to ensure repo exists and user has access
		service.setAvatar(group, name, file);
		return getAvatar(group, name);
	}

}
