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
import org.openlca.git.model.ModelRef;
import org.openlca.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.DatasetWriter;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

abstract class DownloadController {

	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	protected final RepositoryService repoService;
	protected final UserService userService;
	protected final HistoryService historyService;

	protected DownloadController(RepositoryService repoService, UserService userService,
			HistoryService historyService) {
		this.repoService = repoService;
		this.userService = userService;
		this.historyService = historyService;
	}

	protected String prepare(String group, String repository, String commitId, String path) {
		if (Strings.nullOrEmpty(path))
			return prepare(group, repository, commitId);
		try (var repo = repoService.get(group, repository)) {
			log().info("Exporting repository {}/{}/{} (commit id {})", group, repository, path, commitId);
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound("commit " + commitId + " not found");
			var writer = prepareWriter(repo, commit, true);
			write(writer, repo, path, commit.id);
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
			var entry = repo.references.get(ref.path, commitId);
			if (entry == null)
				throw Response.notFound("entry " + ref.path + " not found");
			log().info("Exporting {} {} of repository {}/{} (commit id {})", type, refId, group, repository, commitId);
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound("commit " + commitId + " not found");
			var writer = prepareWriter(repo, commit, true);
			writer.write(entry);
			return put(writer, refId + "_" + commit.id + ".zip");
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	protected String prepare(String group, String repository, String commitId, Set<String> paths) {
		try (var repo = repoService.get(group, repository)) {
			log().info("Exporting {} paths  of repository {}/{}/{} (commit id {})", paths.size(), group, repository,
					commitId);
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound("commit " + commitId + " not found");
			var writer = prepareWriter(repo, commit, true);
			paths.stream().forEach(path -> write(writer, repo, path, commit.id));
			return put(writer, repo.toFilename());
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	private String prepare(String group, String repository, String commitId) {
		try (var repo = repoService.get(group, repository)) {
			log().info("Exporting repository {}/{} (commit id {})", group, repository, commitId);
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound("commit " + commitId + " not found");
			var writer = prepareWriter(repo, commit, false);
			write(writer, repo, null, commit.id);
			repo.references.find().includeCategories().commit(commit.id).iterate(writer::write);
			return put(writer, repo.toFilename());
		} catch (IOException e) {
			throw Response.error("Error writing data sets to tmp file");
		}
	}

	private DatasetWriter prepareWriter(Repository repo, Commit commit, boolean withReferences) throws IOException {
		var writer = createWriter(repo, commit);
		if (withReferences) {
			writer.withReferences();
		}
		return writer;
	}
	
	private void write(DatasetWriter writer, Repository repo, String path, String commitId) {
		var ref = new ModelRef(path);
		if (ref.isDataset) {
			writer.write(repo.references.get(path, commitId));
		} else {
			repo.references.find().includeCategories().commit(commitId).path(path).iterate(writer::write);
		}		
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

	protected ResponseEntity<Resource> download(String token) {
		var info = tokens.get(token);
		if (info == null)
			throw Response.notFound();
		var user = userService.getCurrentUser();
		if (!getUserId(user).equals(info.userId))
			throw Response.forbidden();
		return Response.ok(info.filename, new File(info.path));
	}

	protected abstract DatasetWriter createWriter(Repository repo, Commit commit) throws IOException;

	protected abstract Logger log();

	private record TokenInfo(String path, String filename, String userId) {
	}

}
