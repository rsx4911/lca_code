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
import com.greendelta.cloud.service.CommitService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.NotificationService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("commit")
public class CommitResource {

	private final CommitService service;
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final NotificationService notificationService;

	@Inject
	public CommitResource(CommitService service, RepositoryService repoService, HistoryService historyService,
			NotificationService notificationService) {
		this.service = service;
		this.repoService = repoService;
		this.historyService = historyService;
		this.notificationService = notificationService;
	}

	@GET
	@Path("request/{group}/{name}/{lastCommitId}")
	public Response request(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (!isUpToDate(repo, lastCommitId))
			return Respond.conflict("User is out of sync");
		return Respond.ok();
	}

	private boolean isUpToDate(Repository repo, String lastCommitId) {
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		Commit lastCommit = historyService.getLastCommit(repo);
		if (lastCommit == null)
			return lastCommitId == null;
		return lastCommit.id.equals(lastCommitId);
	}

	@POST
	@Path("{group}/{name}/{lastCommitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response commit(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId,
			InputStream commitData) {
		Repository repo = repoService.get(group, name);
		if (!isUpToDate(repo, lastCommitId))
			return Respond.conflict("User is out of sync");
		String commitId = service.put(repo, commitData);
		Commit commit = historyService.getCommit(repo, commitId);
		notificationService.dataCommitted(repo, commit).send();
		return Respond.created(commitId);
	}

}
