package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import joptsimple.internal.Strings;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.SearchSorting;

@Path("history")
public class HistoryResource {

	private final HistoryService service;
	private final SearchService searchService;
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public HistoryResource(HistoryService service, SearchService searchService, RepositoryService repoService,
			UserService userService) {
		this.service = service;
		this.searchService = searchService;
		this.repoService = repoService;
		this.userService = userService;
	}

	@GET
	@Path("{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("filter") String filter,
			@QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = service.getCommits(repo);
		java.util.Collections.reverse(commits);
		SearchResult<Commit> result = SearchResults.pagedAndFiltered(page, pageSize, filter, commits, (c) -> c.message);
		return Respond.ok(SearchResults.convert(result, this::putUserName));

	}
	
	@GET
	@Path("{group}/{name}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = service.getCommitsAfter(repo, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = service.getCommits(repo, type, refId);
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("category/{group}/{name}/{refId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistoryForCategory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("refId") String refId) {
		Repository repo = repoService.get(group, name);
		IndexEntry first = searchService.getFirst(repo.toId(), refId);
		if (first == null)
			return Respond.noContent();
		List<Commit> commits = service.getCommitsAfter(repo, first.commitId, true);
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("commit/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommit(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		Commit commit = service.getCommit(repo, commitId);
		if (commit == null)
			return Respond.notFound();
		Map<String, Object> result = putUserName(commit);
		return Respond.ok(result);
	}

	@GET
	@Path("previousCommitId/{group}/{name}/{type}/{refId}/{commitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPreviousReference(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		if (commitId == "null")
			commitId = null;
		Commit lastCommit = service.getLastCommitBefore(repo, type, refId, commitId);
		if (lastCommit == null || lastCommit.id.equals(commitId))
			return Respond.notFound("No previous commit found for " + type.name() + " " + refId);
		return Respond.ok(lastCommit.id);
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId,
			@QueryParam("type") ModelType type,
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter) {
		Repository repo = repoService.get(group, name);
		Commit commit = service.getCommit(repo, commitId);
		if (commit == null)
			return Respond.notFound();
		SearchQuery query = createReferencesQuery(repo, commit, type, page, pageSize, filter);
		SearchResult<IndexEntry> result = searchService.search(query);
		return Respond.ok(SearchResults.convert(result, (entry) -> entry.asFetchRequestData()));
	}

	private SearchQuery createReferencesQuery(Repository repo, Commit commit, ModelType type, int page, int pageSize,
			String filter) {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.page(page)
				.pageSize(pageSize)
				.filter(Aggregations.REPOSITORY.name, SearchFilterValue.phrase(repo.toId()))
				.filter("commitId", SearchFilterValue.phrase(commit.id));
		if (!Strings.isNullOrEmpty(filter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		} else {
			for (ModelType categorized : ModelType.categorized()) {
				builder.aggregation(Aggregations.MODEL_TYPE, categorized.name());
			}
		}
		builder.sortBy("typeOrdinal", SearchSorting.DESC);
		return builder.build();
	}

	private List<Map<String, Object>> putUserName(List<Commit> commits) {
		List<Map<String, Object>> mapped = new ArrayList<>();
		for (Commit commit : commits)
			mapped.add(putUserName(commit));
		return mapped;
	}

	private Map<String, Object> putUserName(Commit commit) {
		ObjectMap map = ObjectMap.fromObject(commit);
		User user = userService.getForUsername(commit.user);
		if (user != null)
			map.put("userDisplayName", user.name);
		else
			map.put("userDisplayName", commit.user);
		return map;
	}

}
