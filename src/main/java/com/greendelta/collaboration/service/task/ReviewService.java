package com.greendelta.collaboration.service.task;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.error.ForbiddenAccessException;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class ReviewService extends TaskExecutionService<Review> {

	private final Dao<Review> dao;
	private final Dao<ReviewReference> referenceDao;
	private final AccessService accessService;
	private final RepositoryService repoService;
	private final UserService userService;

	@Autowired
	public ReviewService(Dao<Review> dao, Dao<ReviewReference> referenceDao, Dao<TaskAssignment> assignmentDao,
			UserService userService, RepositoryService repoService,
			AccessService accessService) {
		super(dao, assignmentDao, userService, repoService, accessService);
		this.dao = dao;
		this.referenceDao = referenceDao;
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
		referenceDao.delete(fromDb.references);
		fromDb.references.clear();
		var lastId = referenceDao.getLastId();
		for (var reference : references) {
			reference.id = ++lastId;
			fromDb.references.add(reference);
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
			break;
		}
		dao.update(fromDb);
	}

}
