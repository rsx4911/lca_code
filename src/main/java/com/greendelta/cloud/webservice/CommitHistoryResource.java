package com.greendelta.cloud.webservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.CommitDescriptor;
import org.openlca.cloud.model.data.DatasetDescriptor;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.cloud.util.Strings;

import com.google.inject.Inject;
import com.greendelta.cloud.service.CommitService;

@Path("repository/commits")
public class CommitHistoryResource {

	private CommitService commitService;

	@Inject
	public CommitHistoryResource(CommitService commitService) {
		this.commitService = commitService;
	}

	@GET
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(
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
		List<DatasetDescriptor> descriptors = commitService.getReferences(
				repositoryId, commitId);
		// if size is 0, commit was not found (no commit without files)
		if (descriptors.size() == 0)
			return Respond.notFound(Strings.concat("Commit with id ", commitId,
					" not found"));
		List<FetchRequestData> resultData = new ArrayList<>();
		for (DatasetDescriptor descriptor : descriptors) {
			FetchRequestData value = commitService.toRequestData(repositoryId,
					commitId, descriptor);
			resultData.add(value);
		}
		return Respond.ok(resultData);
	}

}
