package com.greendelta.cloud.webservice;

import static org.openlca.cloud.util.Strings.concat;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;

@Path("history")
public class HistoryResource {

	private HistoryService historyService;

	@Inject
	public HistoryResource(HistoryService historyService,
			FetchService fetchService) {
		this.historyService = historyService;
	}

	@GET
	@Path("{repoOwner}/{repoName}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(@PathParam("repoOwner") String repoOwner,
			@PathParam("repoName") String repoName,
			@PathParam("lastCommitId") String lastCommitId) {
		String repoId = concat(repoOwner, "/", repoName);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommits(repoId, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Collections.reverse(commits);
		return Respond.ok(commits);
	}

}
