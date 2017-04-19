package com.greendelta.collaboration.service;

import java.util.Set;

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;

public class ReviewService extends TaskExecutionService<Review> {

	private final Dao<Review> dao;
	private final AccessService accessService;

	@Inject
	public ReviewService(Dao<Review> dao, UserService userService, RepositoryService repoService,
			AccessService accessService) {
		super(dao, userService, repoService, accessService);
		this.dao = dao;
		this.accessService = accessService;
	}

	public Review setReferences(long reviewId, Set<ReviewReference> references) {
		Review fromDb = get(reviewId);
		Repository repo = getRepository(fromDb.repositoryPath);
		if (!accessService.canManageTaskIn(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MANAGE_TASK");
		fromDb.references = references;
		long lastId = dao.getLastId(ReviewReference.class);
		for (ReviewReference reference : fromDb.references) {
			if (reference.hasId())
				continue;
			reference.setId(++lastId);
		}
		return dao.update(fromDb);
	}

}
