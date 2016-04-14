package com.greendelta.cloud.webservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
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
import org.openlca.cloud.model.data.FileReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("checkout")
public class CheckoutResource {

	private final static Logger log = LoggerFactory.getLogger(CheckoutResource.class);
	private final RepositoryService repoService;
	private final FetchService fetchService;
	private final HistoryService historyService;

	@Inject
	public CheckoutResource(RepositoryService repoService, FetchService fetchService, HistoryService historyService) {
		this.repoService = repoService;
		this.fetchService = fetchService;
		this.historyService = historyService;
	}

	@GET
	@Path("{group}/{name}/{untilCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkout(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("untilCommitId") String untilCommitId) {
		Repository repo = repoService.get(group, name);
		if (untilCommitId.equals("null"))
			untilCommitId = null;
		List<Commit> commits = historyService.getCommitsUntil(repo, untilCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		StreamingOutput data = prepareFetch(commits, repo);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

	private StreamingOutput prepareFetch(List<Commit> commits, Repository repo) {
		try {
			FetchWriter writer = new FetchWriter(null);
			Collections.reverse(commits);
			Set<String> alreadyPut = new HashSet<>();
			for (Commit commit : commits) {
				for (Dataset dataset : historyService.getReferences(repo, commit.id)) {
					if (alreadyPut.contains(toKey(dataset)))
						continue;
					put(writer, repo, dataset, commit.id);
					alreadyPut.add(toKey(dataset));
				}
			}
			writer.setCommitId(commits.get(0).id);
			writer.close();
			return toStream(writer.getFile());
		} catch (IOException e) {
			log.error("Error closing fetch writer", e);
			return null;
		}
	}

	private void put(FetchWriter writer, Repository repo, Dataset dataset, String commitId) throws IOException {
		String data = fetchService.getDataset(repo, dataset.type, dataset.refId, commitId);
		if (Strings.isNullOrEmpty(data))
			return; // ignore deleted data sets
		File binDir = fetchService.getBinDir(repo, dataset.type, dataset.refId, commitId);
		writer.put(dataset, data, binDir);
	}

	private String toKey(FileReference reference) {
		return reference.type.name() + "_" + reference.refId;
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

}
