package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;

@RestController
@RequestMapping("ws/public/repository")
public class RepositoryController {

	private final RepositoryService service;
	private final HistoryService historyService;
	private final ReleaseService releaseService;
	private final SettingsService settings;

	public RepositoryController(RepositoryService service, HistoryService historyService,
			ReleaseService releaseService, 	SettingsService settings) {
		this.service = service;
		this.historyService = historyService;
		this.releaseService = releaseService;
		this.settings = settings;
	}

	@GetMapping
	public List<Map<String, Object>> getReleased() {
		try (var repositories = service.getReleased()) {
			return repositories.stream()
					.map(this::putReleaseInfo)
					.toList();
		}
	}

	private Map<String, Object> putReleaseInfo(Repository repo) {
		var commit = historyService.getLatestAccessibleCommit(repo);
		var release = releaseService.get(repo.path(), commit.id);
		return Repositories.map(repo, commit, release);
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> get(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			var mappedRepo = putReleaseInfo(repo);
			var sortedCommitIds = historyService.getAccessibleCommits(repo).stream()
					.map(c -> c.id)
					.collect(Collectors.toList());
			Collections.reverse(sortedCommitIds);
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

	@GetMapping("meta/{group}/{name}")
	public ResponseEntity<?> getMetaData(
			@PathVariable String group,
			@PathVariable String name) {
		try (var repo = service.get(group, name)) {
			var commit = historyService.getLatestAccessibleCommit(repo);
			if (commit == null)
				return Response.ok(new HashMap<>());
			var counts = new HashMap<ModelType, Long>();
			repo.references.find().includeLibraries().commit(commit.id).iterate(ref -> {
				counts.put(ref.type, counts.getOrDefault(ref.type, 0l) + 1);
			});
			var sortedCounts = new LinkedHashMap<ModelType, Long>();
			List<String> orderedTypes = settings.get(ServerSetting.MODEL_TYPES_ORDER);
			for (var typeString : orderedTypes) {
				var type = ModelType.valueOf(typeString);
				if (counts.containsKey(type)) {
					sortedCounts.put(type, counts.get(type));
				}
			}
			var metaData = new HashMap<String, Object>();
			metaData.put("counts", sortedCounts);
			var modelTypes = repo.getModelTypes(commit);
			var mainModelType = (String) repo.settings.get(RepositorySetting.MAIN_MODEL_TYPE);
			if (mainModelType != null && modelTypes.contains(ModelType.valueOf(mainModelType))) {
				metaData.put("mainModelType", mainModelType);
			}
			return Response.ok(metaData);
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

	@GetMapping("file/{group}/{name}/{type}/{refId}/{path}")
	public ResponseEntity<Resource> getFile(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable ModelType type,
			@PathVariable String refId,
			@PathVariable String path,
			@RequestParam(required = false) String commitId) throws IOException {
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
