package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.openlca.git.model.Commit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Activities;
import com.greendelta.collaboration.controller.util.Activities.ActivityType;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.UserSettings;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryList;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@RestController
@RequestMapping("ws/activities")
public class ActivityController {

	private final UserService userService;
	private final RepositoryService repoService;
	private final CommentService commentService;
	private final TaskService taskService;
	private final SettingsService settings;

	public ActivityController(UserService userService, RepositoryService repoService, CommentService commentService,
			TaskService taskService, SettingsService settings) {
		this.userService = userService;
		this.repoService = repoService;
		this.commentService = commentService;
		this.taskService = taskService;
		this.settings = settings;
	}

	@GetMapping
	public SearchResult<Map<String, Object>> getAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "true") boolean showCommitActivities,
			@RequestParam(defaultValue = "true") boolean showCommentActivities,
			@RequestParam(defaultValue = "true") boolean showTaskActivities,
			@RequestParam(required = false) String repositoryPath) {
		if (repositoryPath == null && !settings.is(ServerSetting.DASHBOARD_ACTIVITIES_ENABLED))
			throw Response.unavailable("Dashboard activities feature not enabled");
		if (repositoryPath != null && !settings.is(ServerSetting.REPOSITORY_ACTIVITIES_ENABLED))
			throw Response.unavailable("Repository activities feature not enabled");
		try (var repositories = getRepositories(repositoryPath)) {
			var activities = new ArrayList<Map<String, Object>>();
			var commits = new HashMap<String, Commit>();
			var repos = new HashMap<String, Repository>();
			repositories.forEach(repo -> {
				if (showCommitActivities) {
					var nextCommits = repo.commits.find().all();
					commits.putAll(
							nextCommits.stream().collect(Collectors.toMap(commit -> commit.id, commit -> commit)));
					activities.addAll(nextCommits.stream()
							.map(commit -> Activities.map(commit, repo))
							.toList());
					nextCommits.forEach(commit -> repos.put(commit.id, repo));
				}
				if (showCommentActivities) {
					activities.addAll(commentService.getAllFor(repo).stream()
							.map(comment -> Activities.map(comment, repo))
							.toList());
				}
			});
			if (showTaskActivities) {
				repositories.forEach(repo -> {
					taskService.getAllFor(repo).forEach(task -> {
						activities.addAll(Activities.map(task, repo));
					});
				});
			}
			activities.sort((a1, a2) -> Long.compare(Maps.getLong(a2, "timestamp"), Maps.getLong(a1, "timestamp")));
			var result = SearchResults.paged(page, pageSize, activities);
			result.data.stream()
					.filter(entry -> entry.get("type") == ActivityType.COMMIT)
					.forEach(entry -> {
						String id = Maps.getString(entry, "id");
						var commit = commits.get(id);
						var user = userService.getForUsernameOrEmail(commit.user);
						entry.put("userDisplayName", user != null ? user.name : commit.user);
					});
			return result;
		}
	}

	private RepositoryList getRepositories(String repositoryPath) {
		if (repositoryPath == null)
			return repoService.getAllAccessible();
		RepositoryList list = new RepositoryList();
		list.add(repoService.get(repositoryPath));
		return list;
	}

	@PutMapping("settings")
	public UserSettings updateSettings(@RequestBody UserSettings userSettings) {
		if (!settings.is(ServerSetting.DASHBOARD_ACTIVITIES_ENABLED)
				&& !settings.is(ServerSetting.REPOSITORY_ACTIVITIES_ENABLED))
			throw Response.unavailable("Activities feature not enabled");
		var currentUser = userService.getCurrentUser();
		currentUser.settings.showCommentActivities = userSettings.showCommentActivities;
		currentUser.settings.showCommitActivities = userSettings.showCommitActivities;
		currentUser.settings.showTaskActivities = userSettings.showTaskActivities;
		currentUser = userService.update(currentUser);
		return currentUser.settings;
	}

}
