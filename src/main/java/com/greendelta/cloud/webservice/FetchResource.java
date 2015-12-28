package com.greendelta.cloud.webservice;

import static org.openlca.cloud.util.Strings.concat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("fetch")
public class FetchResource {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private FetchService service;
	private RepositoryService repoService;
	private HistoryService historyService;

	@Inject
	public FetchResource(FetchService service, RepositoryService repoService,
			HistoryService historyService) {
		this.service = service;
		this.repoService = repoService;
		this.historyService = historyService;
	}

	@GET
	@Path("data/{group}/{name}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@PathParam("group") String group,
			@PathParam("name") String name, @PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		if (commitId.equals("null"))
			commitId = getLastCommitId(repo, type, refId);
		if (commitId == null) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		String dataset = service.getDataset(repo, type, refId, commitId);
		if (dataset == null) {
			String message = notFoundMessage(type, refId, commitId);
			return Respond.notFound(message);
		}
		return Respond.ok(dataset);
	}

	private String getLastCommitId(Repository repo, ModelType type, String refId) {
		Commit commit = historyService.getLastCommit(repo, type, refId);
		if (commit == null)
			return null;
		return commit.id;
	}

	private String notFoundMessage(ModelType type, String refId, String commitId) {
		String base = concat(type.name(), " ", refId, " not found");
		if (commitId == null)
			return base;
		return concat(base, " for commit id ", commitId);

	}

	@GET
	@Path("request/{group}/{name}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommits(repo, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Map<String, FetchRequestData> result = getData(commits, repo);
		if (result.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(result.values()));
	}

	private Map<String, FetchRequestData> getData(List<Commit> commits,
			Repository repo) {
		// iterate over all commits, only latest version will "remain"
		Map<String, FetchRequestData> result = new HashMap<>();
		for (Commit commit : commits) {
			List<Dataset> descriptors = getDatasets(repo, commit.id, null);
			for (Dataset descriptor : descriptors) {
				ModelType type = descriptor.type;
				String refId = descriptor.refId;
				String key = concat(type.name(), "_", refId);
				FetchRequestData value = service.toRequestData(repo, commit.id,
						descriptor);
				result.put(key, value);
			}
		}
		return result;
	}

	@POST
	@Path("{group}/{name}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId,
			List<Dataset> requested) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommits(repo, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		StreamingOutput data = prepareFetch(requested, commits, repo);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		List<Dataset> datasets = historyService.getReferences(repo, commitId);
		if (datasets.size() == 0) {
			// if size is 0, commit was not found (no commit without files)
			String message = concat("Commit with id ", commitId, " not found");
			return Respond.notFound(message);
		}
		List<FetchRequestData> resultData = new ArrayList<>();
		for (Dataset dataset : datasets) {
			FetchRequestData value = service.toRequestData(repo, commitId,
					dataset);
			resultData.add(value);
		}
		return Respond.ok(resultData);
	}

	private StreamingOutput prepareFetch(List<Dataset> requested,
			List<Commit> commits, Repository repo) {
		FetchWriter writer = new FetchWriter(null);
		Map<String, DescriptorAndCommitId> datasets = getNewestVersions(
				commits, repo, requested);
		try {
			for (DescriptorAndCommitId value : datasets.values())
				put(writer, repo, value);
			writer.setCommitId(commits.get(commits.size() - 1).id);
			writer.close();
			return toStream(writer.getFile());
		} catch (IOException e) {
			log.error("Error closing fetch writer", e);
			return null;
		}
	}

	private void put(FetchWriter writer, Repository repo,
			DescriptorAndCommitId value) throws IOException {
		Dataset dataset = value.dataset;
		String data = service.getDataset(repo, dataset.type, dataset.refId,
				value.commitId);
		File binDir = service.getBinDir(repo, dataset.type, dataset.refId,
				value.commitId);
		writer.put(dataset, data, binDir);
	}

	private Map<String, DescriptorAndCommitId> getNewestVersions(
			List<Commit> commits, Repository repo, List<Dataset> requested) {
		Map<String, DescriptorAndCommitId> result = new HashMap<>();
		// iterate over all commits, only latest version will "remain"
		for (Commit commit : commits) {
			List<Dataset> datasets = getDatasets(repo, commit.id, requested);
			for (Dataset dataset : datasets) {
				String key = toKey(dataset);
				DescriptorAndCommitId value = new DescriptorAndCommitId(
						dataset, commit.id);
				result.put(key, value);
			}
		}
		return result;
	}

	private List<Dataset> getDatasets(Repository repo, String commitId,
			List<Dataset> requested) {
		List<Dataset> refs = historyService.getReferences(repo, commitId);
		List<Dataset> datasets = new ArrayList<Dataset>();
		for (Dataset dataset : refs)
			if (requested == null || requested.contains(dataset))
				datasets.add(dataset);
		return datasets;
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
		return concat(reference.type.name(), "_", reference.refId);
	}

	private class DescriptorAndCommitId {

		private Dataset dataset;
		private String commitId;

		private DescriptorAndCommitId(Dataset dataset, String commitId) {
			this.dataset = dataset;
			this.commitId = commitId;
		}

	}

}
