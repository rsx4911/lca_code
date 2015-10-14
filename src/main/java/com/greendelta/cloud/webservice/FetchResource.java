package com.greendelta.cloud.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndexer;
import com.greendelta.cloud.model.data.CommitDescriptor;
import com.greendelta.cloud.model.data.DatasetDescriptor;
import com.greendelta.cloud.model.data.FetchData;
import com.greendelta.cloud.model.data.FetchRequestData;
import com.greendelta.cloud.model.data.FetchResponse;
import com.greendelta.cloud.model.data.FileReference;
import com.greendelta.cloud.service.repository.CommitService;
import com.greendelta.cloud.service.repository.DatasetService;
import com.greendelta.cloud.util.Strings;

@Path("repository/fetch")
public class FetchResource {

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
		DatasetIndexer indexer = datasetService.getIndexer(repositoryId);
		for (CommitDescriptor commit : commits) {
			List<FileReference> references = commitService.getModifiedFiles(
					repositoryId, commit.getId());
			for (FileReference reference : references) {
				String key = reference.getType().name() + "_"
						+ reference.getRefId();
				DatasetDescriptor descriptor = indexer.get(reference.getType(),
						reference.getRefId());
				FetchRequestData value = new FetchRequestData(descriptor);
				String data = datasetService.get(repositoryId,
						descriptor.getType(), descriptor.getRefId(),
						commit.getId());
				value.setDeleted(data == null || data.isEmpty());
				descriptors.put(key, value);
			}
		}
		if (descriptors.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(descriptors.values()));
	}

	@GET
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("repositoryOwner") String repositoryOwner,
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
		Map<String, FetchData> descriptors = new HashMap<>();
		DatasetIndexer indexer = datasetService.getIndexer(repositoryId);
		for (CommitDescriptor commit : commits) {
			List<FileReference> references = commitService.getModifiedFiles(
					repositoryId, commit.getId());
			for (FileReference reference : references) {
				String key = reference.getType().name() + "_"
						+ reference.getRefId();
				DatasetDescriptor descriptor = indexer.get(reference.getType(),
						reference.getRefId());
				FetchData value = new FetchData(descriptor);
				String data = datasetService.get(repositoryId,
						descriptor.getType(), descriptor.getRefId(),
						commit.getId());
				value.setJson(data);
				descriptors.put(key, value);
			}
		}
		if (descriptors.size() == 0)
			return Respond.noContent();
		FetchResponse result = new FetchResponse();
		result.setData(new ArrayList<>(descriptors.values()));
		result.setLatestCommitId(commits.get(commits.size() - 1).getId());
		return Respond.ok(result);
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
		return Respond.ok(commits);
	}

	@GET
	@Path("references/{repositoryOwner}/{repositoryName}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFileReferences(
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
		return Respond.ok(files);
	}
}
