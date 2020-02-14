package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.UserSettings;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Beans;
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
	private final SearchService searchService;
	private final CommentService commentService;
	private final TaskService taskService;
	private final SettingsService settingsService;

	@Inject
	public ActivityResource(UserService userService, RepositoryService repoService, HistoryService historyService,
			SearchService searchService, CommentService commentService, TaskService taskService,
			SettingsService settingsService) {
		this.userService = userService;
		this.repoService = repoService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.commentService = commentService;
		this.taskService = taskService;
		this.settingsService = settingsService;
	}

	@GET
	public Response getAll(
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("showCommitActivities") @DefaultValue("true") boolean showCommitActivities,
			@QueryParam("showCommentActivities") @DefaultValue("true") boolean showCommentActivities,
			@QueryParam("showTaskActivities") @DefaultValue("true") boolean showTaskActivities,
			@QueryParam("repositoryPath") String repositoryPath) {
		if (repositoryPath == null && !settingsService.is(Key.DASHBOARD_ACTIVITIES_ENABLED))
			return Respond.status(Status.SERVICE_UNAVAILABLE, "Dashboard activities feature not enabled");
		if (repositoryPath != null && !settingsService.is(Key.REPOSITORY_ACTIVITIES_ENABLED))
			return Respond.status(Status.SERVICE_UNAVAILABLE, "Repository activities feature not enabled");
		User user = userService.getCurrentUser();
		List<Repository> repositories = getRepositories(repositoryPath);
		List<ObjectMap> activities = new ArrayList<>();
		Map<String, Commit> commits = new HashMap<>();
		Map<String, Repository> repos = new HashMap<>();
		repositories.forEach(repo -> {
			if (showCommitActivities) {
				List<Commit> nextCommits = historyService.getCommits(repo);
				commits.putAll(com.greendelta.collaboration.util.Collections.map(
						nextCommits,
						commit -> commit.id,
						commit -> commit));
				activities.addAll(Client.map(nextCommits,
						commit -> Activities.map(commit, repo.toId())));
				for (Commit commit : nextCommits) {
					repos.put(commit.id, repo);
				}
			}
			if (showCommentActivities) {
				activities.addAll(Client.map(commentService.getAllFor(repo), Activities::map));
			}
		});
		if (showTaskActivities) {
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
		}
		Collections.sort(activities, (a1, a2) -> Long.compare(a2.getLong("timestamp"), a1.getLong("timestamp")));
		SearchResult<ObjectMap> result = SearchResults.paged(page, pageSize, activities);
		for (ObjectMap entry : result.data) {
			if (entry.get("type") == ActivityType.COMMIT) {
				String id = entry.getString("id");
				putAdditionalInfo(entry, repos.get(id), commits.get(id));
			}
		}
		return Respond.ok(result);
	}

	private List<Repository> getRepositories(String repositoryPath) {
		if (repositoryPath == null)
			return repoService.getAllAccessible();
		return Arrays.asList(repoService.get(repositoryPath));
	}

	private void putAdditionalInfo(ObjectMap entry, Repository repo, Commit commit) {
		User user = userService.getForUsername(commit.user);
		entry.put("userDisplayName", user != null ? user.name : commit.user);
		entry.put("additions", searchService.getDatasetCount(repo, commit.id, IndexAction.ADD));
		entry.put("deletions", searchService.getDatasetCount(repo, commit.id, IndexAction.DELETE));
		entry.put("updates", searchService.getDatasetCount(repo, commit.id, IndexAction.UPDATE));
	}

	@PUT
	@Path("settings")
	public Response updateSettings(UserSettings settings) {
		if (!settingsService.is(Key.DASHBOARD_ACTIVITIES_ENABLED)
				&& !settingsService.is(Key.REPOSITORY_ACTIVITIES_ENABLED))
			return Respond.status(Status.SERVICE_UNAVAILABLE, "Activities feature not enabled");
		User currentUser = userService.getCurrentUser();
		Beans.populateProperties(settings, currentUser.settings,
				"showTaskActivities", "showCommentActivities", "showCommitActivities");
		currentUser = userService.update(currentUser);
		return Respond.ok(ObjectMap.fromObject(currentUser.settings));
	}

}
