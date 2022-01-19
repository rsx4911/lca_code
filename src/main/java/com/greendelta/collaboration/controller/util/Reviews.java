package com.greendelta.collaboration.controller.util;

import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.ObjectMap;

public class Reviews {

	private Reviews() {
		// only static access
	}

	public static ObjectMap map(Review review, Repository repo) {
		return Tasks.map(review, repo);
	}

}
