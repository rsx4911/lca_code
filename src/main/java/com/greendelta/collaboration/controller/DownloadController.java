package com.greendelta.collaboration.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.FrontendReference;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

abstract class DownloadController {

	private static final Logger log = LogManager.getLogger(DownloadController.class);
	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final UserService userService;

	protected DownloadController(RepositoryService repoService, UserService userService) {
		this.repoService = repoService;
		this.userService = userService;
	}

	protected String prepare(String group, String repository, String commitId, String path) {
		try (var repo = repoService.get(group, repository)) {
			var refs = repo.references().find().path(path).commit(commitId).all();
			if (refs == null)
				throw Response.notFound("commit " + commitId + " not found");
			return prepare(group, repository, commitId, refs);
		}
	}

	protected String prepare(String group, String repository, ModelType type, String refId, String commitId) {
		try (var repo = repoService.get(group, repository)) {
			var commit = repo.commits().find().until(commitId).latest();
			if (commit == null)
				throw Response.notFound("commit " + commitId + " not found");
			log.info("Exporting {} {} of repository {}/{} (commit id {})", type, refId, group, repository,
					commit.id);
			var writer = createWriter(repo, commit);
			writer.write(type, refId);
			var tmpFile = writer.close();
			var token = put(tmpFile, refId + "_" + commit.id + ".zip");
			return token;
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	protected String prepare(String group, String repository, String commitId,
			Collection<Reference> requested) {
		try (var repo = repoService.get(group, repository)) {
			var commit = repo.commits().find().until(commitId).latest();
			if (commit == null)
				throw Response.notFound("commit " + commitId + " not found");
			log.info("Exporting repository {}/{} (commit id {})", group, repository, commit.id);
			var writer = createWriter(repo, commit);
			for (var next : requested) {
				writer.write(next.type, next.refId);
			}
			var tmpFile = writer.close();
			var token = put(tmpFile, repo.toFilename());
			return token;
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	protected List<Reference> collectRefs(String group, String repository, List<FrontendReference> references) {
		try (var repo = repoService.get(group, repository)) {
			return FrontendReference.collect(repo, references);
		}
	}

	protected String put(File file, String filename) {
		var user = userService.getCurrentUser();
		var token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(file.getAbsolutePath(), filename, getUserId(user)));
		return token;
	}

	private String getUserId(User user) {
		if (user.isAnonymous())
			return "anonymous";
		return user.username;
	}

	protected ResponseEntity<StreamingResponseBody> download(String token) {
		TokenInfo info = tokens.get(token);
		if (info == null)
			throw Response.notFound();
		var user = userService.getCurrentUser();
		if (!getUserId(user).equals(info.userId))
			throw Response.forbidden();
		var tmpFile = new File(info.path);
		return Response.ok(info.filename, tmpFile, () -> tmpFile.delete());
	}

	protected abstract DatasetWriter createWriter(Repository repo, Commit commit) throws IOException;

	private record TokenInfo(String path, String filename, String userId) {
	}

}
