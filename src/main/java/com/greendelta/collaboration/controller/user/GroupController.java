package com.greendelta.collaboration.controller.user;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.openlca.util.Strings;
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
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.Routes;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@RestController
@RequestMapping("ws/group")
public class GroupController {

	private final GroupService service;
	private final UserService userService;
	private final AccessService accessService;
	private final MembershipService membershipService;
	private final DeleteService deleteService;
	private final NotificationService notificationService;

	public GroupController(GroupService service, UserService userService, AccessService accessService,
			MembershipService membershipService, DeleteService deleteService, NotificationService notificationService) {
		this.service = service;
		this.userService = userService;
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.deleteService = deleteService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public SearchResult<Map<String, Object>> getAll(
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "module", required = false) Module module,
			@RequestParam(name = "onlyIfCanWrite", defaultValue = "false") boolean onlyIfCanWrite,
			@RequestParam(name = "adminArea", defaultValue = "false") boolean adminArea) {
		var all = service.getAllAccessible();
		if (onlyIfCanWrite) {
			all = all.stream()
					.filter(Predicate.not(accessService::canWrite))
					.collect(Collectors.toList());
		}
		if (adminArea) {
			all = all.stream()
					.filter(Predicate.not(service::isUserNamespace))
					.collect(Collectors.toList());
		}
		var result = SearchResults.pagedAndFiltered(page, pageSize, filter, all);
		var user = userService.getCurrentUser();
		return SearchResults.convert(result, group -> {
			var map = Maps.of("name", (Object) group);
			var settings = service.getSettings(group);
			map.put("settings", settings.toMap());
			map.put("label", settings.get(GroupSetting.LABEL, group));
			if (module != Module.DASHBOARD)
				return map;
			map.put("role", membershipService.getRole(user, group));
			map.put("repositories", service.getRepositoryCount(group));
			map.put("members", membershipService.getMemberships(group).size());
			return map;
		});
	}

	@GetMapping("{name}")
	public Map<String, Object> get(@PathVariable("name") String name) {
		var user = userService.getCurrentUser();
		var isOwnNamespace = service.isOwnNamespace(name);
		if (!isOwnNamespace && !service.exists(name))
			throw Response.notFound("Group " + name + " not found");
		if (!accessService.canRead(name))
			throw Response.forbidden(name, Permission.READ);
		var group = new HashMap<String, Object>();
		group.put("isUserGroup", isOwnNamespace);
		group.put("userCanDelete", !isOwnNamespace && accessService.canDelete(name));
		group.put("userCanWrite", accessService.canWrite(name));
		group.put("userCanCreate", accessService.canCreateRepositoryIn(name));
		group.put("userCanSetSettings", accessService.canSetSettings(name));
		group.put("settings", service.getSettings(name).toMap());
		var isUserspace = user != null && name.equals(user.username);
		group.put("userCanEditMembers", !isUserspace && accessService.canEditMembersOf(name));
		return group;
	}

	@GetMapping("avatar/{name}")
	public byte[] getAvatar(@PathVariable("name") String name) {
		if (!service.isOwnNamespace(name) && !service.exists(name))
			throw Response.notFound(name);
		return Avatar.get(service.getSettings(name).get(GroupSetting.AVATAR), "avatar-group.png");
	}

	@PostMapping("{name}")
	public ResponseEntity<Map<String, Object>> create(@PathVariable("name") String name) {
		if (Strings.nullOrEmpty(name))
			throw Response.badRequest("name", "Missing input: Name");
		if (!Routes.isValid(name))
			throw Response.badRequest("name",
					"Name must consist of at least 4 characters and can only contain characters, numbers and underscore");
		if (Routes.isReserved(name))
			throw Response.badRequest("name", "This is a reserved word");
		if (service.exists(name))
			throw Response.badRequest("name", "Group " + name + " already exists");
		if (!service.create(name, false))
			throw Response.error(
					"Could not create group, does the configured 'Repositories root directory' exist and can be write-accessed?");
		notificationService.groupCreated(name).send();
		return Response.created(Collections.singletonMap("name", name));
	}

	@PutMapping("avatar/{name}")
	public byte[] setAvatar(
			@PathVariable("name") String name,
			@RequestParam(name = "file", required = false) MultipartFile file) {
		if (!service.isOwnNamespace(name) && !service.exists(name))
			throw Response.notFound(name);
		try {
			service.getSettings(name).set(GroupSetting.AVATAR, file != null ? file.getBytes() : null);
			return getAvatar(name);
		} catch (IOException e) {
			throw Response.error("Error reading avatar file");
		}
	}

	@PutMapping("settings/{name}/{setting}")
	public void setSetting(
			@PathVariable("name") String name,
			@PathVariable("setting") GroupSetting setting,
			@RequestBody Map<String, Object> data) {
		if (!service.isOwnNamespace(name) && !service.exists(name))
			throw Response.notFound(name);
		var user = userService.getCurrentUser();
		if (setting.isAdminSetting() && !user.isDataManager() && !user.isUserManager())
			throw Response.forbidden(name, Permission.SET_SETTINGS);
		var value = data.get("value").toString();
		service.getSettings(name).set(setting, value);
	}

	@DeleteMapping("{name}")
	public void delete(@PathVariable("name") String name) {
		if (!service.exists(name) || service.isUserNamespace(name))
			throw Response.notFound("Group " + name + " not found");
		var notification = notificationService.groupDeleted(name);
		deleteService.deleteGroup(name);
		notification.send();
	}

}
