package com.greendelta.collaboration.webservice.user;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.openlca.cloud.util.WebRequests.WebRequestException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.IndexService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositorySetting;
import com.greendelta.collaboration.service.RepositoryMigrator;
import com.greendelta.collaboration.service.RepositoryMigrator.MigrateResponse;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.BrowseService.BrowseParameter;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.Names;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Module;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Repositories;
import com.greendelta.search.wrapper.SearchResult;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.multipart.FormDataParam;

@Path("repository")
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryResource {

	private final RepositoryService service;
	private final GroupService groupService;
	private final UserService userService;
	private final MembershipService membershipService;
	private final AccessService accessService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final BrowseService browseService;
	private final IndexService indexService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;
	private final LibraryService libraryService;

	@Inject
	public RepositoryResource(RepositoryService service, GroupService groupService, MembershipService membershipService,
			UserService userService, AccessService accessService, HistoryService historyService,
			SearchService searchService, BrowseService browseService, IndexService indexService,
			DeleteService deleteService, NotificationService notificationService, LibraryService libraryService) {
		this.service = service;
		this.groupService = groupService;
		this.userService = userService;
		this.membershipService = membershipService;
		this.accessService = accessService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.browseService = browseService;
		this.indexService = indexService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
		this.libraryService = libraryService;
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("group") @DefaultValue("") String group,
			@QueryParam("onlyPublic") @DefaultValue("false") boolean onlyPublic,
			@QueryParam("module") Module module) {
		SearchResult<Repository> result = service.getAll(page, pageSize, filter, onlyPublic, true);
		if (module == null)
			return Respond.ok(SearchResults.convert(result, Repositories::map));
		User user = userService.getCurrentUser();
		switch (module) {
		case DASHBOARD:
		case GROUP:
			return Respond.ok(SearchResults.convert(result,
					repo -> putRepositoryInfo(Repositories.map(repo), repo, user)));
		case REVIEW:
			return Respond.ok(Client.map(Collections.filter(result.data,
					repo -> !accessService.canManageTaskIn(repo.toId())), Repositories::map));
		default:
			return Respond.ok(Client.map(result.data, Repositories::map));
		}
	}

	private ObjectMap putRepositoryInfo(ObjectMap map, Repository repo, User user) {
		map.put("role", membershipService.getRole(user, repo.toId()));
		map.put("datasets", browseService.getCount(new BrowseParameter(repo)));
		map.put("commits", historyService.getCommits(repo).size());
		map.put("members", membershipService.getMemberships(repo.toId()).size());
		return map;
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
		mappedRepo.put("size", repo.getSize());
		mappedRepo.put("libraryRestrictions", libraryService.getRestrictions(repo));
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
	@Path("export/{group}/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response doExport(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		String filename = repo.toId().replace('/', '-') + ".zip";
		return Respond.ok(filename, 0, service.pack(repo));
	}

	@POST
	@Path("{group}/{name}")
	public Response create(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Response response = checkValid(group, name);
		if (response != null)
			return response;
		Repository repo = service.create(group, name);
		notificationService.repositoryCreated(repo).send();
		return Respond.created(Repositories.map(repo, groupService.isUserNamespace(group)));
	}

	private Response checkValid(String group, String name) {
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
		return null;
	}

	@POST
	@Path("import/{group}/{name}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response doImport(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@FormDataParam("commitMessage") String commitMessage,
			@FormDataParam("file") InputStream input,
			@QueryParam("format") String format) {
		Repository repo = service.get(group, name);
		if (format != null && "json-ld".equals(format.toLowerCase())) {
			if (Strings.isNullOrEmpty(commitMessage))
				return Respond.invalid("commitMessage", "Missing input: Commit message");
			service.importJsonLd(repo, input, commitMessage);
		} else {
			service.unpack(repo, input);
		}
		indexService.index(repo);
		repo = service.get(group, name);
		if (repo.settings.publicAccess) {
			service.setSetting(repo, RepositorySetting.PUBLIC_ACCESS, false);
		}
		return Respond.ok(new HashMap<>());
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
		return Respond.ok(Repositories.map(newRepo, groupService.isUserNamespace(newGroup)));
	}

	@POST
	@Path("clone/{group}/{name}/{commitId}/{newGroup}/{newName}")
	public Response clone(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId,
			@PathParam("newGroup") String newGroup,
			@PathParam("newName") String newName) {
		Response response = checkValid(newGroup, newName);
		if (response != null)
			return response;
		Repository from = service.get(group, name);
		Repository to = service.create(newGroup, newName);
		List<Commit> commits = historyService.getCommitsUntil(from, commitId);
		if (!service.clone(from, to, commits)) {
			deleteService.delete(to);
			return Respond.error("Unexpected error during cloning");
		}
		List<IndexEntry> entries = searchService.getAll(from);
		List<IndexEntry> cloned = new ArrayList<>();
		List<String> commitIds = Collections.convertToList(commits, commit -> commit.id);
		for (IndexEntry entry : entries) {
			if (!commitIds.contains(entry.commitId))
				continue;
			IndexEntry clone = entry.clone();
			clone.repositoryId = to.toId();
			clone.group = to.group;
			cloned.add(clone);
		}
		searchService.index(cloned);
		return response;
	}

	private void updateRepoId(Repository oldRepo, Repository newRepo) {
		Set<String> documentIds = searchService.getDocumentIds(oldRepo);
		Map<String, Object> update = new HashMap<>();
		update.put("repositoryId", newRepo.toId());
		update.put("group", newRepo.group);
		searchService.update(documentIds, update);
	}

	@POST
	@Path("import/external/{group}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response importExternal(
			@PathParam("group") String group,
			@PathParam("name") String name,
			Map<String, Object> data) {
		ObjectMap map = ObjectMap.fromMap(data);
		String url = map.getString("url");
		if (Strings.isNullOrEmpty(url))
			return Respond.invalid("url", "Missing input: Url");
		while (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		String username = map.getString("username");
		if (Strings.isNullOrEmpty(username))
			return Respond.invalid("username", "Missing input: Username");
		String password = map.getString("password");
		if (Strings.isNullOrEmpty(password))
			return Respond.invalid("password", "Missing input: Password");
		Repository repo = service.get(group, name);
		RepositoryMigrator migrator = new RepositoryMigrator(service, indexService);
		try {
			MigrateResponse response = migrator.migrate(url, repo, username, password, map.getInteger("token"));
			if (response == MigrateResponse.TOKEN_REQUIRED) 
				return Respond.invalid("token", "Token required");
		} catch (WebRequestException e) {
			if (e.isConnectException() || e.getCause() instanceof ClientHandlerException)
				return Respond.invalid("url", "Cannot connect to " + map.getString("url"));
			return Respond.badRequest(e.getMessage());
		} catch (Exception e) {
			return Respond.invalid("url", "Cannot connect to " + map.getString("url"));
		}
		return Respond.ok(new HashMap<>());
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

	@PUT
	@Path("settings/{group}/{name}/{setting}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setSetting(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("setting") RepositorySetting setting,
			Map<String, Object> data) {
		Object value = data.get("value");
		Repository repo = service.get(group, name);
		service.setSetting(repo, setting, value);
		if (setting == RepositorySetting.TAGS) {
			Set<String> documentIds = searchService.getDocumentIds(repo);
			List<String> tags = RepositorySetting.TAGS.parse(value);
			if (tags != null && tags.isEmpty()) {
				tags = null;
			}
			Map<String, Object> update = java.util.Collections.singletonMap("tags", tags);
			searchService.update(documentIds, update);
		}
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("restriction/{group}/{name}/{library}/{role}")
	public Response setRestriction(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("library") String library,
			@PathParam("role") Role role) {
		Repository repo = service.get(group, name);
		libraryService.setRestriction(repo, library, role);
		return Respond.ok(new HashMap<>());
	}

	@DELETE
	@Path("restriction/{group}/{name}/{library}")
	public Response removeRestriction(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("library") String library) {
		Repository repo = service.get(group, name);
		libraryService.setRestriction(repo, library, null);
		return Respond.ok(new HashMap<>());
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

}
