package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.repository.Descriptors.Descriptor;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.io.DatasetWriter;
import com.greendelta.collaboration.webservice.ReferenceCollector.Reference;
import com.greendelta.collaboration.webservice.util.Client;

abstract class DownloadResource {

	private static final Logger log = LogManager.getLogger(DownloadResource.class);
	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final BrowseService browseService;
	private final UserService userService;

	protected DownloadResource(RepositoryService repoService, BrowseService browseService, UserService userService) {
		this.repoService = repoService;
		this.browseService = browseService;
		this.userService = userService;
	}

	protected Response prepare(String group, String repository, String commitId, String path) {
		Repository repo = repoService.get(group, repository);
		Commit commit = getCommit(repo, commitId);
		if (commit == null)
			return Respond.notFound("commit " + commitId + " not found");
		ModelType type = Client.getTypeFromPath(path);
		String subPath = Client.getCategoryFromPath(path);
		Iterator<Descriptor> entries = repo.descriptors.getForPath(type, commit, subPath);
		return prepare(group, repository, commit.id, entries);
	}

	protected Response prepare(String group, String repository, ModelType type, String refId, String commitId) {
		Repository repo = repoService.get(group, repository);
		Commit commit = getCommit(repo, commitId);
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
			Iterator<? extends Descriptor> requested) {
		try {
			Repository repo = repoService.get(group, repository);
			Commit commit = getCommit(repo, commitId);
			if (commit == null)
				return Respond.notFound("commit " + commitId + " not found");
			log.info("Exporting repository {}/{} (commit id {})", group, repository, commit.id);
			DatasetWriter writer = createWriter(repo, commit);
			while (requested.hasNext()) {
				Descriptor next = requested.next();
				writer.write(next.type, next.refId);
			}
			File tmpFile = writer.close();
			String token = put(tmpFile, repo.toFilename());
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	private Commit getCommit(Repository repo, String commitId) {
		Commit commit = repo.commits.get(commitId);
		if (commit != null)
			return commit;
		return repo.commits.find().last();
	}

	protected Iterator<Descriptor> collectRefs(String group, String repository, List<Reference> references) {
		Repository repo = repoService.get(group, repository);
		ReferenceCollector<Descriptor> collector = new ReferenceCollector<>(browseService, this::toRef);
		return collector.getReferences(repo, references).iterator();
	}

	private Descriptor toRef(Reference ref) {
		Descriptor descriptor = new Descriptor();
		descriptor.type = ref.type;
		descriptor.refId = ref.id;
		descriptor.commitId = ref.commitId;
		return descriptor;
	}

	protected String put(File file, String filename) {
		User user = userService.getCurrentUser();
		String token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(file.getAbsolutePath(), filename, getUserId(user)));
		return token;
	}

	private String getUserId(User user) {
		if (user == null || user.getId() == 0l || user.username == null)
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
