package com.greendelta.collaboration.webservice.user;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.CommitService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.webservice.Respond;

@Path("commit")
public class CommitResource {

	private final CommitService service;
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final NotificationService notificationService;
	private final AccessService accessService;

	@Inject
	public CommitResource(CommitService service, RepositoryService repoService, HistoryService historyService,
			NotificationService notificationService, AccessService accessService) {
		this.service = service;
		this.repoService = repoService;
		this.historyService = historyService;
		this.notificationService = notificationService;
		this.accessService = accessService;
	}

	@GET
	@Path("request/{group}/{name}")
	public Response request(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (!accessService.canWrite(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "WRITE");
		if (repo.settings.prohibitCommits)
			throw new UnauthorizedAccessException(repo.toId(), "COMMIT");
		if (!isUpToDate(repo, lastCommitId))
			return Respond.conflict("User is out of sync");
		return Respond.ok();
	}

	private boolean isUpToDate(Repository repo, String lastCommitId) {
		Commit lastCommit = historyService.getLastCommit(repo);
		if (lastCommit == null)
			return lastCommitId == null;
		return lastCommit.id.equals(lastCommitId);
	}

	@POST
	@Path("{group}/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response commit(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("lastCommitId") String lastCommitId,
			InputStream commitData) {
		Repository repo = repoService.get(group, name);
		if (!accessService.canWrite(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "WRITE");
		if (repo.settings.prohibitCommits)
			throw new UnauthorizedAccessException(repo.toId(), "COMMIT");
		if (!isUpToDate(repo, lastCommitId))
			return Respond.conflict("User is out of sync");
		Commit commit = service.put(repo, commitData);
		if (commit == null)
			return Respond.error("Unknown error handling commit data");
		notificationService.dataCommitted(repo, commit).send();
		return Respond.created(commit.id);
	}

}
