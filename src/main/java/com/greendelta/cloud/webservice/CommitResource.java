package com.greendelta.cloud.webservice;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.CommitService;

import static org.openlca.cloud.util.Strings.concat;

@Path("commit")
public class CommitResource {

	private CommitService commitService;
	private HistoryService historyService;

	@Inject
	public CommitResource(CommitService commitService,
			HistoryService historyService) {
		this.commitService = commitService;
		this.historyService = historyService;
	}

	@GET
	@Path("request/{repoOwner}/{repoName}/{lastCommitId}")
	public Response request(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("lastCommitId") String lastCommitId) {
		String repoId = concat(repoOwner, "/", repoName);
		if (!isUpToDate(repoId, lastCommitId))
			return Respond.conflict("User is out of sync");
		return Respond.ok();
	}

	private boolean isUpToDate(String repoId, String lastCommitId) {
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		Commit lastCommit = historyService.getLastCommit(repoId);
		if (lastCommit == null)
			return lastCommitId == null;
		return lastCommit.getId().equals(lastCommitId);
	}

	@POST
	@Path("{repoOwner}/{repoName}/{lastCommitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response commit(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("lastCommitId") String lastCommitId,
			InputStream commitData) {
		String repoId = concat(repoOwner, "/", repoName);
		if (!isUpToDate(repoId, lastCommitId))
			return Respond.conflict("User is out of sync");
		String commitId = commitService.put(repoId, commitData);
		return Respond.created(commitId);
	}

}
