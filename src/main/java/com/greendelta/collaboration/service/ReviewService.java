package com.greendelta.collaboration.service;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.task.Review;

public class ReviewService extends TaskExecutionService<Review> {

	@Inject
	public ReviewService(Dao<Review> dao, UserService userService, RepositoryService repoService,
			AccessService accessService) {
		super(dao, userService, repoService, accessService);
	}
}
