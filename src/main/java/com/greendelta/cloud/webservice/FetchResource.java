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
import org.openlca.cloud.model.data.CommitDescriptor;
import org.openlca.cloud.model.data.DatasetDescriptor;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.cloud.util.Strings;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.cloud.service.CommitService;

@Path("repository/fetch")
public class FetchResource {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private CommitService commitService;

	@Inject
	public FetchResource(CommitService commitService) {
		this.commitService = commitService;
	}

	@GET
	@Path("data/{repositoryOwner}/{repositoryName}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(
			@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (commitId.equals("null")) {
			CommitDescriptor commit = commitService.getLatestCommitForDataset(
					repositoryId, type, refId);
			if (commit != null)
				commitId = commit.getId();
		}
		if (commitId == null)
			return Respond.notFound(Strings.concat(type.name(), " ", refId,
					" not found"));
		String dataset = commitService.getDataset(repositoryId, type, refId,
				commitId);
		if (dataset == null)
			return Respond.notFound(Strings.concat(type.name(), " ", refId,
					" not found for commit id ", commitId));
		return Respond.ok(dataset);
	}

	@GET
	@Path("request/{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(
			@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommits(repositoryId,
				latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Map<String, FetchRequestData> result = new HashMap<>();
		for (CommitDescriptor commit : commits) {
			List<DatasetDescriptor> descriptors = commitService.getReferences(
					repositoryId, commit.getId());
			for (DatasetDescriptor descriptor : descriptors) {
				String key = descriptor.getType().name() + "_"
						+ descriptor.getRefId();
				FetchRequestData value = commitService.toRequestData(
						repositoryId, commit.getId(), descriptor);
				result.put(key, value);
			}
		}
		if (result.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(result.values()));
	}

	@POST
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("latestCommitId") String latestCommitId,
			List<DatasetDescriptor> requested) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommits(repositoryId,
				latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		StreamingOutput data = prepareFetch(requested, commits, repositoryId);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

	private StreamingOutput prepareFetch(List<DatasetDescriptor> requested,
			List<CommitDescriptor> commits, String repositoryId) {
		FetchWriter writer = new FetchWriter(null);
		Map<String, DescriptorAndCommitId> descriptors = getNewestVersions(
				commits, repositoryId, requested);
		for (DescriptorAndCommitId value : descriptors.values()) {
			DatasetDescriptor descriptor = value.descriptor;
			String data = commitService
					.getDataset(repositoryId, descriptor.getType(),
							descriptor.getRefId(), value.commitId);
			writer.put(descriptor, data);
		}
		writer.setCommitId(commits.get(commits.size() - 1).getId());
		try {
			writer.close();
			return toStream(writer.getFile());
		} catch (IOException e) {
			log.error("Error closing fetch writer", e);
			return null;
		}
	}

	private Map<String, DescriptorAndCommitId> getNewestVersions(
			List<CommitDescriptor> commits, String repositoryId,
			List<DatasetDescriptor> requested) {
		Map<String, DescriptorAndCommitId> result = new HashMap<>();
		// iterate over all commits, only latest version will "remain"
		for (CommitDescriptor commit : commits) {
			List<DatasetDescriptor> descriptors = commitService.getReferences(
					repositoryId, commit.getId());
			for (DatasetDescriptor descriptor : descriptors) {
				if (!requested.contains(descriptor))
					continue;
				String key = toKey(descriptor);
				result.put(key,
						new DescriptorAndCommitId(descriptor, commit.getId()));
			}
		}
		return result;
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
		return reference.getType().name() + "_" + reference.getRefId();
	}

	private class DescriptorAndCommitId {

		private DatasetDescriptor descriptor;
		private String commitId;

		private DescriptorAndCommitId(DatasetDescriptor descriptor,
				String commitId) {
			this.descriptor = descriptor;
			this.commitId = commitId;
		}

	}

}
