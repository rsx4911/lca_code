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
import com.greendelta.collaboration.controller.util.Releases;
import com.greendelta.collaboration.controller.util.Repositories;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.ReleaseInfo;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.RepositoryService;

@RestController
@RequestMapping("ws/public/repository")
public class RepositoryController {

	private final RepositoryService service;
	private final GroupService groupService;
	private final HistoryService historyService;
	private final ReleaseService releaseService;

	public RepositoryController(RepositoryService service, GroupService groupService, HistoryService historyService,
			ReleaseService releaseService) {
		this.service = service;
		this.groupService = groupService;
		this.historyService = historyService;
		this.releaseService = releaseService;
	}

	@GetMapping
	public List<Map<String, Object>> getReleased() {
		try (var repositories = service.getReleased()) {
			return repositories.stream()
					.map(repo -> Repositories.mapForList(repo, true))
					.toList();
		}
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> get(
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		try (var repo = service.get(group, name)) {
			var mappedRepo = Repositories.mapForUser(repo, groupService.isUserNamespace(group));
			var sortedCommitIds = historyService.getAccessibleCommits(repo).stream()
					.map(c -> c.id)
					.toList();
			var releases = releaseService.getFor(repo.path()).stream()
					.sorted((r1, r2) -> compare(r1, r2, sortedCommitIds))
					.map(Releases::map)
					.toList();
			mappedRepo.put("releases", releases);
			return mappedRepo;
		}
	}

	private int compare(ReleaseInfo r1, ReleaseInfo r2, List<String> sortedCommitIds) {
		return Integer.compare(sortedCommitIds.indexOf(r1.commitId), sortedCommitIds.indexOf(r2.commitId));
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
