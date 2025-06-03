package com.greendelta.collaboration.controller.user;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openlca.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.ChangeLogService;
import com.greendelta.collaboration.service.FileService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("ws/changelog")
public class ChangeLogController {

	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final UserService userService;
	private final ChangeLogService changeLogService;
	private final FileService fileService;
	private final SettingsService settings;

	public ChangeLogController(RepositoryService repoService, UserService userService,
			ChangeLogService changeLogService, FileService fileService, SettingsService settings) {
		this.repoService = repoService;
		this.userService = userService;
		this.changeLogService = changeLogService;
		this.fileService = fileService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}")
	public String request(
			HttpServletRequest request,
			@PathVariable String group,
			@PathVariable String name) {
		return request(request, group, name, null);
	}

	@GetMapping("{group}/{name}/{commitId}")
	public String request(
			HttpServletRequest request,
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId) {
		if (!settings.is(ServerSetting.CHANGE_LOG_ENABLED))
			throw Response.unavailable("Change log feature not enabled");
		try (var repo = repoService.get(group, name)) {
			var file = fileService.createTempFile();
			if (Strings.nullOrEmpty(commitId)) {
				changeLogService.generate(file, request, repo);
			} else {
				changeLogService.generate(file, request, repo, commitId);
			}
			if (file == null)
				throw Response.badRequest("Could not render changelog");
			var filename = Strings.nullOrEmpty(commitId)
					? "changelog_" + repo.path() + ".zip"
					: "changelog_" + repo.path() + "-" + commitId + ".zip";
			var token = put(file, filename);
			return token;
		} catch (IOException e) {
			throw Response.error("Error creating change log");
		}
	}

	@GetMapping("{token}")
	public ResponseEntity<Resource> download(@PathVariable String token) {
		var info = tokens.get(token);
		if (info == null)
			throw Response.notFound();
		var user = userService.getCurrentUser();
		if (user == null)
			throw Response.forbidden();
		if (!user.username.equals(info.userId))
			throw Response.forbidden();
		var tmpFile = new File(info.path);
		return Response.ok(info.filename, tmpFile);
	}

	private String put(File file, String filename) {
		var user = userService.getCurrentUser();
		var token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(file.getAbsolutePath(), filename, user.username));
		return token;
	}

	private record TokenInfo(String path, String filename, String userId) {
	}
}
