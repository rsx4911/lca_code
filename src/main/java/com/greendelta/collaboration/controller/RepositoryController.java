package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
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
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.Maps;

@RestController
@RequestMapping("ws/public/repository")
public class RepositoryController {

	private final RepositoryService service;
	private final GroupService groupService;
	private final HistoryService historyService;

	public RepositoryController(RepositoryService service, GroupService groupService, HistoryService historyService) {
		this.service = service;
		this.groupService = groupService;
		this.historyService = historyService;
	}

	@GetMapping
	public List<Map<String, Object>> getReleased() {
		try (var repositories = service.getReleased()) {
			return repositories.stream()
					.map(repo -> Repositories.mapForList(repo, true))
					.toList();
		}
	}

	@GetMapping("count/{group}/{name}")
	public ResponseEntity<?> getReferenceCount(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			var commit = historyService.getLatestAccessibleCommit(repo);
			if (commit == null)
				return Response.ok(Map.of("datasets", 0));
			return Response.ok(Map.of("datasets", repo.references.find().commit(commit.id).count()));
		}
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> get(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			var mappedRepo = Repositories.mapForUser(repo, groupService.isUserNamespace(group));
			var lastRelease = historyService.getLatestAccessibleCommit(repo);
			if (lastRelease != null) {
				Maps.put(mappedRepo, "settings.lastChange", lastRelease.timestamp);
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
	public ResponseEntity<Resource> getFile(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@PathVariable("path") String path,
			@RequestParam(name = "commitId", required = false) String commitId) throws IOException {
		try (var repo = service.get(group, name)) {
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound(notFoundMessage(type, refId, commitId, path));
			var ref = repo.references.get(type, refId, commitId);
			if (ref == null)
				throw Response.notFound(notFoundMessage(type, refId, commitId, path));
			var binary = repo.datasets.getBinary(ref, path);
			if (binary == null)
				throw Response.notFound(notFoundMessage(type, refId, commitId, path));
			var filename = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
			return Response.ok(filename, binary);
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
