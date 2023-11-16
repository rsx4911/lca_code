package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
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

import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.io.LegacyJsonWriter;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/download/json1")
public class DownloadLegacyJsonController extends DownloadController {

	public DownloadLegacyJsonController(RepositoryService repoService, UserService userService) {
		super(repoService, userService);
	}

	@Override
	@GetMapping("{token}")
	public ResponseEntity<StreamingResponseBody> download(@PathVariable("token") String token) {
		return super.download(token);
	}

	@GetMapping("prepare/{group}/{repository}")
	public String prepareByPath(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository,
			@RequestParam(name = "commitId", required = false) String commitId,
			@RequestParam(name = "path", required = false) String path) {
		return super.prepare(group, repository, commitId, path);
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
		return new LegacyJsonWriter(repo, commit);
	}

	@Override
	protected Logger log() {
		return LogManager.getLogger(DownloadLegacyJsonController.class);
	}

}
