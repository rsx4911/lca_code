package com.greendelta.collaboration.webservice.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.core.model.ModelType;
import org.openlca.util.KeyGen;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.DatasetIndexEntry;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.model.task.TaskState;
import com.greendelta.collaboration.service.AccessService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.NotificationService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.ReviewService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.service.TaskService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Reviews;

@Path("task/review")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReviewResource {

	private final ReviewService service;
	private final TaskService taskService;
	private final UserService userService;
	private final AccessService accessService;
	private final HistoryService historyService;
	private final NotificationService notificationService;
	private final RepositoryService repoService;
	private final SearchService searchService;

	@Inject
	public ReviewResource(ReviewService service, TaskService taskService, UserService userService,
			AccessService accessService, HistoryService historyService, NotificationService notificationService,
			RepositoryService repoService, SearchService searchService) {
		this.service = service;
		this.taskService = taskService;
		this.userService = userService;
		this.accessService = accessService;
		this.historyService = historyService;
		this.notificationService = notificationService;
		this.repoService = repoService;
		this.searchService = searchService;
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
		String[] path = review.repositoryPath.split("/");
		Repository repo = repoService.get(path[0], path[1]);
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
		Set<ReviewReference> all = new HashSet<>();
		String[] split = review.repositoryPath.split("/");
		Repository repo = repoService.get(split[0], split[1]);
		if (repo == null)
			return Respond.notFound("No repository with id " + review.repositoryPath + " found");
		for (Reference reference : references) {
			if (reference.type != null && Strings.isNullOrEmpty(reference.id)) {
				all.addAll(collectForType(repo, reference.type));
			} else if (reference.type == ModelType.CATEGORY && !Strings.isNullOrEmpty(reference.id)) {
				all.addAll(collectForCategory(repo, toId(reference.id)));
			} else {
				all.add(convert(repo, reference));
			}
		}
		service.setReferences(id, all);
		return createResponse();
	}

	private List<ReviewReference> collectForType(Repository repo, ModelType type) {
		return convert(repo, searchService.getAll(repo, type));
	}

	private List<ReviewReference> collectForCategory(Repository repo, String id) {
		return convert(repo, searchService.getForCategory(repo, id));
	}

	private String toId(String categoryPath) {
		return KeyGen.get(categoryPath.split("/"));
	}

	private List<ReviewReference> convert(Repository repo, List<DatasetIndexEntry> entries) {
		List<ReviewReference> references = new ArrayList<>();
		for (DatasetIndexEntry entry : entries) {
			ReviewReference ref = new ReviewReference();
			if (ref.type == ModelType.CATEGORY) {
				references.addAll(convert(repo, searchService.getForCategory(repo, ref.refId)));
			} else {
				ref.type = entry.type;
				ref.refId = entry.refId;
				ref.commitId = entry.commitId;
				ref.name = entry.name;
				references.add(ref);
			}
		}
		return references;
	}

	private ReviewReference convert(Repository repo, Reference ref) {
		ReviewReference reference = new ReviewReference();
		reference.type = ref.type;
		reference.refId = ref.id;
		reference.commitId = historyService.getLastCommit(repo, ref.type, ref.id).id;
		reference.name = searchService.get(repo, ref.id, reference.commitId).name;
		return reference;
	}

	@PUT
	@Path("{id}/assign/{username}")
	public Response assignReviewer(@PathParam("id") long id, @PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		if (review.references.isEmpty())
			return Respond.invalid("", "Please select data set references before assigning a user");
		String[] path = review.repositoryPath.split("/");
		Repository repository = repoService.get(path[0], path[1]);
		TaskAssignment assignment = service.startAssignment(review, username,
				(user, repo) -> accessService.canReviewIn(user, repo.toId()));
		notificationService.taskAssigned(repository, review, assignment).send();
		return createResponse();
	}

	@PUT
	@Path("{id}/markAsReviewed/{referenceId}/{value}")
	public Response markAsReviewed(@PathParam("id") long id, @PathParam("referenceId") long referenceId,
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
		String[] path = review.repositoryPath.split("/");
		Repository repo = repoService.get(path[0], path[1]);
		TaskAssignment assignment = service.endAssignment(review, username, false);
		notificationService.taskCompleted(repo, review, assignment);
		return createResponse();
	}

	@PUT
	@Path("{id}/cancel/{username}")
	public Response cancelAssignment(@PathParam("id") long id, @PathParam("username") String username) {
		Review review = service.get(id);
		if (review == null)
			return Respond.notFound("No review with id " + id + " found");
		String[] path = review.repositoryPath.split("/");
		Repository repo = repoService.get(path[0], path[1]);
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
		String[] path = review.repositoryPath.split("/");
		Repository repo = repoService.get(path[0], path[1]);
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
		String[] path = review.repositoryPath.split("/");
		Repository repo = repoService.get(path[0], path[1]);
		service.end(review, TaskState.CANCELED);
		notificationService.taskCanceled(repo, review).send();
		return createResponse();
	}

	private Response createResponse() {
		User user = userService.getCurrentUser();
		int activeTasks = taskService.getAllActiveFor(user).size();
		return Respond.ok(Collections.singletonMap("activeTasks", Integer.toString(activeTasks)));
	}

	private static class Reference {

		public String id;
		public ModelType type;

	}

}
