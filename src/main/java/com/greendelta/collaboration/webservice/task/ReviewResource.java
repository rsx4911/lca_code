package com.greendelta.collaboration.webservice.task;

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
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.AccessService;
import com.greendelta.collaboration.service.ReviewService;
import com.greendelta.collaboration.service.TaskService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Reviews;

@Path("task/review")
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

	private final ReviewService service;
	private final TaskService taskService;
	private final UserService userService;
	private final AccessService accessService;

	@Inject
	public ReviewResource(ReviewService service, TaskService taskService, UserService userService,
			AccessService accessService) {
		this.service = service;
		this.taskService = taskService;
		this.userService = userService;
		this.accessService = accessService;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") long id) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		return Respond.ok(Reviews.map(review));
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response start(Review review) {
		if (Strings.isNullOrEmpty(review.name))
			return Respond.invalid("name", "Missing input: Name");
		if (Strings.isNullOrEmpty(review.repositoryPath))
			return Respond.invalid("repositoryPath", "Missing input: Repository path");
		service.start(review);
		return createResponse();
	}

	@PUT
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response update(Review review) {
		service.merge(review);
		return createResponse();
	}

	@PUT
	@Path("{id}/assign/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response assignReviewer(@PathParam("id") long id, @PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		service.startAssignment(review, username, (user, repo) -> accessService.canReviewIn(user, repo.toId()));
		return createResponse();
	}

	@PUT
	@Path("{id}/complete/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response completeAssignment(@PathParam("id") long id, @PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		service.endAssignment(review, username, false);
		return createResponse();
	}

	@PUT
	@Path("{id}/cancel/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response cancelAssignment(@PathParam("id") long id, @PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		service.endAssignment(review, username, true);
		return createResponse();
	}

	@PUT
	@Path("{id}/complete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response closeReview(@PathParam("id") long id) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		service.end(review, TaskState.COMPLETED);
		return createResponse();
	}

	@PUT
	@Path("{id}/cancel")
	@Produces(MediaType.TEXT_PLAIN)
	public Response cancelReview(@PathParam("id") long id) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		service.end(review, TaskState.CANCELED);
		return createResponse();
	}

	private Response createResponse() {
		User user = userService.getCurrentUser();
		int activeTasks = taskService.getAllActiveFor(user).size();
		return Respond.ok(Integer.toString(activeTasks));
	}

}
