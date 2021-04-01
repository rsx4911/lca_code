package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.index.FlowIndexEntry;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

import joptsimple.internal.Strings;

@Path("history")
@Produces(MediaType.APPLICATION_JSON)
public class HistoryResource {

	private final SearchService searchService;
	private final RepositoryService repoService;
	private final UserService userService;
	private final AccessService accessService;

	@Inject
	public HistoryResource(SearchService searchService, RepositoryService repoService, UserService userService,
			AccessService accessService) {
		this.searchService = searchService;
		this.repoService = repoService;
		this.userService = userService;
		this.accessService = accessService;
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}")
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId) {
		Repository repo = repoService.get(group, name);
		IndexEntry first = searchService.getFirst(repo, type, refId);
		if (first == null)
			return Respond.notFound();
		List<Commit> commits = repo.commits.getAfter(first.commitId, true);
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		List<Map<String, Object>> mapped = new ArrayList<>();
		List<Commit> ownCommits = repo.commits.get(type, refId);
		for (Commit commit : commits) {
			Map<String, Object> map = putUserName(commit);
			for (Commit c : ownCommits) {
				if (!commit.id.equals(c.id))
					continue;
				map.put("modelHasChanged", true);
				break;
			}
			mapped.add(map);
		}
		return Respond.ok(mapped);
	}

	@GET
	@Path("{group}/{name}")
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId != null && !lastCommitId.isEmpty()) {
			Commit commit = repo.commits.get(lastCommitId);
			if (commit == null)
				return Respond.notFound("Commit " + lastCommitId + " not found");
		}
		List<Commit> commits = repo.commits.getAfter(lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("search/{group}/{name}")
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("filter") String filter,
			@QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = repo.commits.get();
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		SearchResult<Commit> result = SearchResults.pagedAndFiltered(page, pageSize, filter, commits, (c) -> c.message);
		return Respond
				.ok(putAdditionalInfo(SearchResults.convert(result, c -> ObjectMap.fromObject(c)), repo, commits));
	}

	private Map<String, Object> putAdditionalInfo(SearchResult<ObjectMap> result, Repository repo,
			List<Commit> commits) {
		Map<String, Integer> groupCount = new HashMap<>();
		ObjectMap map = ObjectMap.fromObject(SearchResults.convert(result, this::putUserName));
		List<ObjectMap> data = new ArrayList<>();
		for (ObjectMap commit : result.data) {
			int count = 0;
			for (Commit c : commits) {
				if (!isSameDay(commit.getLong("timestamp"), c.timestamp))
					continue;
				count++;
			}
			groupCount.put(commit.getString("id"), count);
			String commitId = commit.getString("id");
			commit.put("additions", searchService.getDatasetCount(repo, commitId, IndexAction.ADD));
			commit.put("deletions", searchService.getDatasetCount(repo, commitId, IndexAction.DELETE));
			commit.put("updates", searchService.getDatasetCount(repo, commitId, IndexAction.UPDATE));
			data.add(commit);
		}
		map.put("resultInfo.groupCount", groupCount);
		map.put("data", data);
		return map;
	}

	private boolean isSameDay(long d1, long d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(d2);
		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
			return false;
		return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

	@GET
	@Path("category/{group}/{name}/{refId}")
	public Response getCommitHistoryForCategory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("refId") String refId) {
		Repository repo = repoService.get(group, name);
		IndexEntry first = searchService.getFirst(repo, ModelType.CATEGORY, refId);
		if (first == null)
			return Respond.noContent();
		List<Commit> commits = repo.commits.getAfter(first.commitId, true);
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("commit/{group}/{name}/{commitId}")
	public Response getCommit(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		Commit commit = repo.commits.get(commitId);
		if (commit == null)
			return Respond.notFound();
		Map<String, Object> map = putUserName(commit);
		map.put("additions", searchService.getDatasetCount(repo, commitId, IndexAction.ADD));
		map.put("deletions", searchService.getDatasetCount(repo, commitId, IndexAction.DELETE));
		map.put("updates", searchService.getDatasetCount(repo, commitId, IndexAction.UPDATE));
		map.put("canCreateChangeLog", accessService.canCreateChangeLog(repo.toId()));
		return Respond.ok(map);
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	public Response getReferences(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId,
			@QueryParam("type") ModelType type,
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter) {
		Repository repo = repoService.get(group, name);
		Commit commit = repo.commits.get(commitId);
		if (commit == null)
			return Respond.notFound();
		SearchQuery query = createReferencesQuery(repo, commit, type, page, pageSize, filter);
		SearchResult<IndexEntry> result = searchService.search(query);
		return Respond.ok(SearchResults.convert(result, (entry) -> {
			ObjectMap map = ObjectMap.fromObject(entry.asFetchRequestData());
			if (entry instanceof FlowIndexEntry) {
				map.put("flowType", ((FlowIndexEntry) entry).flowType);
			} else if (entry instanceof ProcessIndexEntry) {
				map.put("processType", ((ProcessIndexEntry) entry).processType);
			}
			return map;
		}));
	}

	private SearchQuery createReferencesQuery(Repository repo, Commit commit, ModelType type, int page, int pageSize,
			String filter) {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.page(page)
				.pageSize(pageSize)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repo.toId()))
				.filter("commitId", SearchFilterValue.term(commit.id));
		if (!Strings.isNullOrEmpty(filter)) {
			builder.filter("name", SearchFilterValue.wildcard("*" + filter.toLowerCase() + "*"));
		}
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		} else {
			for (ModelType categorized : ModelType.categorized()) {
				builder.aggregation(Aggregations.MODEL_TYPE, categorized.name());
			}
		}
		return builder.build();
	}

	private List<Map<String, Object>> putUserName(List<Commit> commits) {
		List<Map<String, Object>> mapped = new ArrayList<>();
		for (Commit commit : commits)
			mapped.add(putUserName(commit));
		return mapped;
	}

	private Map<String, Object> putUserName(Commit commit) {
		return putUserName(ObjectMap.fromObject(commit));
	}

	private Map<String, Object> putUserName(ObjectMap map) {
		User user = userService.getForUsername(map.getString("user"));
		if (user != null)
			map.put("userDisplayName", user.name);
		else
			map.put("userDisplayName", map.getString("user"));
		return map;
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
		Commit lastCommit = repo.commits.getLastBefore(type, refId, commitId);
		if (lastCommit == null || lastCommit.id.equals(commitId))
			return Respond.notFound("No previous commit found for " + type.name() + " " + refId);
		return Respond.ok(lastCommit.id);
	}

}
