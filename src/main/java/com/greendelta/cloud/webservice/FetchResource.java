package com.greendelta.cloud.webservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
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
import com.greendelta.cloud.index.DatasetIndexer;
import com.greendelta.cloud.service.repository.CommitService;
import com.greendelta.cloud.service.repository.DatasetService;

@Path("repository/fetch")
public class FetchResource {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private DatasetService datasetService;
	private CommitService commitService;

	@Inject
	public FetchResource(DatasetService datasetService,
			CommitService commitService) {
		this.datasetService = datasetService;
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
		if (commitId.equals("null"))
			commitId = commitService.getLatestCommitId(repositoryId, type,
					refId);
		if (commitId == null)
			return Respond.notFound(Strings.concat(type.name(), " ", refId,
					" not found"));
		String dataset = datasetService
				.get(repositoryId, type, refId, commitId);
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
		List<CommitDescriptor> commits = commitService.getCommitHistory(
				repositoryId, latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Map<String, FetchRequestData> descriptors = new HashMap<>();
		for (CommitDescriptor commit : commits) {
			List<FileReference> references = commitService.getModifiedFiles(
					repositoryId, commit.getId());
			for (FileReference reference : references) {
				String key = reference.getType().name() + "_"
						+ reference.getRefId();
				descriptors.put(key,
						toRequestData(repositoryId, commit.getId(), reference));
			}
		}
		if (descriptors.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(descriptors.values()));
	}

	@POST
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("latestCommitId") String latestCommitId, List<DatasetDescriptor> requested) {
		if (requested.isEmpty())
			return Respond.noContent();
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommitHistory(
				repositoryId, latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		StreamingOutput data = prepareFetch(requested, commits, repositoryId);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

	private StreamingOutput prepareFetch(List<DatasetDescriptor> requested, List<CommitDescriptor> commits,
			String repositoryId) {
		FetchWriter writer = new FetchWriter(null);
		Map<String, DescriptorAndCommitId> descriptors = getNewestVersions(
				commits, repositoryId);
		for (DescriptorAndCommitId value : descriptors.values()) {
			DatasetDescriptor descriptor = value.descriptor;
			String data = datasetService
					.get(repositoryId, descriptor.getType(),
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
			List<CommitDescriptor> commits, String repositoryId) {
		DatasetIndexer indexer = datasetService.getIndexer(repositoryId);
		Map<String, DescriptorAndCommitId> descriptors = new HashMap<>();
		// iterate over all commits, only latest version will "remain"
		for (CommitDescriptor commit : commits) {
			List<FileReference> references = commitService.getModifiedFiles(
					repositoryId, commit.getId());
			for (FileReference reference : references) {
				String key = toKey(reference);
				DatasetDescriptor descriptor = indexer.get(reference.getType(),
						reference.getRefId());
				descriptors.put(key, new DescriptorAndCommitId(descriptor,
						commit.getId()));
			}
		}
		return descriptors;
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

	@GET
	@Path("commits/{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(
			@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommitHistory(
				repositoryId, latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Collections.reverse(commits);
		return Respond.ok(commits);
	}

	@GET
	@Path("references/{repositoryOwner}/{repositoryName}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(
			@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("commitId") String commitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		List<FileReference> files = commitService.getModifiedFiles(
				repositoryId, commitId);
		// if size is 0, commit was not found (no commit without files)
		if (files.size() == 0)
			return Respond.notFound(Strings.concat("Commit with id ", commitId,
					" not found"));
		List<FetchRequestData> descriptors = new ArrayList<>();
		for (FileReference reference : files)
			descriptors.add(toRequestData(repositoryId, commitId, reference));
		return Respond.ok(descriptors);
	}

	private FetchRequestData toRequestData(String repositoryId,
			String commitId, FileReference reference) {
		DatasetIndexer indexer = datasetService.getIndexer(repositoryId);
		DatasetDescriptor descriptor = indexer.get(reference.getType(),
				reference.getRefId());
		FetchRequestData value = new FetchRequestData(descriptor);
		String data = datasetService.get(repositoryId, descriptor.getType(),
				descriptor.getRefId(), commitId);
		List<CommitDescriptor> previous = commitService
				.getCommitHistoryForDataset(repositoryId, reference.getType(),
						reference.getRefId(), commitId);
		boolean wasAdded = previous.isEmpty();
		if (!wasAdded) {
			CommitDescriptor commit = previous.get(previous.size() - 1);
			String previousData = datasetService
					.get(repositoryId, descriptor.getType(),
							descriptor.getRefId(), commit.getId());
			wasAdded = previousData == null || previousData.isEmpty();
		}
		value.setDeleted(data == null || data.isEmpty());
		value.setAdded(wasAdded);
		return value;
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
