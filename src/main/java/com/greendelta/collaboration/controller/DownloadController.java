package com.greendelta.collaboration.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

abstract class DownloadController {

	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final UserService userService;

	protected DownloadController(RepositoryService repoService, UserService userService) {
		this.repoService = repoService;
		this.userService = userService;
	}

	protected String prepare(String group, String repository, String commitId, String path) {
		if (Strings.nullOrEmpty(path))
			return prepare(group, repository, commitId);
		try (var repo = repoService.get(group, repository)) {
			log().info("Exporting repository {}/{}/{} (commit id {})", group, repository, path, commitId);
			var writer = prepareWriter(repo, commitId, true);
			repo.entries.iterate(commitId, path, writer::write);
			return put(writer, repo.toFilename());
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	protected String prepare(String group, String repository, ModelType type, String refId, String commitId) {
		try (var repo = repoService.get(group, repository)) {
			var ref = repo.references.get(type, refId, commitId);
			if (ref == null)
				throw Response.notFound("ref " + type + " " + refId + " not found");
			var entry = repo.entries.get(ref.path, commitId);
			if (entry == null)
				throw Response.notFound("entry " + ref.path + " not found");
			log().info("Exporting {} {} of repository {}/{} (commit id {})", type, refId, group, repository, commitId);
			var writer = prepareWriter(repo, commitId, true);
			writer.write(entry);
			return put(writer, refId + "_" + commitId + ".zip");
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	protected String prepare(String group, String repository, String commitId, Set<String> paths) {
		try (var repo = repoService.get(group, repository)) {
			log().info("Exporting {} paths  of repository {}/{}/{} (commit id {})", paths.size(), group, repository,
					commitId);
			var writer = prepareWriter(repo, commitId, true);
			paths.stream().forEach(path -> {
				repo.entries.iterate(commitId, path, writer::write);
			});
			return put(writer, repo.toFilename());
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	private String prepare(String group, String repository, String commitId) {
		try (var repo = repoService.get(group, repository)) {
			log().info("Exporting repository {}/{} (commit id {})", group, repository, commitId);
			var writer = prepareWriter(repo, commitId, false);
			repo.entries.iterate(commitId, writer::write);
			return put(writer, repo.toFilename());
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	private DatasetWriter prepareWriter(Repository repo, String commitId, boolean withReferences) throws IOException {
		var commit = repo.commits.find().until(commitId).latest();
		if (commit == null)
			throw Response.notFound("commit " + commitId + " not found");
		var writer = createWriter(repo, commit);
		if (withReferences) {
			writer.withReferences();
		}
		return writer;
	}

	protected String put(DatasetWriter writer, String filename) throws IOException {
		var user = userService.getCurrentUser();
		var token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(writer.close().getAbsolutePath(), filename, getUserId(user)));
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

	protected abstract Logger log();

	private record TokenInfo(String path, String filename, String userId) {
	}

	public static void main(String[] args) throws IOException {
		var repo = new OlcaRepository(new File("C:\\Users\\greve\\opt\\collab\\git\\greve\\empty_cat"));
		var entries = repo.entries.find().path("FLOW").recursive().all();
		for (var e : entries)
			System.out.println(e.path);
	}

}
