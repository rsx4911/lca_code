package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Activities;
import com.greendelta.collaboration.webservice.util.Activities.ActivityType;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.search.wrapper.SearchResult;

@Path("activities")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityResource {

	private final UserService userService;
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final CommentService commentService;
	private final TaskService taskService;

	@Inject
	public ActivityResource(UserService userService, RepositoryService repoService, HistoryService historyService,
			CommentService commentService, TaskService taskService) {
		this.userService = userService;
		this.repoService = repoService;
		this.historyService = historyService;
		this.commentService = commentService;
		this.taskService = taskService;
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("repositoryPath") String repositoryPath) {
		User user = userService.getCurrentUser();
		List<Repository> repositories = getRepositories(repositoryPath);
		List<ObjectMap> activities = new ArrayList<>();
		Map<String, Commit> commits = new HashMap<>();
		repositories.forEach(repo -> {
			List<Commit> nextCommits = historyService.getCommits(repo);
			commits.putAll(com.greendelta.collaboration.util.Collections.map(
					nextCommits,
					commit -> commit.id,
					commit -> commit));
			activities.addAll(Client.map(nextCommits,
					commit -> Activities.map(commit, repo.toId())));
			activities.addAll(Client.map(commentService.getAllFor(repo), Activities::map));
		});
		if (repositoryPath != null && repositories.size() != 0) {
			Repository repo = repositories.get(0);
			for (Task task : taskService.getAllFor(repo)) {
				activities.addAll(Activities.map(task));
			}						
		} else {
			for (Task task : taskService.getAllFor(user)) {
				activities.addAll(Activities.map(task));
			}			
		}
		Collections.sort(activities, (a1, a2) -> Long.compare(a2.getLong("timestamp"), a1.getLong("timestamp")));
		SearchResult<ObjectMap> result = SearchResults.paged(page, pageSize, activities);
		for (ObjectMap entry : result.data) {
			if (entry.get("type") == ActivityType.COMMIT) {
				entry.put("userDisplayName", getUserName(commits.get(entry.getString("id"))));
			}
		}
		return Respond.ok(result);
	}

	private List<Repository> getRepositories(String repositoryPath) {
		if (repositoryPath == null)
			return repoService.getAllAccessible();
		return Arrays.asList(repoService.get(repositoryPath));
	}

	private String getUserName(Commit commit) {
		User user = userService.getForUsername(commit.user);
		if (user != null)
			return user.name;
		return commit.user;
	}

}
