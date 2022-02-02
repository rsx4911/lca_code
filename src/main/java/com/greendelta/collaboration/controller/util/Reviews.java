package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.service.Repository;

public class Reviews {

	private Reviews() {
		// only static access
	}

	public static Map<String, Object> map(Review review, Repository repo) {
		return Tasks.map(review, repo);
	}

}
