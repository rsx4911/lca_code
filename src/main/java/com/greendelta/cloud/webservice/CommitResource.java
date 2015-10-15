package com.greendelta.cloud.webservice;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.model.data.CommitDescriptor;
import com.greendelta.cloud.service.repository.CommitService;
import com.greendelta.cloud.util.Strings;

@Path("repository/commit")
public class CommitResource {

	private CommitService commitService;

	@Inject
	public CommitResource(CommitService commitService) {
		this.commitService = commitService;
	}

	@GET
	@Path("request/{repositoryOwner}/{repositoryName}/{latestCommitId}")
	public Response request(
			@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (!isUpToDate(repositoryId, latestCommitId))
			return Respond.conflict("User is out of sync");
		return Respond.ok();
	}

	private boolean isUpToDate(String repositoryId, String latestCommitId) {
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		CommitDescriptor latestCommit = commitService
				.getLatestCommit(repositoryId);
		if (latestCommit == null)
			return latestCommitId == null;
		return latestCommit.getId().equals(latestCommitId);
	}

	@POST
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response commit(
			@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName,
			@PathParam("latestCommitId") String latestCommitId,
			InputStream commitData) {
		String repositoryId = Strings.concat(repositoryOwner, "/",
				repositoryName);
		if (!isUpToDate(repositoryId, latestCommitId))
			return Respond.conflict("User is out of sync");
		String commitId = commitService.push(repositoryId, commitData);
		return Respond.created(commitId);
	}

}
