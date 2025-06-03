package com.greendelta.collaboration.controller.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
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
import org.springframework.web.multipart.MultipartFile;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Repositories;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.RepositoryClient;
import com.greendelta.collaboration.io.ZipCommitWriter;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.IndexService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.Routes;
import com.greendelta.collaboration.util.SearchResults;

@RestController("userRepositoryController")
@RequestMapping("ws/repository")
public class RepositoryController {

	private static final Logger log = LoggerFactory.getLogger(RepositoryController.class);
	private final RepositoryService service;
	private final GroupService groupService;
	private final UserService userService;
	private final MembershipService membershipService;
	private final PermissionsService permissions;
	private final IndexService indexService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;
	private final HistoryService historyService;
	private final SettingsService settings;

	public RepositoryController(RepositoryService service, GroupService groupService,
			MembershipService membershipService, UserService userService, PermissionsService permissions,
			IndexService indexService, DeleteService deleteService, NotificationService notificationService,
			HistoryService historyService, SettingsService settings) {
		this.service = service;
		this.groupService = groupService;
		this.userService = userService;
		this.membershipService = membershipService;
		this.permissions = permissions;
		this.indexService = indexService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
		this.historyService = historyService;
		this.settings = settings;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String group,
			@RequestParam(required = false) Module module) {
		try (var all = service.getAllAccessible()) {
			all.sort();
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, all, Repository::path);
			if (module == null)
				return Response.ok(SearchResults.convert(result, this::map));
			var user = userService.getCurrentUser();
			switch (module) {
				case DASHBOARD, GROUP:
					return Response.ok(SearchResults.convert(result,
							repo -> putRepositoryInfo(this.map(repo), repo, user)));
				case REVIEW:
					return Response.ok(all.stream()
							.filter(repo -> permissions.canManageTaskIn(repo.path()))
							.map(this::map)
							.toList());
				default:
					return Response.ok(all.stream()
							.map(this::map)
							.toList());
			}
		}
	}

	private Map<String, Object> map(Repository repo) {
		return Repositories.mapForList(repo, !historyService.getReleasedCommits(repo).isEmpty());
	}

	private Map<String, Object> putRepositoryInfo(Map<String, Object> map, Repository repo, User user) {
		map.put("role", membershipService.getRole(user, repo.path()));
		map.put("commits", repo.commits.find().all().size());
		map.put("members", membershipService.getMemberships(repo.path()).size());
		if (!user.isAnonymous()) {
			var lastCommit = repo.commits.find().latest();
			map.put("lastCommit", lastCommit != null ? lastCommit.timestamp : null);
		}
		return map;
	}

	@GetMapping("count/{group}/{name}")
	public ResponseEntity<?> getReferenceCount(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			return Response.ok(Map.of("datasets", repo.references.find().count()));
		}
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> get(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			var mappedRepo = Repositories.mapForUser(repo, groupService.isUserNamespace(group));
			var path = repo.path();
			mappedRepo.put("userCanDelete", permissions.canDelete(path));
			mappedRepo.put("userCanWrite", permissions.canWriteTo(path));
			mappedRepo.put("userCanMove", permissions.canMove(path));
			mappedRepo.put("userCanClone", permissions.canMove(path));
			mappedRepo.put("userCanEditMembers", permissions.canEditMembersOf(path));
			mappedRepo.put("userCanSetSettings", permissions.canSetSettingsOf(path));
			mappedRepo.put("userCanCreateChangeLog", permissions.canCreateChangeLogOf(path));
			mappedRepo.put("userCanCreateReleases", permissions.canCreateReleasesIn(path));
			mappedRepo.put("size", repo.getSize());
			var latestRelease = historyService.getLatestReleasedCommit(repo);
			var modelTypes = repo.getModelTypes(latestRelease);
			List<String> typeOrder = settings.get(ServerSetting.MODEL_TYPES_ORDER);
			mappedRepo.put("modelTypes", typeOrder.stream()
					.map(ModelType::valueOf)
					.filter(modelTypes::contains)
					.toList());
			return mappedRepo;
		}
	}

	@GetMapping("avatar/{group}/{name}")
	public byte[] getAvatar(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			byte[] avatar = repo.settings.get(RepositorySetting.AVATAR);
			if (avatar != null)
				return avatar;
			return Avatar.get("avatar-repository.png");
		}
	}

	@GetMapping("export/{group}/{name}")
	public ResponseEntity<Resource> doExport(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			return Response.ok(repo.toFilename(), service.pack(repo));
		} catch (IOException e) {
			throw Response.error("Could not export repository, an unexpected error occured");
		}
	}

	@PostMapping("{group}/{name}")
	public ResponseEntity<Map<String, Object>> create(
			@PathVariable String group,
			@PathVariable String name) {
		checkValid(group, name);
		try (var repo = service.create(group, name)) {
			if (repo == null)
				throw Response.error(
						"Could not create repository, does the configured 'Repositories root directory' exist and can be write-accessed?");
			notificationService.repositoryCreated(repo).send();
			return Response.created(Repositories.mapForUser(repo, groupService.isUserNamespace(group)));
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
		if (!groupService.exists(group)) {
			var user = userService.getCurrentUser();
			if (!group.equals(user.username))
				throw Response.badRequest("group", "Specified group does not exist");
			groupService.create(user.username, true);
		}
	}

	@PostMapping("import/{group}/{name}")
	public void doImport(
			@PathVariable String group,
			@PathVariable String name,
			@RequestParam MultipartFile file,
			@RequestParam(defaultValue = "repository") String format,
			@RequestParam(required = false) String commitMessage) {
		try (var repo = service.get(group, name)) {
			if (format != null && "json-ld".equals(format.toLowerCase())) {
				importJsonLd(repo, file.getInputStream(), commitMessage);
			} else {
				service.unpack(repo, file.getInputStream());
			}
			indexService.indexPrivateAsync(RepositoryPath.of(group, name), null, repo.commits.head());
		} catch (IOException e) {
			log.error("Error getting input stream from multipart file", e);
		}
	}

	private void importJsonLd(Repository repo, InputStream input, String commitMessage) {
		if (Strings.nullOrEmpty(commitMessage))
			throw Response.badRequest("commitMessage", "Missing input: Commit message");
		var user = userService.getCurrentUser();
		try {
			var success = ZipCommitWriter.write(input, repo, user, commitMessage);
			if (!success)
				throw Response.badRequest("data",
						"Incompatible schema version: Are you trying to import JSON-LD from openLCA 1.x?");
		} catch (IOException e) {
			log.error("Error converting json to repository", e);
			throw Response.badRequest("data",
					"Incompatible data: The data you provided is not a JSON-LD 2.0 zip file");
		}
	}

	@PostMapping("move/{group}/{name}/{newGroup}/{newName}")
	public ResponseEntity<Map<String, Object>> move(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String newGroup,
			@PathVariable String newName) {
		checkValid(newGroup, newName);
		try (var repo = service.get(group, name)) {
			if (!service.move(repo, newGroup, newName))
				throw Response.error("Repository could not be moved");
			var newRepo = service.get(newGroup, newName);
			notificationService.repositoryMoved(repo, newRepo).send();
			var result = Repositories.mapForUser(newRepo, groupService.isUserNamespace(newGroup));
			indexService.moveIndexAsync(RepositoryPath.of(group, name), RepositoryPath.of(newGroup, newName));
			return Response.ok(result);
		}
	}

	@PostMapping("clone/{group}/{name}/{commitId}/{newGroup}/{newName}")
	public void clone(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId,
			@PathVariable String newGroup,
			@PathVariable String newName) {
		checkValid(newGroup, newName);
		try (var from = service.get(group, name);
				var to = service.create(newGroup, newName)) {
			if (to == null)
				throw Response.error(
						"Could not create repository, does the configured 'Repositories root directory' exist and can be write-accessed?");
			var head = from.commits.head();
			Commit commit = null;
			if (head != null && !head.id.equals(commitId)) {
				commit = from.commits.get(commitId);
			}
			if (!service.clone(from, to, commit)) {
				deleteService.delete(to);
				throw Response.error("Unexpected error during cloning");
			}
			indexService.indexPrivateAsync(RepositoryPath.of(newGroup, newName), null, to.commits.head());
		}
	}

	@PostMapping("import/external/{group}/{name}")
	public void importExternal(
			@PathVariable String group,
			@PathVariable String name,
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
					indexService.indexPrivateAsync(RepositoryPath.of(group, name), null, repo.commits.head());
				});
			}
		} catch (IOException e) {
			throw Response.error(e.getMessage());
		} catch (Exception e) {
			throw Response.badRequest("url", "Cannot connect to " + Maps.getString(map, "url"));
		}
	}

	@PutMapping("avatar/{group}/{name}")
	public byte[] setAvatar(
			@PathVariable String group,
			@PathVariable String name,
			@RequestParam(required = false) MultipartFile file) {
		try (var repo = service.get(group, name)) {
			repo.settings.set(RepositorySetting.AVATAR, file != null ? file.getBytes() : null);
			return getAvatar(group, name);
		} catch (IOException e) {
			throw Response.error("Error reading avatar file");
		}
	}

	@PutMapping("settings/{group}/{name}/{setting}")
	public void setSetting(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable RepositorySetting setting,
			@RequestBody Map<String, Object> data) {
		var value = data.get("value");
		try (var repo = service.get(group, name)) {
			switch (setting) {
				case TAGS:
					var tags = parseStringList(value);
					var previous = repo.settings.get(RepositorySetting.TAGS, new ArrayList<String>());
					repo.settings.set(setting, value);
					if (!new HashSet<>(tags).equals(new HashSet<>(previous))) {
						indexService.updatePrivateTagsAsync(RepositoryPath.of(group, name));
					}
					break;
				default:
					repo.settings.set(setting, value);
					break;
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

	@DeleteMapping("{group}/{name}")
	public void delete(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			var notification = notificationService.repositoryDeleted(repo);
			indexService.deleteIndex(repo);
			deleteService.delete(repo);
			notification.send();
		}
	}

}
