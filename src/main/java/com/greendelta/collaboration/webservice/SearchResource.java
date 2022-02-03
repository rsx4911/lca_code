package com.greendelta.collaboration.webservice;

import java.util.HashMap;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.GroupService.GroupSettings;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.results.AggregationResult;

import joptsimple.internal.Strings;

@Path("public/search")
public class SearchResource {

	private static final Logger log = LogManager.getLogger(SearchResource.class);
	private final SearchService service;
	private final RepositoryService repoService;
	private final GroupService groupService;
	private final HistoryService historyService;
	private final UserService userService;
	private final SettingsService settingsService;

	@Inject
	public SearchResource(SearchService service, RepositoryService repoService, GroupService groupService,
			HistoryService historyService, UserService userService, SettingsService settingsService) {
		this.service = service;
		this.repoService = repoService;
		this.groupService = groupService;
		this.historyService = historyService;
		this.userService = userService;
		this.settingsService = settingsService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context UriInfo uriInfo) {
		Map<String, Set<String>> parameters = Client.getQueryParameters(uriInfo);
		String query = Client.removeStringFilter("query", parameters);
		int page = Client.removeIntFilter("page", parameters, 1);
		int pageSize = Client.removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		log.info("Running search for '{}', page={}, pageSize={}, parameters={}", query, page, pageSize, parameters);
		SearchResult<IndexEntry> result = service.search(query, page, pageSize, parameters);
		for (AggregationResult aResult : result.aggregations) {
			if (aResult.name.equals(Aggregations.CATEGORY.name)) {
				aResult.group("/");
			}
		}
		return Respond.ok(map(result));
	}

	private Map<String, Object> map(SearchResult<IndexEntry> result) {
		Map<String, Repository> repositories = Collections.map(repoService.getAllAccessible(), repo -> repo.toId());
		ObjectMap map = ObjectMap.fromMap(new HashMap<>());
		ObjectMap resultInfo = ObjectMap.fromObject(result.resultInfo);
		resultInfo.put("indexing", service.isReindexing());
		map.put("resultInfo", resultInfo);
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		List<ObjectMap> data = Client.map(result.data, r -> {
			ObjectMap rMap = ObjectMap.fromObject(r);
			Repository repo = repositories.get(r.repositoryId);
			rMap.put("repositoryLabel", repo.getLabel());
			if (!loggedIn) {
				rMap.nullify("commitId", "commitMessage", "commitTimestamp", "action");
			}
			return rMap;
		});
		map.put("data", data);
		List<AggregationResult> aggregations = Collections.filter(result.aggregations, a -> {
			if (!settingsService.is(Key.REPOSITORY_TAGS_ENABLED) && a.name.equals(Aggregations.REPOSITORY_TAGS.name))
				return true;
			if (!settingsService.is(Key.DATASET_TAGS_ENABLED) && a.name.equals(Aggregations.DATASET_TAGS.name))
				return true;
			return false;
		});
		map.put("aggregations", Client.map(aggregations, a -> {
			ObjectMap aMap = ObjectMap.fromObject(a);
			if (a.name.equals(Aggregations.REPOSITORY.name)) {
				aMap.put("entries", Client.map(a.entries, e -> {
					ObjectMap eMap = ObjectMap.fromObject(e);
					Repository repo = repositories.get(e.key);
					eMap.put("label", Strings.isNullOrEmpty(repo.settings.label) ? repo.name : repo.settings.label);
					return eMap;
				}));
			} else if (a.name.equals(Aggregations.GROUP.name)) {
				aMap.put("entries", Client.map(a.entries, e -> {
					ObjectMap eMap = ObjectMap.fromObject(e);
					GroupSettings groupSettings = groupService.getSettings(e.key);
					eMap.put("label", Strings.isNullOrEmpty(groupSettings.label) ? e.key : groupSettings.label );
					return eMap;
				}));
			}
			return aMap;
		}));
		return map;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("flowLinks/{flowRefId}")
	public Response searchFlowLinks(
			@PathParam("flowRefId") String flowRefId,
			@QueryParam("commitId") String commitId,
			@QueryParam("repositoryId") String repositoryId,
			@QueryParam("direction") String direction,
			@QueryParam("filter") String filter,
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		SearchQueryBuilder builder = new SearchQueryBuilder();
		Repository repo = repoService.get(repositoryId);
		builder.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repo.toId()));
		putDefaultFilter(builder, page, pageSize, filter);
		putFlowFilter(builder, repo, flowRefId, commitId, direction);
		SearchResult<IndexEntry> result = service.search(builder.build());
		return Respond.ok(result);
	}

	private void putFlowFilter(SearchQueryBuilder builder, Repository repo, String refId, String commitId,
			String direction) {
		SearchFilterValue value = SearchFilterValue.term(refId);
		if ("in".equals(direction)) {
			builder.filter("inputs", value);
		} else if ("out".equals(direction)) {
			builder.filter("outputs", value);
		} else {
			builder.filter(new String[] { "inputs", "outputs" }, value);
		}
		if (commitId == null) {
			builder.filter("mostRecent", SearchFilterValue.term(true));
			return;
		}
		Commit commit = historyService.getCommit(repo, commitId);
		if (commit == null)
			return;
		builder.filter("commits", SearchFilterValue.term(commitId));
	}

	private void putDefaultFilter(SearchQueryBuilder builder, int page, int pageSize, String filter) {
		if (!Strings.isNullOrEmpty(filter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		builder.page(page);
		builder.pageSize(pageSize);
	}

}
