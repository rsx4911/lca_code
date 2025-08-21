package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.io.JsonWriter;
import com.greendelta.collaboration.service.FileService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/download/json")
public class DownloadJsonController extends DownloadController {

	private final LibraryService libraryService;
	private final FileService fileService;

	public DownloadJsonController(RepositoryService repoService, UserService userService, HistoryService historyService,
			LibraryService libraryService, FileService fileService) {
		super(repoService, userService, historyService);
		this.libraryService = libraryService;
		this.fileService = fileService;
	}

	@Override
	@GetMapping("{token}")
	public ResponseEntity<Resource> download(@PathVariable String token) {
		if (token.startsWith("repository_")) {
			var info = token.substring(11).split("@");
			if (info.length < 3)
				throw Response.notFound();
			try (var repo = repoService.get(info[0], info[1])) {
				var commitId = info[2];
				var commit = historyService.getAccessibleCommit(repo, commitId);
				if (commit == null)
					throw Response.notFound();
				var cachedJsonFile = repo.getCachedJsonFile(commit.id);
				if (!cachedJsonFile.exists())
					throw Response.notFound();
				return Response.ok(repo.toFilename(), cachedJsonFile);
			}
		}
		return super.download(token);
	}

	@GetMapping("prepare/{group}/{repository}")
	public String prepareByPath(
			@PathVariable String group,
			@PathVariable String repository,
			@RequestParam(required = false) String commitId,
			@RequestParam(required = false) String path) {
		try (var repo = repoService.get(group, repository)) {
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (Strings.nullOrEmpty(path) && commit != null) {
				var cachedJsonFile = repo.getCachedJsonFile(commit.id);
				if (cachedJsonFile.exists())
					return "repository_" + group + "@" + repository + "@" + commit.id;
				repoService.generateCachedJson(repo, commit, libraryService.loader());
			}
		}
		return super.prepare(group, repository, commitId, path);
	}

	@GetMapping("prepare/{group}/{repository}/{type}/{refId}")
	public String prepareDataset(
			@PathVariable String group,
			@PathVariable String repository,
			@PathVariable ModelType type,
			@PathVariable String refId,
			@RequestParam(required = false) String commitId) {
		return super.prepare(group, repository, type, refId, commitId);
	}

	@PostMapping("prepare/{group}/{repository}")
	public String prepareSelection(
			@PathVariable String group,
			@PathVariable String repository,
			@RequestParam(required = false) String commitId,
			@RequestBody Set<String> paths) {
		return super.prepare(group, repository, commitId, paths);
	}

	@Override
	protected DatasetWriter createWriter(Repository repo, Commit commit) throws IOException {
		return new JsonWriter(fileService.createTempFile(), repo, libraryService.loader(), commit);
	}

	@Override
	protected Logger log() {
		return LogManager.getLogger(DownloadJsonController.class);
	}

}
