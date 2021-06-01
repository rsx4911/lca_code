package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.Reference;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.io.DatasetWriter;
import com.greendelta.collaboration.webservice.util.FrontendReference;

abstract class DownloadResource {

	private static final Logger log = LogManager.getLogger(DownloadResource.class);
	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final UserService userService;

	protected DownloadResource(RepositoryService repoService, UserService userService) {
		this.repoService = repoService;
		this.userService = userService;
	}

	protected Response prepare(String group, String repository, String commitId, String path) {
		Repository repo = repoService.get(group, repository);
		List<Reference> refs = repo.references.find().path(path).commit(commitId).all();
		if (refs == null)
			return Respond.notFound("commit " + commitId + " not found");
		return prepare(group, repository, commitId, refs);
	}

	protected Response prepare(String group, String repository, ModelType type, String refId, String commitId) {
		Repository repo = repoService.get(group, repository);
		Commit commit = repo.commits.find().until(commitId).latest();
		if (commit == null)
			return Respond.notFound("commit " + commitId + " not found");
		try {
			log.info("Exporting {} {} of repository {}/{} (commit id {})", type, refId, group, repository, commit.id);
			DatasetWriter writer = createWriter(repo, commit);
			writer.write(type, refId);
			File tmpFile = writer.close();
			String token = put(tmpFile, refId + "_" + commit.id + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	protected Response prepare(String group, String repository, String commitId,
			Collection<Reference> requested) {
		try {
			Repository repo = repoService.get(group, repository);
			Commit commit = repo.commits.find().until(commitId).latest();
			if (commit == null)
				return Respond.notFound("commit " + commitId + " not found");
			log.info("Exporting repository {}/{} (commit id {})", group, repository, commit.id);
			DatasetWriter writer = createWriter(repo, commit);
			for (Reference next : requested) {
				writer.write(next.type, next.refId);
			}
			File tmpFile = writer.close();
			String token = put(tmpFile, repo.toFilename());
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	protected List<Reference> collectRefs(String group, String repository, List<FrontendReference> references) {
		Repository repo = repoService.get(group, repository);
		return FrontendReference.collect(repo, references);
	}

	protected String put(File file, String filename) {
		User user = userService.getCurrentUser();
		String token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(file.getAbsolutePath(), filename, getUserId(user)));
		return token;
	}

	private String getUserId(User user) {
		if (user == null || user.id == 0l || user.username == null)
			return "anonymous";
		return user.username;
	}

	protected Response download(String token) {
		TokenInfo info = tokens.get(token);
		if (info == null)
			return Respond.notFound();
		User user = userService.getCurrentUser();
		if (!getUserId(user).equals(info.userId))
			return Respond.forbidden();
		File tmpFile = new File(info.path);
		return Respond.ok(info.filename, tmpFile, () -> tmpFile.delete());
	}

	protected abstract DatasetWriter createWriter(Repository repo, Commit commit) throws IOException;

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
