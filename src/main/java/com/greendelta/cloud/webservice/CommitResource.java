package com.greendelta.cloud.webservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.cloud.model.data.Commit;
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
	@Consumes(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("latestCommitId") String latestCommitId) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		if (!isUpToDate(repositoryId, latestCommitId))
			return Respond.conflict("User is out of sync");
		return Respond.ok();
	}

	private boolean isUpToDate(String repositoryId, String latestCommitId) {
		if (latestCommitId.equals("null"))
			latestCommitId = null;
		CommitDescriptor latestCommit = commitService.getLatestCommit(repositoryId);
		if (latestCommit == null)
			return latestCommitId == null;
		return latestCommit.getId().equals(latestCommitId);
	}

	@POST
	@Path("{repositoryOwner}/{repositoryName}/{latestCommitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response commit(@PathParam("repositoryOwner") String repositoryOwner,
			@PathParam("repositoryName") String repositoryName, @PathParam("latestCommitId") String latestCommitId,
			Commit commit) {
		String repositoryId = Strings.concat(repositoryOwner, "/", repositoryName);
		if (!isUpToDate(repositoryId, latestCommitId))
			return Respond.conflict("User is out of sync");
		String commitId = commitService.push(repositoryId, commit);
		return Respond.created(commitId);
	}

}
