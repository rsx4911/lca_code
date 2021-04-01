package com.greendelta.collaboration.webservice.user;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.common.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.ChangeLogService;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("changelog")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class ChangeLogResource {

	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final ChangeLogService service;
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public ChangeLogResource(ChangeLogService service, RepositoryService repoService, UserService userService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
	}

	@GET
	@Path("{group}/{name}")
	public Response request(@Context HttpServletRequest request, @PathParam("group") String group,
			@PathParam("name") String name) {
		return request(request, group, name, null);
	}

	@GET
	@Path("{group}/{name}/{commitId}")
	public Response request(@Context HttpServletRequest request, @PathParam("group") String group,
			@PathParam("name") String name, @PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		File file = null;
		String filename = null;
		if (Strings.isNullOrEmpty(commitId)) {
			file = service.generate(request, repo);
			filename = "changelog_" + repo.toId() + ".zip";
		} else {
			file = service.generate(request, repo, commitId);
			filename = "changelog_" + repo.toId() + "-" + commitId + ".zip";
		}
		if (file == null)
			return Respond.badRequest("Could not render changelog");
		String token = put(file, filename);
		return Respond.ok(token);
	}

	@GET
	@Path("{token}")
	public Response download(@PathParam("token") String token) {
		TokenInfo info = tokens.get(token);
		if (info == null)
			return Respond.notFound();
		User user = userService.getCurrentUser();
		if (user == null)
			return Respond.forbidden();
		if (!user.username.equals(info.userId))
			return Respond.forbidden();
		File tmpFile = new File(info.path);
		return Respond.ok(info.filename, tmpFile, () -> tmpFile.delete());
	}

	private String put(File file, String filename) {
		User user = userService.getCurrentUser();
		String token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(file.getAbsolutePath(), filename, user.username));
		return token;
	}

	private static class TokenInfo {
		private final String path;
		private final String filename;
		private final String userId;

		private TokenInfo(String path, String filename, String username) {
			this.path = path;
			this.filename = filename;
			this.userId = username;
		}
	}
}
