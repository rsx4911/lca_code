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
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/download/json")
public class DownloadJsonController extends DownloadController {

	private final RepositoryService repoService;
	private final LibraryService libraryService;
	private final ReleaseService releaseService;
	private final FileService fileService;

	public DownloadJsonController(RepositoryService repoService, UserService userService,
			LibraryService libraryService, ReleaseService releaseService, FileService fileService) {
		super(repoService, userService);
		this.repoService = repoService;
		this.libraryService = libraryService;
		this.releaseService = releaseService;
		this.fileService = fileService;
	}

	@Override
	@GetMapping("{token}")
	public ResponseEntity<Resource> download(@PathVariable("token") String token) {
		if (token.startsWith("repository_")) {
			try (var repo = repoService.get(token.substring(11).replace("@", "/"))) {
				// TODO offer all cached json files, not just the latest
				var latestRelease = releaseService.getLatest(repo.path());
				var cachedJsonFile = repo.getCachedJsonFile(latestRelease.commitId);
				if (cachedJsonFile.exists())
					return Response.ok(repo.toFilename(), cachedJsonFile);
			}
		}
		return super.download(token);
	}

	@GetMapping("prepare/{group}/{repository}")
	public String prepareByPath(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository,
			@RequestParam(name = "commitId", required = false) String commitId,
			@RequestParam(name = "path", required = false) String path) {
		// TODO offer all cached json files, not just the latest
		if (isCompleteCurrentRepo(group, repository, commitId, path))
			return "repository_" + group + "@" + repository;
		return super.prepare(group, repository, commitId, path);
	}

	private boolean isCompleteCurrentRepo(String group, String repository, String commitId, String path) {
		try (var repo = repoService.get(group, repository)) {
			// TODO offer all cached json files, not just the latest
			var latestRelease = releaseService.getLatest(repo.path());
			var cachedJsonFile = repo.getCachedJsonFile(latestRelease.commitId);
			if (!cachedJsonFile.exists())
				return false; // is not cached
			if (!Strings.nullOrEmpty(path))
				return false; // is not complete repo
			if (commitId != null && !commitId.equals(repo.commits.resolve("HEAD")))
				return false; // is not current state (last commit)
			return true;
		}
	}

	@GetMapping("prepare/{group}/{repository}/{type}/{refId}")
	public String prepareDataset(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@RequestParam(name = "commitId", required = false) String commitId) {
		return super.prepare(group, repository, type, refId, commitId);
	}

	@PostMapping("prepare/{group}/{repository}")
	public String prepareSelection(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository,
			@RequestParam(name = "commitId", required = false) String commitId,
			@RequestBody Set<String> paths) {
		return super.prepare(group, repository, commitId, paths);
	}

	@Override
	protected DatasetWriter createWriter(Repository repo, Commit commit) throws IOException {
		return new JsonWriter(fileService.createTempFile(), repo,
				repo.linkedLibraries(libraryService.getLibraryUrlResolver()), commit);
	}

	@Override
	protected Logger log() {
		return LogManager.getLogger(DownloadJsonController.class);
	}

}
