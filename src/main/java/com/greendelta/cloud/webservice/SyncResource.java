package com.greendelta.cloud.webservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.openlca.cloud.api.data.FetchWriter;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.cloud.model.data.FileReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("sync")
public class SyncResource {

	private final static Logger log = LoggerFactory.getLogger(SyncResource.class);
	private final FetchService fetchService;
	private final HistoryService historyService;
	private final RepositoryService repoService;

	@Inject
	public SyncResource(FetchService fetchService, HistoryService historyService, RepositoryService repoService) {
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.repoService = repoService;
	}

	@GET
	@Path("{group}/{name}/{untilCommitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("untilCommitId") String untilCommitId) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = null;
		if (untilCommitId.equals("null"))
			commits = historyService.getCommits(repo);
		else
			commits = historyService.getCommitsUntil(repo, untilCommitId);
		if (commits.isEmpty())
			return Respond.noContent();
		List<FetchRequestData> result = getData(commits, repo);
		return Respond.ok(new ArrayList<>(result));
	}

	private List<FetchRequestData> getData(List<Commit> commits, Repository repo) {
		List<FetchRequestData> result = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();
		Collections.reverse(commits);
		for (Commit commit : commits) {
			List<Dataset> descriptors = historyService.getReferences(repo, commit.id);
			for (Dataset descriptor : descriptors) {
				if (alreadyAdded.contains(toKey(descriptor)))
					continue;
				FetchRequestData value = fetchService.toRequestData(repo, commit.id, descriptor);
				result.add(value);
				alreadyAdded.add(toKey(descriptor));
			}
		}
		return result;
	}

	@PUT
	@Path("get/{group}/{name}/{untilCommitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("untilCommitId") String untilCommitId, List<Dataset> requested) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = null;
		if (untilCommitId.equals("null"))
			commits = historyService.getCommits(repo);
		else
			commits = historyService.getCommitsUntil(repo, untilCommitId);
		if (commits.isEmpty())
			return Respond.noContent();
		FetchWriter writer = new FetchWriter(null);
		Collections.reverse(commits);
		try {
			Set<String> alreadyPut = new HashSet<>();
			for (Commit commit : commits) {
				for (Dataset dataset : historyService.getReferences(repo, commit.id)) {
					if (requested != null && !requested.contains(dataset))
						continue;
					if (alreadyPut.contains(toKey(dataset)))
						continue;
					put(writer, repo, dataset, commit.id);
					alreadyPut.add(toKey(dataset));
				}
			}
			writer.close();
			return Respond.ok(toStream(writer.getFile()));
		} catch (IOException e) {
			log.error("Error closing fetch writer", e);
			return null;
		}
	}

	private void put(FetchWriter writer, Repository repo, Dataset dataset, String commitId) throws IOException {
		String data = fetchService.getDataset(repo, dataset.type, dataset.refId, commitId);
		File binDir = fetchService.getBinDir(repo, dataset.type, dataset.refId, commitId);
		writer.put(dataset, data, binDir);
	}

	private StreamingOutput toStream(File file) {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				Files.copy(file.toPath(), output);
			}

		};
	}

	private String toKey(FileReference reference) {
		return reference.type.name() + "_" + reference.refId;
	}
}
