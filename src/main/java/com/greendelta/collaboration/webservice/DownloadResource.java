package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.DatasetWriter;

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
	@Path("prepare/{group}/{repository}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepare(@PathParam("group") String group, @PathParam("repository") String repository) {
		Repository repo = repoService.get(group, repository);
		List<Commit> commits = historyService.getCommits(repo);
		if (commits.size() == 0)
			return Respond.noContent();
		Set<String> alreadyAdded = new HashSet<>();
		Collections.reverse(commits);
		try {
			DatasetWriter writer = new DatasetWriter(fetchService, historyService, repo);
			for (Commit commit : commits) {
				List<Dataset> descriptors = historyService.getReferences(repo, commit.id);
				for (Dataset descriptor : descriptors) {
					if (alreadyAdded.contains(toKey(descriptor)))
						continue;
					alreadyAdded.add(toKey(descriptor));
					writer.write(descriptor.type, descriptor.refId, commit.id);
				}
			}
			File tmpFile = writer.close();
			String token = UUID.randomUUID().toString();
			tokenToPath.put(token, tmpFile.getAbsolutePath());
			tokenToFilename.put(token, group + "_" + repository + ".zip");
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

	private String toKey(FileReference reference) {
		return reference.type.name() + "_" + reference.refId;
	}
}
