package com.greendelta.collaboration.controller.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openlca.git.model.Commit;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Repositories;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.error.WebRequestException;
import com.greendelta.collaboration.io.RepositoryClient;
import com.greendelta.collaboration.io.RepositoryJsonWriter;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.Routes;
import com.greendelta.collaboration.util.SearchResults;

@RestController("userRepositoryController")
@RequestMapping("ws/repository")
public class RepositoryController {

	private final RepositoryService service;
	private final GroupService groupService;
	private final UserService userService;
	private final MembershipService membershipService;
	private final AccessService accessService;
	private final SearchService searchService;
	private final DeleteService deleteService;
	private final LibraryService libraryService;
	private final NotificationService notificationService;

	@Autowired
	public RepositoryController(RepositoryService service, GroupService groupService,
			MembershipService membershipService, UserService userService, AccessService accessService,
			SearchService searchService, DeleteService deleteService, LibraryService libraryService,
			NotificationService notificationService) {
		this.service = service;
		this.groupService = groupService;
		this.userService = userService;
		this.membershipService = membershipService;
		this.accessService = accessService;
		this.searchService = searchService;
		this.deleteService = deleteService;
		this.libraryService = libraryService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "group", required = false) String group,
			@RequestParam(name = "onlyPublic", defaultValue = "false") boolean onlyPublic,
			@RequestParam(name = "module", required = false) Module module) {
		try (var all = service.getAll(page, pageSize, filter, onlyPublic, true)) {
			if (module == null)
				return Response.ok(SearchResults.convert(all, Repositories::map));
			var user = userService.getCurrentUser();
			switch (module) {
			case DASHBOARD, GROUP:
				return Response.ok(SearchResults.convert(all,
						repo -> putRepositoryInfo(Repositories.map(repo), repo, user)));
			case REVIEW:
				return Response.ok(all.data.stream()
						.filter(repo -> accessService.canManageTaskIn(repo.path()))
						.map(Repositories::map)
						.toList());
			default:
				return Response.ok(all.data.stream().map(Repositories::map).toList());
			}
		}
	}

	private Map<String, Object> putRepositoryInfo(Map<String, Object> map, Repository repo, User user) {
		map.put("role", membershipService.getRole(user, repo.path()));
		map.put("datasets", repo.references().find().count());
		map.put("commits", repo.commits().find().all().size());
		map.put("members", membershipService.getMemberships(repo.path()).size());
		if (user.isDataManager()) {
			var lastCommit = repo.commits().find().latest();
			map.put("lastCommit", lastCommit != null ? lastCommit.timestamp : null);
		}
		return map;
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> get(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			var mappedRepo = Repositories.map(repo, groupService.isUserNamespace(group));
			var path = repo.path();
			mappedRepo.put("userCanDelete", accessService.canDelete(path));
			mappedRepo.put("userCanWrite", accessService.canWrite(path));
			mappedRepo.put("userCanMove", accessService.canMove(path));
			mappedRepo.put("userCanClone", accessService.canMove(path));
			mappedRepo.put("userCanEditMembers", accessService.canEditMembersOf(path));
			mappedRepo.put("userCanSetSettings", accessService.canSetSettings(path));
			mappedRepo.put("userCanCreateChangeLog", accessService.canCreateChangeLog(path));
			mappedRepo.put("size", repo.getSize());
			var restrictions = repo.settings.get(RepositorySetting.LIBRARY_RESTRICTIONS, new HashMap<String, Role>());
			libraryService.getAll().stream()
					.filter(lib -> !restrictions.containsKey(lib.name))
					.forEach(lib -> restrictions.put(lib.name, null));
			mappedRepo.put("libraryRestrictions", restrictions);
			return mappedRepo;
		}
	}

	@GetMapping("avatar/{group}/{name}")
	public byte[] getAvatar(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			return Avatar.get(repo.settings.get(RepositorySetting.AVATAR), "avatar-repository.png");
		}
	}

	@GetMapping("meta/{group}/{name}")
	public Map<String, Object> getMeta(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			return java.util.Collections.singletonMap("schemaVersion", repo.getSchemaVersion());
		}
	}

	@GetMapping("export/{group}/{name}")
	public ResponseEntity<StreamingResponseBody> doExport(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			return Response.ok(repo.toFilename(), 0, service.pack(repo));
		}
	}

	@PostMapping("{group}/{name}")
	public ResponseEntity<Map<String, Object>> create(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		checkValid(group, name);
		try (var repo = service.create(group, name)) {
			notificationService.repositoryCreated(repo).send();
			return Response.created(Repositories.map(repo, groupService.isUserNamespace(group)));
		}
	}

	private void checkValid(String group, String name) {
		if (Strings.nullOrEmpty(group))
			throw Response.badRequest("group", "Missing input: Group");
		if (Strings.nullOrEmpty(name))
			throw Response.badRequest("name", "Missing input: Name");
		if (!Routes.isValid(name))
			throw Response.badRequest("name",
					"Name must consist of at least 4 characters and can only contain characters, numbers and _");
		if (Routes.isReserved(name))
			throw Response.badRequest("name", "This is a reserved word");
		if (service.exists(group, name))
			throw Response.badRequest("name", "Repository " + name + " already exists");
		if (!groupService.exists(group))
			throw Response.badRequest("group", "Specified group does not exist");
	}

	@PostMapping("import/{group}/{name}")
	public void doImport(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam("commitMessage") String commitMessage,
			@RequestParam("file") InputStream input,
			@RequestParam("format") String format) {
		try (var repo = service.get(group, name)) {
			if (format != null && "json-ld".equals(format.toLowerCase())) {
				if (Strings.nullOrEmpty(commitMessage))
					throw Response.badRequest("commitMessage", "Missing input: Commit message");
				service.importJsonLd(repo, input, commitMessage);
			} else {
				service.unpack(repo, input);
			}
			if (repo.settings.is(RepositorySetting.PUBLIC_ACCESS)) {
				repo.settings.set(RepositorySetting.PUBLIC_ACCESS, false);
			}
			searchService.index(repo);
		}
	}

	@PostMapping("move/{group}/{name}/{newGroup}/{newName}")
	public ResponseEntity<Map<String, Object>> move(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("newGroup") String newGroup,
			@PathVariable("newName") String newName) {
		if (!group.equals(newGroup))
			if (!groupService.exists(newGroup))
				throw Response.badRequest("newGroup", "Specified group does not exist");
		if (service.exists(newGroup, newName))
			throw Response.badRequest("newName", "Specified repository does already exist");
		try (var repo = service.get(group, name)) {
			if (!service.move(repo, newGroup, newName))
				throw Response.error("Repository could not be moved");
			try (var newRepo = service.get(newGroup, newName)) {
				searchService.update(repo, newRepo);
				notificationService.repositoryMoved(repo, newRepo).send();
				return Response.ok(Repositories.map(newRepo, groupService.isUserNamespace(newGroup)));
			}
		}
	}

	@PostMapping("clone/{group}/{name}/{commitId}/{newGroup}/{newName}")
	public void clone(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId,
			@PathVariable("newGroup") String newGroup,
			@PathVariable("newName") String newName) {
		checkValid(newGroup, newName);
		try (var from = service.get(group, name);
				var to = service.create(newGroup, newName)) {
			var head = from.commits().head();
			Commit commit = null;
			if (head != null && !head.id.equals(commitId)) {
				commit = from.commits().get(commitId);
			}
			if (!service.clone(from, to, commit)) {
				deleteService.delete(to);
				throw Response.error("Unexpected error during cloning");
			}
			searchService.index(to);
		}
	}

	@PostMapping("import/external/{group}/{name}")
	public void importExternal(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestBody Map<String, Object> map) {
		var url = Maps.getString(map, "url");
		if (Strings.nullOrEmpty(url))
			throw Response.badRequest("url", "Missing input: Url");
		while (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		var username = Maps.getString(map, "username");
		if (Strings.nullOrEmpty(username))
			throw Response.badRequest("username", "Missing input: Username");
		var password = Maps.getString(map, "password");
		if (Strings.nullOrEmpty(password))
			throw Response.badRequest("password", "Missing input: Password");
		try (var repo = service.get(group, name)) {
			var repoId = url.substring(url.lastIndexOf("/") + 1);
			url = url.substring(0, url.lastIndexOf("/"));
			repoId = url.substring(url.lastIndexOf("/") + 1) + '/' + repoId;
			url = url.substring(0, url.lastIndexOf("/")) + "/ws";
			try (var client = new RepositoryClient(url, username, password)) {
				client.exportRepository(repoId, stream -> {
					service.unpack(repo, stream);
					searchService.index(repo);
				});
			}
		} catch (WebRequestException e) {
			throw Response.status(e);
		} catch (Exception e) {
			throw Response.badRequest("url", "Cannot connect to " + Maps.getString(map, "url"));
		}
	}

	@PutMapping("avatar/{group}/{name}")
	public byte[] setAvatar(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam("file") byte[] file) {
		try (var repo = service.get(group, name)) {
			repo.settings.set(RepositorySetting.AVATAR, file);
			return getAvatar(group, name);
		}
	}

	@PutMapping("settings/{group}/{name}/{setting}")
	public void setSetting(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("setting") RepositorySetting setting,
			@RequestBody Map<String, Object> data) {
		var value = data.get("value");
		try (var repo = service.get(group, name)) {
			var updateSearch = false;
			if (setting == RepositorySetting.TAGS) {
				var tags = parseStringList(value);
				if (tags != null && tags.isEmpty()) {
					tags = null;
				}
				value = tags;
				List<String> previous = repo.settings.get(RepositorySetting.TAGS);
				updateSearch = new HashSet<>(tags).equals(new HashSet<>(previous));
			}
			repo.settings.set(setting, value);
			if (RepositorySetting.JSON_FILE_GENERATION.equals(setting)
					&& repo.settings.is(RepositorySetting.PUBLIC_ACCESS)) {
				try {
					handleJsonFileGeneration(repo, Boolean.parseBoolean(value.toString()));
				} catch (IOException e) {
					throw Response.error("Error creating cached json file");
				}
			}
			if (updateSearch) {
				searchService.update(repo);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static List<String> parseStringList(Object value) {
		if (value == null)
			return new ArrayList<>();
		if (value instanceof String[] array)
			return Arrays.asList(array);
		if (value instanceof List)
			return (List<String>) value;
		return new ArrayList<>();
	}

	private void handleJsonFileGeneration(Repository repo, boolean create) throws IOException {
		var file = repo.getCachedJsonFile();
		if (file.exists()) {
			file.delete();
		}
		if (!create)
			return;
		RepositoryJsonWriter.writeCurrentAsync(repo);
	}

	@PutMapping("restriction/{group}/{name}/{library}/{role}")
	public void setRestriction(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("library") String library,
			@PathVariable("role") Role role) {
		try (var repo = service.get(group, name)) {
			service.setRestriction(repo, library, role);
		}
	}

	@DeleteMapping("restriction/{group}/{name}/{library}")
	public void removeRestriction(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("library") String library) {
		try (var repo = service.get(group, name)) {
			service.setRestriction(repo, library, null);
		}
	}

	@DeleteMapping("{group}/{name}")
	public void delete(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			var notification = notificationService.repositoryDeleted(repo);
			searchService.remove(repo);
			deleteService.delete(repo);
			notification.send();
		}
	}

}
