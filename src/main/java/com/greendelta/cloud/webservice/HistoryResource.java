package com.greendelta.cloud.webservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.ObjectMap;

import com.google.inject.Inject;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;
import com.greendelta.cloud.service.UserService;

@Path("history")
public class HistoryResource {

	private HistoryService service;
	private RepositoryService repoService;
	private UserService userService;

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
		List<Commit> commits = service.getCommits(repo, lastCommitId);
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
		Map<String, Object> result = putUserName(commit);
		List<Dataset> refs = new ArrayList<>();
		for (Dataset d : service.getReferences(repo, commitId))
			refs.add(d);
		result.put("references", refs);
		return Respond.ok(result);
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
		ObjectMap userMap = ObjectMap.fromObject(user);
		userMap.removeAllBut("username", "name");
		map.put("user", userMap);
		return map;
	}
}
