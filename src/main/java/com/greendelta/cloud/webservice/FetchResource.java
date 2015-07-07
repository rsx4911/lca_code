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
import com.greendelta.cloud.model.data.DatasetIdentifier;
import com.greendelta.cloud.model.data.FileReference;
import com.greendelta.cloud.service.repository.CommitService;
import com.greendelta.cloud.service.repository.DatasetService;
import com.greendelta.cloud.util.Strings;

@Path("repository/fetch")
public class FetchResource {

	private DatasetService datasetService;
	private CommitService commitService;

	@Inject
	public FetchResource(DatasetService datasetService) {
		this.datasetService = datasetService;
	}

	@GET
	@Path("data/{repositoryOwner}/{repositoryName}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("type") ModelType type,
			@PathParam("refId") String refId, @PathParam("commitId") String commitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		String dataset = datasetService.get(repositoryId, type, refId, commitId);
		if (dataset == null)
			return Respond.notFound(Strings.concat(type.name(), " ", refId, " not found for commit id ", commitId));
		return Respond.ok(dataset);
	}

	@GET
	@Path("request/{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommitHistory(repositoryId, latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Map<String, DatasetIdentifier> identifiers = new HashMap<>();
		DatasetIndexer indexer = datasetService.getIndexer(repositoryId);
		for (CommitDescriptor commit : commits) {
			for (FileReference reference : commitService.getModifiedFiles(repositoryId, commit.getId())) {
				String key = reference.getType().name() + "_" + reference.getRefId();
				DatasetIdentifier value = indexer.get(reference.getType(), reference.getRefId());
				identifiers.put(key, value);
			}
		}
		if (identifiers.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(identifiers.values()));
	}

	@GET
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommitHistory(repositoryId, latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Map<String, String> data = new HashMap<>();
		for (CommitDescriptor commit : commits) {
			for (FileReference reference : commitService.getModifiedFiles(repositoryId, commit.getId())) {
				String key = reference.getType().name() + "_" + reference.getRefId();
				String value = datasetService.get(repositoryId, reference.getType(), reference.getRefId(),
						commit.getId());
				data.put(key, value);
			}
		}
		if (data.size() == 0)
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(data.values()));
	}

	@GET
	@Path("commits/{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		List<CommitDescriptor> commits = commitService.getCommitHistory(repositoryId, latestCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		return Respond.ok(commits);
	}

	@GET
	@Path("references/{repositoryOwner}/{repositoryName}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFileReferences(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("commitId") String commitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		List<FileReference> files = commitService.getModifiedFiles(repositoryId, commitId);
		// if size is 0, commit was not found (no commit without files)
		if (files.size() == 0)
			return Respond.notFound(Strings.concat("Commit with id ", commitId, " not found"));
		return Respond.ok(files);
	}
}
