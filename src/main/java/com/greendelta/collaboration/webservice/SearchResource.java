package com.greendelta.collaboration.webservice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import joptsimple.internal.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

@Path("public/search")
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
	public Response search(@Context UriInfo uriInfo) {
		Map<String, Set<String>> parameters = Client.getQueryParameters(uriInfo);
		String query = Client.removeStringFilter("query", parameters);
		int page = Client.removeIntFilter("page", parameters, 1);
		int pageSize = Client.removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		return Respond.ok(service.search(query, page, pageSize, parameters));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("flowLinks/{flowRefId}")
	public Response searchFlowLinks(
			@PathParam("flowRefId") String flowRefId,
			@QueryParam("repositoryId") String repositoryId,
			@QueryParam("direction") String direction,
			@QueryParam("filter") String filter,
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		SearchQueryBuilder builder = new SearchQueryBuilder();
		boolean hasAccess = putRepositories(builder, repositoryId);
		if (!hasAccess)
			return Respond.ok(buildEmptyResult(page, pageSize));
		putDefaultFilter(builder, page, pageSize, filter);
		putFlowFilter(builder, flowRefId, direction);
		return Respond.ok(service.search(builder.build()));
	}

	private boolean putRepositories(SearchQueryBuilder builder, String repositoryId) {
		List<Repository> repos = null;
		if (Strings.isNullOrEmpty(repositoryId)) {
			repos = repoService.getAllAccessible();
		} else {
			String[] repo = repositoryId.split("/");
			repos = Collections.singletonList(repoService.get(repo[0], repo[1]));
		}
		if (repos.isEmpty())
			return false;
		for (Repository repo : repos) {
			builder.aggregation(Aggregations.REPOSITORY, repo.toId());
		}
		return true;
	}

	private void putFlowFilter(SearchQueryBuilder builder, String refId, String direction) {
		SearchFilterValue value = SearchFilterValue.phrase(refId);
		if ("in".equals(direction)) {
			builder.filter("inputs", value);
		} else if ("out".equals(direction)) {
			builder.filter("outputs", value);
		} else {
			builder.filter(new String[] { "inputs", "outputs" }, value);
		}
	}

	private void putDefaultFilter(SearchQueryBuilder builder, int page, int pageSize, String filter) {
		if (!Strings.isNullOrEmpty(filter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		builder.page(page);
		builder.pageSize(pageSize);
	}

	private SearchResult<IndexEntry> buildEmptyResult(int page, int pageSize) {
		SearchResult<IndexEntry> result = new SearchResult<>();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = pageSize;
		return result;
	}

}
