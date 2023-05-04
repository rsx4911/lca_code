package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.List;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.FrontendReferences;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.io.JsonWriter;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/download/json")
public class DownloadJsonController extends DownloadController {

	private final RepositoryService repoService;

	@Autowired
	public DownloadJsonController(RepositoryService repoService, UserService userService) {
		super(repoService, userService);
		this.repoService = repoService;
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
			if (commitId != null && !commitId.equals(repo.commits().resolve("HEAD")))
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
			@RequestBody FrontendReferences references) {
		return super.prepare(group, repository, commitId, collectRefs(group, repository, references));
	}

	@PutMapping("prepare/{group}/{repository}")
	public String prepareRequested(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository,
			@RequestParam(name = "commitId", required = false) String commitId,
			@RequestBody List<Reference> requested) {
		return super.prepare(group, repository, commitId, requested);
	}

	@Override
	protected DatasetWriter createWriter(Repository repo, Commit commit) throws IOException {
		return new JsonWriter(repo.references(), repo.datasets(), commit);
	}

}
