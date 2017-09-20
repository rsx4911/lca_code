package com.greendelta.collaboration.webservice.user;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.search.SearchQuery;
import com.greendelta.collaboration.search.SearchQueryBuilder;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.webservice.Respond;

@Path("search")
public class SearchResource {

	private final SearchService service;
	private final RepositoryService repoService;

	@Inject
	public SearchResource(SearchService service, RepositoryService repoService) {
		this.service = service;
		this.repoService = repoService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("query") @DefaultValue("") String query,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("type") ModelType type) {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.query(query)
				.page(page - 1)
				.pageSize(SearchQuery.DEFAULT_PAGE_SIZE);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		for (Repository repo : repoService.getAllAccessible()) {
			builder.aggregation(Aggregations.REPOSITORY, repo.toId());
		}
		return Respond.ok(service.search(builder.build()));
	}

}
