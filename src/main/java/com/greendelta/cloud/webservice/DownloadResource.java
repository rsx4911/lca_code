package com.greendelta.cloud.webservice;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.util.DatasetWriter;

@Path("download")
public class DownloadResource {

	private final static Map<String, String> tokenToPath = new HashMap<>();
	private final static Map<String, String> tokenToFilename = new HashMap<>();
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final FetchService fetchService;

	@Inject
	public DownloadResource(RepositoryService repoService, HistoryService historyService, FetchService fetchService) {
		this.repoService = repoService;
		this.historyService = historyService;
		this.fetchService = fetchService;
	}

	@GET
	@Path("prepare/{group}/{repository}/{type}/{refId}/{commitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepare(@PathParam("group") String group, @PathParam("repository") String repository,
			@PathParam("type") ModelType type, @PathParam("refId") String refId, @PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, repository);
		if (commitId.equals("null")) {
			Commit commit = historyService.getLastCommit(repo, type, refId);
			if (commit != null)
				commitId = commit.id;
		}
		if (commitId == null)
			return Respond.notFound(type.name() + " " + refId + " not found");
		try {
			DatasetWriter writer = new DatasetWriter(fetchService, historyService, repo);
			writer.write(type, refId, commitId);
			File tmpFile = writer.close();
			String token = UUID.randomUUID().toString();
			tokenToPath.put(token, tmpFile.getAbsolutePath());
			tokenToFilename.put(token, refId + "_" + commitId + ".zip");
			return Respond.ok(token);
		} catch (IOException e) {
			return Respond.error("Error writing data sets to tmp file");
		}
	}

	@GET
	@Path("{token}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("token") String token) {
		String path = tokenToPath.remove(token);
		String filename = tokenToFilename.remove(token);
		if (path == null)
			return Respond.notFound();
		File tmpFile = new File(path);
		return Respond.ok(filename, tmpFile, () -> tmpFile.delete());
	}

}
