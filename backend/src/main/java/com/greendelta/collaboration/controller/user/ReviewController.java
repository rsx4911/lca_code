package com.greendelta.collaboration.controller.user;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openlca.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Reviews;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.task.ReviewService;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/task/review")
public class ReviewController {

	private final ReviewService service;
	private final TaskService taskService;
	private final UserService userService;
	private final PermissionsService permissions;
	private final NotificationService notificationService;
	private final RepositoryService repoService;
	private final SettingsService settings;

	public ReviewController(ReviewService service, TaskService taskService, UserService userService,
			PermissionsService permissions, NotificationService notificationService, RepositoryService repoService,
			SettingsService settings) {
		this.service = service;
		this.taskService = taskService;
		this.userService = userService;
		this.permissions = permissions;
		this.notificationService = notificationService;
		this.repoService = repoService;
		this.settings = settings;
	}

	@GetMapping("{id}")
	public Map<String, Object> get(@PathVariable long id) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		try (var repo = repoService.get(review.repositoryPath)) {
			return Reviews.map(review, repo);
		}
	}

	@PostMapping
	public Map<String, Object> start(@RequestBody Review review) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		checkValidity(review);
		try (var repo = repoService.get(review.repositoryPath)) {
			service.start(review);
			notificationService.taskStarted(repo, review).send();
			return getActiveTasks();
		}
	}

	@PutMapping("{id}")
	public Map<String, Object> update(@RequestBody Review review) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		if (Strings.nullOrEmpty(review.name))
			throw Response.badRequest("name", "Missing input: Name");
		service.merge(review);
		return getActiveTasks();
	}

	private void checkValidity(Review review) {
		if (Strings.nullOrEmpty(review.name))
			throw Response.badRequest("name", "Missing input: Name");
		if (Strings.nullOrEmpty(review.repositoryPath))
			throw Response.badRequest("repositoryPath", "Missing input: Repository path");
		if (!review.assignments.isEmpty() || review.id != 0)
			throw Response.conflict("Review object already exists");
	}

	@PutMapping("{id}/references")
	public Map<String, Object> setReferences(
			@PathVariable long id,
			@RequestBody Set<String> paths) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		if (!review.assignments.isEmpty())
			throw Response.badRequest("", "References can not be changed after reviewer were already assigned");
		try (var repo = repoService.get(review.repositoryPath)) {
			if (repo == null)
				throw Response.notFound("No repository with id " + review.repositoryPath + " found");
			service.setReferences(id, paths);
			return getActiveTasks();
		}
	}

	@PutMapping("{id}/complete")
	public Map<String, Object> completeReview(@PathVariable long id) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		try (var repo = repoService.get(review.repositoryPath)) {
			service.end(review, TaskState.COMPLETED);
			notificationService.taskCompleted(repo, review).send();
			return getActiveTasks();
		}
	}

	@PutMapping("{id}/cancel")
	public Map<String, Object> cancelReview(@PathVariable long id) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		try (var repo = repoService.get(review.repositoryPath)) {
			service.end(review, TaskState.CANCELED);
			notificationService.taskCanceled(repo, review).send();
			return getActiveTasks();
		}
	}

	@PutMapping("{id}/assign/{username}")
	public Map<String, Object> assignReviewer(
			@PathVariable long id,
			@PathVariable String username) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		if (review.references.isEmpty())
			throw Response.badRequest("", "Please select data set references before assigning a user");
		try (var repo = repoService.get(review.repositoryPath)) {
			var assignment = service.startAssignment(review, username,
					(user, r) -> permissions.canReviewIn(user, r.path()));
			notificationService.taskAssigned(repo, review, assignment).send();
			return getActiveTasks();
		}
	}

	@PutMapping("{id}/complete/{username}")
	public Map<String, Object> completeAssignment(
			@PathVariable long id,
			@PathVariable String username) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		try (var repo = repoService.get(review.repositoryPath)) {
			if (review.state != TaskState.PROCESSING)
				throw Response.conflict("Task is not in process state");
			var assignment = service.endAssignment(review, username, false);
			if (assignment == null)
				throw Response.notFound("User " + username + " has no active assignment");
			notificationService.taskCompleted(repo, review, assignment);
			return getActiveTasks();
		}
	}

	@PutMapping("{id}/cancel/{username}")
	public Map<String, Object> cancelAssignment(
			@PathVariable long id,
			@PathVariable String username) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		try (var repo = repoService.get(review.repositoryPath)) {
			if (review.state != TaskState.PROCESSING)
				throw Response.conflict("Task is not in process state");
			var assignment = service.endAssignment(review, username, true);
			if (assignment == null)
				throw Response.notFound("User " + username + " has no active assignment");
			notificationService.taskRevoked(repo, review, assignment);
			return getActiveTasks();
		}
	}

	@PutMapping("{id}/markAsReviewed/{referenceId}/{value}")
	public Map<String, Object> markAsReviewed(
			@PathVariable long id,
			@PathVariable long referenceId,
			@PathVariable boolean value) {
		if (!settings.is(ServerSetting.TASKS_ENABLED))
			throw Response.unavailable("Task feature not enabled");
		var review = service.get(id);
		if (review == null)
			throw Response.notFound("No review with id " + id + " found");
		service.markAsReviewed(id, referenceId, value);
		return getActiveTasks();
	}

	private Map<String, Object> getActiveTasks() {
		var user = userService.getCurrentUser();
		var activeTasks = taskService.getAllActiveFor(user).size();
		return Collections.singletonMap("activeTasks", Integer.toString(activeTasks));
	}

}
