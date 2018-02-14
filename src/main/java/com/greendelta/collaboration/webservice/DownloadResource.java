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

import org.apache.logging.log4j.util.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.BrowseService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.export.DatasetWriter;
import com.greendelta.collaboration.webservice.ReferenceCollector.Reference;

abstract class DownloadResource {

	private final static Map<String, String> tokenToPath = new HashMap<>();
	private final static Map<String, String> tokenToFilename = new HashMap<>();
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final BrowseService browseService;

	protected DownloadResource(RepositoryService repoService, HistoryService historyService,
			SearchService searchService, BrowseService browseService) {
		this.repoService = repoService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.browseService = browseService;
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
			DatasetWriter writer = createWriter(repo, commitId);
			writer.write(type, refId);
			File tmpFile = writer.close();
			String token = put(tmpFile, refId + "_" + commitId + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
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

	protected Response prepare(String group, String repository, String commitId, Collection<FileReference> requested) {
		try {
			Repository repo = repoService.get(group, repository);
			if (commitId == null) {
				Commit commit = historyService.getLastCommit(repo);
				commitId = commit.id;
			}
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
		String token = UUID.randomUUID().toString();
		tokenToPath.put(token, file.getAbsolutePath());
		tokenToFilename.put(token, filename);
		return token;
	}

	protected Response download(String token) {
		String path = tokenToPath.remove(token);
		String filename = tokenToFilename.remove(token);
		if (path == null)
			return Respond.notFound();
		File tmpFile = new File(path);
		return Respond.ok(filename, tmpFile, () -> tmpFile.delete());
	}

	protected abstract DatasetWriter createWriter(Repository repo, String commitId) throws IOException;

}
