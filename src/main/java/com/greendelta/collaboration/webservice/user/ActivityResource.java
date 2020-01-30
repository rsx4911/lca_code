package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.search.wrapper.SearchResult;

@Path("activities")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityResource {

	private final RepositoryService repoService;
	private final HistoryService historyService;

	@Inject
	public ActivityResource(RepositoryService repoService, HistoryService historyService) {
		this.repoService = repoService;
		this.historyService = historyService;
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		List<Repository> repositories = repoService.getAllAccessible();
		List<Commit> commits = new ArrayList<>();
		repositories.forEach(repo -> commits.addAll(historyService.getCommits(repo)));
		Collections.sort(commits, (c1, c2) -> Long.compare(c2.timestamp, c1.timestamp));
		SearchResult<Commit> result = SearchResults.paged(page, pageSize, commits);
		return Respond.ok(result);
	}

}
