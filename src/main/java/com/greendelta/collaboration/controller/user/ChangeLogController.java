package com.greendelta.collaboration.controller.user;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.error.WebRequestException;
import com.greendelta.collaboration.io.ChangeLogWriter;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/changelog")
public class ChangeLogController {

	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final UserService userService;
	private final SettingsService settings;

	public ChangeLogController(RepositoryService repoService, UserService userService, SettingsService settings) {
		this.repoService = repoService;
		this.userService = userService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}")
	public String request(
		 HttpServletRequest request,
			@PathVariable("group") String group,
			@PathVariable("name") String name) {
		return request(request, group, name, null);
	}

	@GetMapping("{group}/{name}/{commitId}")
	public String request(
		 HttpServletRequest request,
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId) {
		if (!settings.is(ServerSetting.CHANGE_LOG_ENABLED))
			throw Response.unavailable("Change log feature not enabled");
		try (var repo = repoService.get(group, name)) {
			File file = null;
			String filename = null;
			var writer = new ChangeLogWriter();
			try {
				if (Strings.nullOrEmpty(commitId)) {
					file = writer.generate(request, repo);
					filename = "changelog_" + repo.path() + ".zip";
				} else {
					var commit = repo.commits.get(commitId);
					if (commit == null)
						throw Response.notFound("Could not find commit with id " + commitId);
					file = writer.generate(request, repo, commit);
					filename = "changelog_" + repo.path() + "-" + commitId + ".zip";
				}
			} catch (WebRequestException e) {
				throw Response.status(e);
			}
			if (file == null)
				throw Response.badRequest("Could not render changelog");
			var token = put(file, filename);
			return token;
		}
	}

	@GetMapping("{token}")
	public ResponseEntity<StreamingResponseBody> download(@PathVariable("token") String token) {
		var info = tokens.get(token);
		if (info == null)
			throw Response.notFound();
		var user = userService.getCurrentUser();
		if (user == null)
			throw Response.forbidden();
		if (!user.username.equals(info.userId))
			throw Response.forbidden();
		var tmpFile = new File(info.path);
		return Response.ok(info.filename, tmpFile, () -> tmpFile.delete());
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
