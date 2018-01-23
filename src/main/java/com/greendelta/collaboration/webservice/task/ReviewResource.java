package com.greendelta.collaboration.webservice.task;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.AccessService;
import com.greendelta.collaboration.service.BrowseService;
import com.greendelta.collaboration.service.NotificationService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.ReviewService;
import com.greendelta.collaboration.service.TaskService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.task.ReferenceCollector.Reference;
import com.greendelta.collaboration.webservice.util.Reviews;

@Path("task/review")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReviewResource {

	private final ReviewService service;
	private final TaskService taskService;
	private final UserService userService;
	private final AccessService accessService;
	private final BrowseService browseService;
	private final NotificationService notificationService;
	private final RepositoryService repoService;

	@Inject
	public ReviewResource(ReviewService service, TaskService taskService, UserService userService,
			AccessService accessService, NotificationService notificationService, RepositoryService repoService,
			BrowseService browseService) {
		this.service = service;
		this.taskService = taskService;
		this.userService = userService;
		this.accessService = accessService;
		this.notificationService = notificationService;
		this.repoService = repoService;
		this.browseService = browseService;
	}

	@GET
	@Path("{id}")
	public Response get(@PathParam("id") long id) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		return Respond.ok(Reviews.map(review));
	}

	@POST
	public Response start(Review review) {
		Response invalid = checkValidity(review);
		if (invalid != null)
			return invalid;
		Repository repo = repoService.get(review.repositoryPath);
		service.start(review);
		notificationService.taskStarted(repo, review).send();
		return createResponse();
	}

	@PUT
	@Path("{id}")
	public Response update(Review review) {
		if (Strings.isNullOrEmpty(review.name))
			return Respond.invalid("name", "Missing input: Name");
		service.merge(review);
		return createResponse();
	}

	private Response checkValidity(Review review) {
		if (Strings.isNullOrEmpty(review.name))
			return Respond.invalid("name", "Missing input: Name");
		if (Strings.isNullOrEmpty(review.repositoryPath))
			return Respond.invalid("repositoryPath", "Missing input: Repository path");
		return null;
	}

	@PUT
	@Path("{id}/references")
	public Response setReferences(@PathParam("id") long id, List<Reference> references) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		if (!review.assignments.isEmpty())
			return Respond.invalid("", "References can not be changed after reviewer were already assigned");
		Repository repo = repoService.get(review.repositoryPath);
		if (repo == null)
			return Respond.notFound("No repository with id " + review.repositoryPath + " found");
		ReferenceCollector collector = new ReferenceCollector(browseService);
		service.setReferences(id, collector.getReferences(repo, references));
		return createResponse();
	}

	@PUT
	@Path("{id}/assign/{username}")
	public Response assignReviewer(
			@PathParam("id") long id,
			@PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		if (review.references.isEmpty())
			return Respond.invalid("", "Please select data set references before assigning a user");
		Repository repository = repoService.get(review.repositoryPath);
		TaskAssignment assignment = service.startAssignment(review, username,
				(user, repo) -> accessService.canReviewIn(user, repo.toId()));
		notificationService.taskAssigned(repository, review, assignment).send();
		return createResponse();
	}

	@PUT
	@Path("{id}/markAsReviewed/{referenceId}/{value}")
	public Response markAsReviewed(
			@PathParam("id") long id,
			@PathParam("referenceId") long referenceId,
			@PathParam("value") boolean value) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		service.markAsReviewed(id, referenceId, value);
		return createResponse();
	}

	@PUT
	@Path("{id}/complete/{username}")
	public Response completeAssignment(@PathParam("id") long id, @PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		Repository repo = repoService.get(review.repositoryPath);
		TaskAssignment assignment = service.endAssignment(review, username, false);
		notificationService.taskCompleted(repo, review, assignment);
		return createResponse();
	}

	@PUT
	@Path("{id}/cancel/{username}")
	public Response cancelAssignment(
			@PathParam("id") long id,
			@PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		Repository repo = repoService.get(review.repositoryPath);
		TaskAssignment assignment = service.endAssignment(review, username, true);
		notificationService.taskRevoked(repo, review, assignment);
		return createResponse();
	}

	@PUT
	@Path("{id}/complete")
	public Response closeReview(@PathParam("id") long id) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		Repository repo = repoService.get(review.repositoryPath);
		service.end(review, TaskState.COMPLETED);
		notificationService.taskCompleted(repo, review).send();
		return createResponse();
	}

	@PUT
	@Path("{id}/cancel")
	public Response cancelReview(@PathParam("id") long id) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		Repository repo = repoService.get(review.repositoryPath);
		service.end(review, TaskState.CANCELED);
		notificationService.taskCanceled(repo, review).send();
		return createResponse();
	}

	private Response createResponse() {
		User user = userService.getCurrentUser();
		int activeTasks = taskService.getAllActiveFor(user).size();
		return Respond.ok(Collections.singletonMap("activeTasks", Integer.toString(activeTasks)));
	}

}
