package com.greendelta.collaboration.webservice.user;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.webservice.Respond;

@Path("checkout")
public class CheckoutResource {

	private final RepositoryService repoService;
	private final FetchService fetchService;
	private final HistoryService historyService;

	@Inject
	public CheckoutResource(RepositoryService repoService, FetchService fetchService, HistoryService historyService) {
		this.repoService = repoService;
		this.fetchService = fetchService;
		this.historyService = historyService;
	}

	@GET
	@Path("{group}/{name}/{untilCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkout(
			@PathParam("group") String group, 
			@PathParam("name") String name,
			@PathParam("untilCommitId") String untilCommitId) {
		Repository repo = repoService.get(group, name);
		if (untilCommitId.equals("null"))
			untilCommitId = null;
		List<Commit> commits = historyService.getCommitsUntil(repo, untilCommitId);
		if (commits.isEmpty())
			return Respond.noContent();
		StreamingOutput data = fetchService.prepareData(repo, commits);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

}
