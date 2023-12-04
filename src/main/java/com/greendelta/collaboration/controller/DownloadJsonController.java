package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.io.JsonWriter;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/download/json")
public class DownloadJsonController extends DownloadController {

	private final RepositoryService repoService;
	private final LibraryService libraryService;

	public DownloadJsonController(RepositoryService repoService, UserService userService,
			LibraryService libraryService) {
		super(repoService, userService);
		this.repoService = repoService;
		this.libraryService = libraryService;
	}

	@Override
	@GetMapping("{token}")
	public ResponseEntity<StreamingResponseBody> download(@PathVariable("token") String token) {
		if (token.startsWith("repository_")) {
			try (var repo = repoService.get(token.substring(11).replace("@", "/"))) {
				if (repo.getCachedJsonFile().exists())
					return Response.ok(repo.toFilename(), repo.getCachedJsonFile());
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
		if (isCompleteCurrentRepo(group, repository, commitId, path))
			return "repository_" + group + "@" + repository;
		return super.prepare(group, repository, commitId, path);
	}

	private boolean isCompleteCurrentRepo(String group, String repository, String commitId, String path) {
		try (var repo = repoService.get(group, repository)) {
			if (!repo.getCachedJsonFile().exists())
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
		return new JsonWriter(repo, repo.linkedLibraries(libraryService.getLibraryUrlResolver()), commit);
	}

	@Override
	protected Logger log() {
		return LogManager.getLogger(DownloadJsonController.class);
	}

}
