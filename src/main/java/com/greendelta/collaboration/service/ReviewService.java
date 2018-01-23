package com.greendelta.collaboration.service;

import java.util.Set;

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;

public class ReviewService extends TaskExecutionService<Review> {

	private final Dao<Review> dao;
	private final AccessService accessService;
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public ReviewService(Dao<Review> dao, UserService userService, RepositoryService repoService,
			AccessService accessService) {
		super(dao, userService, repoService, accessService);
		this.dao = dao;
		this.userService = userService;
		this.repoService = repoService;
		this.accessService = accessService;
	}

	public void setReferences(long reviewId, Set<ReviewReference> references) {
		Review fromDb = get(reviewId);
		Repository repo = repoService.get(fromDb.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		fromDb.references = references;
		long lastId = dao.getLastId(ReviewReference.class);
		for (ReviewReference reference : fromDb.references) {
			if (reference.hasId())
				continue;
			reference.setId(++lastId);
		}
		dao.update(fromDb);
	}

	public void markAsReviewed(long reviewId, long referenceId, boolean value) {
		Review fromDb = get(reviewId);
		for (ReviewReference reference : fromDb.references) {
			if (reference.getId() != referenceId)
				continue;
			if (value && reference.reviewer != null)
				// already marked
				return;
			if (!value && reference.reviewer == null)
				// already not marked
				return;
			if (value) {
				reference.reviewer = userService.getCurrentUser();
			} else {
				reference.reviewer = null;
			}
			dao.update(fromDb);
		}
	}

}
