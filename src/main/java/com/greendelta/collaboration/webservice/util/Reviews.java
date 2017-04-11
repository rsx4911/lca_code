package com.greendelta.collaboration.webservice.util;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.task.Review;

public class Reviews {

	private Reviews() {
		// only static access
	}

	public static ObjectMap map(Review review) {
		return Tasks.map(review);
	}

}
