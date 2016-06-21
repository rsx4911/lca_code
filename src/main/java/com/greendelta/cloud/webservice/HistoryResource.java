package com.greendelta.cloud.webservice;

import java.util.ArrayList;
import java.util.Collections;
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
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.ObjectMap;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.PagedResult;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;

@Path("history")
public class HistoryResource {

	private final HistoryService service;
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public HistoryResource(HistoryService service, RepositoryService repoService, UserService userService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
	}

	@GET
	@Path("{group}/{name}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = service.getCommitsAfter(repo, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommitHistory(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type, @PathParam("refId") String refId) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = service.getCommits(repo, type, refId);
		if (commits.size() == 0)
			return Respond.noContent();
		Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("commit/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommit(@PathParam("group") String group,
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
	public Response getPreviousReference(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("type") ModelType type, @PathParam("refId") String refId, @PathParam("commitId") String commitId) {
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
	public Response getReferences(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("commitId") String commitId, @QueryParam("page") @DefaultValue("1") int page) {
		Repository repo = repoService.get(group, name);
		Commit commit = service.getCommit(repo, commitId);
		if (commit == null)
			return Respond.notFound();
		List<Dataset> all = service.getReferences(repo, commitId);
		List<Dataset> filtered = filterCategorizedTypes(all);
		List<Dataset> refs = new ArrayList<>();
		for (int i = (page - 1) * 10; i < page * 10; i++)
			if (filtered.size() > i)
				refs.add(filtered.get(i));
		PagedResult<Dataset> result = new PagedResult<Dataset>(page, null, filtered.size(), refs.size(), refs);
		return Respond.ok(result);
	}

	private List<Dataset> filterCategorizedTypes(List<Dataset> all) {
		List<Dataset> filtered = new ArrayList<>();
		for (Dataset ds : all)
			if (ds.type.isCategorized())
				filtered.add(ds);
		return filtered;
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
