package com.greendelta.collaboration.service.task;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.error.ForbiddenAccessException;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class ReviewService extends TaskExecutionService<Review> {

	private final Dao<Review> dao;
	private final AccessService accessService;
	private final RepositoryService repoService;
	private final UserService userService;

	@Autowired
	public ReviewService(Dao<Review> dao, UserService userService, RepositoryService repoService,
			AccessService accessService) {
		super(dao, userService, repoService, accessService);
		this.dao = dao;
		this.userService = userService;
		this.repoService = repoService;
		this.accessService = accessService;
	}

	public void setReferences(long reviewId, Set<ReviewReference> references) {
		var fromDb = get(reviewId);
		try (var repo = repoService.get(fromDb.repositoryPath)) {
			if (!accessService.canManageTaskIn(repo.path()))
				throw new ForbiddenAccessException(repo.path(), "MANAGE_TASK");
		}
		fromDb.references = references;
		var lastId = dao.getLastId(ReviewReference.class);
		for (var reference : fromDb.references) {
			if (reference.id == 0)
				continue;
			reference.id = ++lastId;
		}
		dao.update(fromDb);
	}

	public void markAsReviewed(long reviewId, long referenceId, boolean value) {
		var fromDb = get(reviewId);
		for (ReviewReference reference : fromDb.references) {
			if (reference.id != referenceId)
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
