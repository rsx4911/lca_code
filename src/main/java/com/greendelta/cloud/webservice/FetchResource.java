package com.greendelta.cloud.webservice;

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
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.FetchService;

import static org.openlca.cloud.util.Strings.concat;

@Path("fetch")
public class FetchResource {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private FetchService fetchService;
	private HistoryService historyService;

	@Inject
	public FetchResource(FetchService fetchService,
			HistoryService historyService) {
		this.fetchService = fetchService;
		this.historyService = historyService;
	}

	@GET
	@Path("data/{repoOwner}/{repoName}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		String repoId = concat(repoOwner, "/", repoName);
		if (commitId.equals("null"))
			commitId = getLastCommitId(repoId, type, refId);
		if (commitId == null) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		String dataset = fetchService.getDataset(repoId, type, refId, commitId);
		if (dataset == null) {
			String message = notFoundMessage(type, refId, commitId);
			return Respond.notFound(message);
		}
		return Respond.ok(dataset);
	}

	private String getLastCommitId(String repoId, ModelType type, String refId) {
		Commit commit = historyService.getLastCommit(repoId, type, refId);
		if (commit == null)
			return null;
		return commit.getId();
	}

	private String notFoundMessage(ModelType type, String refId, String commitId) {
		String base = concat(type.name(), " ", refId, " not found");
		if (commitId == null)
			return base;
		return concat(base, " for commit id ", commitId);

	}

	@GET
	@Path("request/{repoOwner}/{repoName}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("lastCommitId") String lastCommitId) {
		String repoId = concat(repoOwner, "/", repoName);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommits(repoId, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Map<String, FetchRequestData> result = getData(commits, repoId);
		if (result.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(result.values()));
	}

	private Map<String, FetchRequestData> getData(List<Commit> commits,
			String repoId) {
		// iterate over all commits, only latest version will "remain"
		Map<String, FetchRequestData> result = new HashMap<>();
		for (Commit commit : commits) {
			List<Dataset> descriptors = getDatasets(repoId, commit.getId(),
					null);
			for (Dataset descriptor : descriptors) {
				ModelType type = descriptor.getType();
				String refId = descriptor.getRefId();
				String key = concat(type.name(), "_", refId);
				FetchRequestData value = fetchService.toRequestData(repoId,
						commit.getId(), descriptor);
				result.put(key, value);
			}
		}
		return result;
	}

	@POST
	@Path("{repoOwner}/{repoName}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("lastCommitId") String lastCommitId,
			List<Dataset> requested) {
		String repoId = concat(repoOwner, "/", repoName);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommits(repoId, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		StreamingOutput data = prepareFetch(requested, commits, repoId);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

	@GET
	@Path("references/{repoOwner}/{repoName}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("commitId") String commitId) {
		String repoId = concat(repoOwner, "/", repoName);
		List<Dataset> datasets = historyService.getReferences(repoId, commitId);
		if (datasets.size() == 0) {
			// if size is 0, commit was not found (no commit without files)
			String message = concat("Commit with id ", commitId, " not found");
			return Respond.notFound(message);
		}
		List<FetchRequestData> resultData = new ArrayList<>();
		for (Dataset dataset : datasets) {
			FetchRequestData value = fetchService.toRequestData(repoId,
					commitId, dataset);
			resultData.add(value);
		}
		return Respond.ok(resultData);
	}

	private StreamingOutput prepareFetch(List<Dataset> requested,
			List<Commit> commits, String repoId) {
		FetchWriter writer = new FetchWriter(null);
		Map<String, DescriptorAndCommitId> datasets = getNewestVersions(
				commits, repoId, requested);
		try {
			for (DescriptorAndCommitId value : datasets.values())
				put(writer, repoId, value);
			writer.setCommitId(commits.get(commits.size() - 1).getId());
			writer.close();
			return toStream(writer.getFile());
		} catch (IOException e) {
			log.error("Error closing fetch writer", e);
			return null;
		}
	}

	private void put(FetchWriter writer, String repoId,
			DescriptorAndCommitId value) throws IOException {
		Dataset dataset = value.dataset;
		String data = fetchService.getDataset(repoId, dataset.getType(),
				dataset.getRefId(), value.commitId);
		File binDir = fetchService.getBinDir(repoId, dataset.getType(),
				dataset.getRefId(), value.commitId);
		writer.put(dataset, data, binDir);
	}

	private Map<String, DescriptorAndCommitId> getNewestVersions(
			List<Commit> commits, String repoId, List<Dataset> requested) {
		Map<String, DescriptorAndCommitId> result = new HashMap<>();
		// iterate over all commits, only latest version will "remain"
		for (Commit commit : commits) {
			List<Dataset> datasets = getDatasets(repoId, commit.getId(),
					requested);
			for (Dataset dataset : datasets) {
				String key = toKey(dataset);
				DescriptorAndCommitId value = new DescriptorAndCommitId(
						dataset, commit.getId());
				result.put(key, value);
			}
		}
		return result;
	}

	private List<Dataset> getDatasets(String repoId, String commitId,
			List<Dataset> requested) {
		List<Dataset> refs = historyService.getReferences(repoId, commitId);
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
		return concat(reference.getType().name(), "_", reference.getRefId());
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
