package com.greendelta.collaboration.controller;

import java.io.IOException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.io.LegacyJsonWriter;
import com.greendelta.collaboration.service.FileService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/download/json1")
public class DownloadLegacyJsonController extends DownloadController {

	private final FileService fileService;

	public DownloadLegacyJsonController(RepositoryService repoService, UserService userService,
			HistoryService historyService, FileService fileService) {
		super(repoService, userService, historyService);
		this.fileService = fileService;
	}

	@Override
	@GetMapping("{token}")
	public ResponseEntity<Resource> download(@PathVariable String token) {
		return super.download(token);
	}

	@GetMapping("prepare/{group}/{repository}")
	public String prepareByPath(
			@PathVariable String group,
			@PathVariable String repository,
			@RequestParam(required = false) String commitId,
			@RequestParam(required = false) String path) {
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
		return new LegacyJsonWriter(fileService.createTempFile(), repo, commit);
	}

	@Override
	protected Logger log() {
		return LogManager.getLogger(DownloadLegacyJsonController.class);
	}

}
