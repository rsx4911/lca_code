package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.export.DatasetWriter;

abstract class DownloadResource {

	private final static Map<String, String> tokenToPath = new HashMap<>();
	private final static Map<String, String> tokenToFilename = new HashMap<>();
	private final RepositoryService repoService;
	private final HistoryService historyService;

	protected DownloadResource(RepositoryService repoService, HistoryService historyService) {
		this.repoService = repoService;
		this.historyService = historyService;
	}

	protected Response prepare(String group, String repository, ModelType type, String refId, String commitId) {
		Repository repo = repoService.get(group, repository);
		if (commitId.equals("null")) {
			Commit commit = historyService.getLastCommit(repo, type, refId);
			if (commit != null)
				commitId = commit.id;
		}
		if (commitId == null)
			return Respond.notFound(type.name() + " " + refId + " not found");
		try {
			DatasetWriter writer = createWriter(repo);
			writer.write(type, refId, commitId);
			File tmpFile = writer.close();
			String token = put(tmpFile, refId + "_" + commitId + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	protected Response prepare(String group, String repository, List<FileReference> requested) {
		Repository repo = repoService.get(group, repository);
		try {
			DatasetWriter writer = createWriter(repo);
			for (FileReference element : requested) {
				Commit commit = historyService.getLastCommit(repo, element.type, element.refId);
				if (commit == null)
					continue;
				writer.write(element.type, element.refId, commit.id);
			}
			File tmpFile = writer.close();
			String token = put(tmpFile, group + "_" + repository + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}	
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

	protected abstract DatasetWriter createWriter(Repository repo) throws IOException;

}
