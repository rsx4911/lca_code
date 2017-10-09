package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.lca.search.SearchClient;
import com.greendelta.lca.search.SearchFilterValue.Type;
import com.greendelta.lca.search.SearchQuery;
import com.greendelta.lca.search.SearchQueryBuilder;
import com.greendelta.lca.search.SearchResult;
import com.greendelta.lca.search.aggregations.SearchAggregation;
import com.greendelta.lca.search.aggregations.results.AggregationResult;
import com.greendelta.lca.search.aggregations.results.AggregationResultBuilder;

@Path("public/search")
public class SearchResource {

	private final RepositoryService repoService;
	private final SearchClient client;
	private final String baseUrl;

	@Inject
	public SearchResource(RepositoryService repoService, SearchClient client, @Named("base.url") String baseUrl) {
		this.repoService = repoService;
		this.client = client;
		this.baseUrl = baseUrl;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context UriInfo uriInfo) {
		Map<String, Set<String>> parameters = Client.getQueryParameters(uriInfo);
		String query = Client.removeStringFilter("query", parameters);
		int page = Client.removeIntFilter("page", parameters, 1);
		int pageSize = Client.removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		Set<String> repoIds = new HashSet<>();
		for (Repository repo : repoService.getAllAccessible()) {
			repoIds.add(repo.toId());
		}
		if (repoIds.isEmpty())
			return Respond.ok(buildEmptyResult(page, pageSize));
		parameters.put(Aggregations.MODEL_TYPE.field, Collections.singleton(ModelType.PROCESS.name()));
		parameters.put(Aggregations.REPOSITORY.field, repoIds);
		return Respond.ok(search(query, page, pageSize, parameters));
	}

	private SearchResult search(String query, int page, int pageSize, Map<String, Set<String>> parameters) {
		SearchQueryBuilder builder = new SearchQueryBuilder();
		List<SearchAggregation> aggregations = new ArrayList<>(Arrays.asList(Aggregations.PROCESS_FILTERS));
		aggregations.add(Aggregations.REPOSITORY);
		aggregations.add(Aggregations.MODEL_TYPE);
		for (SearchAggregation aggregation : aggregations) {
			Set<String> filterValues = parameters.remove(aggregation.name);
			if (filterValues != null && !filterValues.isEmpty()) {
				for (String filterValue : filterValues) {
					builder.aggregation(aggregation, filterValue);
				}
			} else {
				builder.aggregation(aggregation);
			}
		}
		for (String filter : parameters.keySet()) {
			Set<String> filterValues = parameters.get(filter);
			if (filterValues != null && !filterValues.isEmpty()) {
				String[] values = filterValues.toArray(new String[filterValues.size()]);
				builder.filter(filter, Type.WILDCART, values);
			}
		}
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(query, "name");
		}
		builder.page(page);
		builder.pageSize(pageSize);
		SearchResult result = client.search(builder.build());
		prepareResult(result);
		return result;
	}

	private void prepareResult(SearchResult result) {
		for (AggregationResult aggreagtion : new ArrayList<>(result.aggregations)) {
			if (aggreagtion.name.equals(Aggregations.REPOSITORY.name)) {
				result.aggregations.remove(aggreagtion);
			} else if (aggreagtion.name.equals(Aggregations.MODEL_TYPE.name)) {
				result.aggregations.remove(aggreagtion);
			}
		}
		for (Map<String, Object> data : result.data) {
			data.remove("type");
			data.remove("categoryType");
			data.remove("categoryRefId");
			data.remove("commitMessage");
			data.remove("lastUpdate");
			String path = data.remove("fullPath").toString();
			if (path.contains("/")) // full path contains name
				path = path.substring(0, path.lastIndexOf("/"));
			data.put("category", path);
			String repoId = data.remove("repositoryId").toString();
			String refId = data.get("refId").toString();
			String commitId = data.remove("commitId").toString();
			data.put("format", "JSON-LD");
			data.put("dataSetUrl", baseUrl + "/ws/public/browse/" + repoId + "/PROCESS/" + refId + "/" + commitId);
		}
	}

	private SearchResult buildEmptyResult(int page, int pageSize) {
		SearchResult result = new SearchResult();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = pageSize;
		for (SearchAggregation aggr : Aggregations.PROCESS_FILTERS) {
			result.aggregations.add(new AggregationResultBuilder().type(aggr.type).name(aggr.name).build());
		}
		return result;
	}
}
