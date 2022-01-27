package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Repositories;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.ObjectMap;

@RestController
@RequestMapping("ws/public/repository")
public class RepositoryController {

	private final RepositoryService service;
	private final GroupService groupService;

	@Autowired
	public RepositoryController(RepositoryService service, GroupService groupService) {
		this.service = service;
		this.groupService = groupService;
	}

	@GetMapping
	public List<ObjectMap> getPublic() {
		try (var repositories = service.getPublic()) {
			return repositories.stream().map(repo -> {
				var map = Repositories.map(repo);
				map.put("datasets", repo.references().find().all().size());
				return map;
			}).toList();
		}
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> get(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			var publicAccess = repo.settings.is(RepositorySetting.PUBLIC_ACCESS);
			var mappedRepo = Repositories.map(repo, groupService.isUserNamespace(group, publicAccess));
			var lastCommit = repo.commits().head();
			if (lastCommit != null) {
				mappedRepo.put("settings.lastChange", lastCommit.timestamp);
			}
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

	@GetMapping("file/{group}/{name}/{type}/{refId}/{path}")
	public byte[] getFile(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@PathVariable("path") String path,
			@RequestParam(name = "commitId", required = false) String commitId) throws IOException {
		try (var repo = service.get(group, name)) {
			var ref = repo.references().get(type, refId, commitId);
			if (ref == null)
				throw Response.notFound(notFoundMessage(type, refId, commitId, path));
			var binary = repo.datasets().getBinary(ref, path);
			if (binary == null)
				throw Response.notFound(notFoundMessage(type, refId, commitId, path));
			return binary;
		}
	}

	private String notFoundMessage(ModelType type, String refId, String commitId, String filename) {
		var base = "";
		if (!Strings.nullOrEmpty(filename))
			base = "Binary file " + filename + " of ";
		base += type.name() + " " + refId + " not found";
		if (commitId == null)
			return base;
		return base + " for commit id " + commitId;
	}
}
