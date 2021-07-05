package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
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

import com.google.inject.Inject;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.DsRepo;
import com.greendelta.collaboration.service.search.DsEntry;
import com.greendelta.collaboration.service.search.DsVersion;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.results.AggregationResult;

@Path("public/search")
public class SearchResource {

	private static final Logger log = LogManager.getLogger(SearchResource.class);
	private final SearchService service;
	private final RepositoryService repoService;
	private final GroupService groupService;
	private final UserService userService;
	private final SettingsService settingsService;

	@Inject
	public SearchResource(SearchService service, RepositoryService repoService, GroupService groupService,
			UserService userService, SettingsService settingsService) {
		this.service = service;
		this.repoService = repoService;
		this.groupService = groupService;
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
		SearchResult<DsEntry> result = service.search(query, page, pageSize, parameters);
		for (AggregationResult aResult : result.aggregations) {
			if (aResult.name.equals(Aggregations.CATEGORY.name)) {
				aResult.group("/");
			}
		}
		return Respond.ok(map(result));
	}

	private Map<String, Object> map(SearchResult<DsEntry> result) {
		Map<String, Repository> repositories = Collections.map(repoService.getAllAccessible(), repo -> repo.toId());
		ObjectMap map = ObjectMap.fromMap(new HashMap<>());
		map.put("resultInfo", result.resultInfo);
		boolean loggedIn = userService.getCurrentUser().id != 0;
		List<ObjectMap> data = Client.map(result.data, dsEntry -> {
			ObjectMap e = ObjectMap.fromObject(dsEntry);
			List<ObjectMap> versions = new ArrayList<>();
			for (DsVersion dsVersion : dsEntry.versions) {
				ObjectMap v = ObjectMap.fromObject(dsVersion);
				List<ObjectMap> repos = new ArrayList<>();
				for (DsRepo dsRepo : dsVersion.repos) {
					ObjectMap r = ObjectMap.fromObject(dsRepo);
					Repository repo = repositories.get(dsRepo.id);
					r.put("repositoryLabel", repo.getLabel());
					if (!loggedIn) {
						r.nullify("commitId", "commitMessage");
					}
					repos.add(r);
				}
				v.put("repos", repos);
				versions.add(v);
			}
			e.put("versions", versions);
			return e;
		});
		map.put("data", data);
		List<AggregationResult> aggregations = Collections.filter(result.aggregations, a -> {
			if (!settingsService.is(ServerSetting.REPOSITORY_TAGS_ENABLED)
					&& a.name.equals(Aggregations.REPOSITORY_TAGS.name))
				return true;
			if (!settingsService.is(ServerSetting.DATASET_TAGS_ENABLED)
					&& a.name.equals(Aggregations.DATASET_TAGS.name))
				return true;
			return false;
		});
		map.put("aggregations", Client.map(aggregations, a -> {
			ObjectMap aMap = ObjectMap.fromObject(a);
			if (a.name.equals(Aggregations.REPOSITORY.name)) {
				aMap.put("entries", Client.map(a.entries, e -> {
					ObjectMap eMap = ObjectMap.fromObject(e);
					Repository repo = repositories.get(e.key);
					eMap.put("label", repo.settings.get(RepositorySetting.LABEL, repo.name));
					return eMap;
				}));
			} else if (a.name.equals(Aggregations.GROUP.name)) {
				aMap.put("entries", Client.map(a.entries, e -> {
					ObjectMap eMap = ObjectMap.fromObject(e);
					String label = groupService.getSettings(e.key).get(GroupSetting.LABEL, e.key);
					eMap.put("label", label);
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
		// TODO
		return Respond.ok(SearchResults.pagedAndFiltered(page, pageSize, filter, new ArrayList<>()));
	}

}
