package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.io.DatasetWriter;
import com.greendelta.collaboration.webservice.ReferenceCollector.Reference;

abstract class DownloadResource {

	private static final Logger log = LogManager.getLogger(DownloadResource.class);
	private final static Map<String, TokenInfo> tokens = new HashMap<>();
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final BrowseService browseService;
	private final UserService userService;

	protected DownloadResource(RepositoryService repoService, HistoryService historyService,
			SearchService searchService, BrowseService browseService, UserService userService) {
		this.repoService = repoService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.browseService = browseService;
		this.userService = userService;
	}

	protected Response prepare(String group, String repository, String commitId, String path) {
		Repository repo = repoService.get(group, repository);
		Commit commit = null;
		if (commitId != null) {
			commit = historyService.getCommit(repo, commitId);
			if (commit == null)
				return Respond.notFound(commitId);
		}
		ModelType type = null;
		String subPath = null;
		if (!Strings.isEmpty(path)) {
			if (path.contains("/")) {
				type = ModelType.valueOf(path.substring(0, path.indexOf('/')));
				subPath = path.substring(path.indexOf('/') + 1);
			} else {
				type = ModelType.valueOf(path);
			}
		}
		List<IndexEntry> entries = searchService.getMostRecent(repo, type, subPath, commit);
		List<FileReference> references = Collections.convert(entries, (e) -> e.asFileReference());
		return prepare(group, repository, commitId, references);
	}

	protected Response prepare(String group, String repository, ModelType type, String refId, String commitId) {
		Repository repo = repoService.get(group, repository);
		if (commitId == null) {
			Commit commit = historyService.getLastCommit(repo, type, refId);
			if (commit != null)
				commitId = commit.id;
		}
		if (commitId == null)
			return Respond.notFound(type.name() + " " + refId + " not found");
		try {
			log.info("Exporting {} {} of repository {}/{} (commit id {})", type, refId, group, repository, commitId);
			DatasetWriter writer = createWriter(repo, commitId);
			writer.write(type, refId);
			File tmpFile = writer.close();
			String token = put(tmpFile, refId + "_" + commitId + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	protected Response prepare(String group, String repository, String commitId, Collection<FileReference> requested) {
		try {
			Repository repo = repoService.get(group, repository);
			if (commitId == null) {
				Commit commit = historyService.getLastCommit(repo);
				commitId = commit.id;
			}
			log.info("Exporting repository {}/{} (commit id {})", group, repository, commitId);
			DatasetWriter writer = createWriter(repo, commitId);
			for (FileReference element : requested) {
				writer.write(element.type, element.refId);
			}
			File tmpFile = writer.close();
			String token = put(tmpFile, group + "_" + repository + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	protected Set<FileReference> collectRefs(String group, String repository, List<Reference> references) {
		Repository repo = repoService.get(group, repository);
		ReferenceCollector<FileReference> collector = new ReferenceCollector<>(browseService, this::toRef);
		return collector.getReferences(repo, references);
	}

	private FileReference toRef(Reference ref) {
		FileReference fRef = new FileReference();
		fRef.type = ref.type;
		fRef.refId = ref.id;
		return fRef;
	}

	protected String put(File file, String filename) {
		User user = userService.getCurrentUser();
		String token = UUID.randomUUID().toString();
		tokens.put(token, new TokenInfo(file.getAbsolutePath(), filename, getUserId(user)));
		return token;
	}

	private String getUserId(User user) {
		if (user.getId() == 0l || user.username == null)
			return user.username;
		return "anonymous";
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

	protected abstract DatasetWriter createWriter(Repository repo, String commitId) throws IOException;

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
