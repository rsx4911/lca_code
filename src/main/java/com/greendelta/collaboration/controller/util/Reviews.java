package com.greendelta.collaboration.controller.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;

public class Reviews {

	private Reviews() {
		// only static access
	}

	public static Map<String, Object> map(Review review, Repository repo) {
		var map = Tasks.map(review, repo);
		map.put("references", map(review.references, repo));
		return map;
	}

	private static List<Map<String, Object>> map(List<ReviewReference> references, Repository repo) {
		var list = new ArrayList<Map<String, Object>>();
		for (var ref : references) {
			var map = Maps.of(ref);
			map.put("reviewer", Users.mapForOthers(ref.reviewer));
			map.put("name", MetaData.getName(repo, ref.type, ref.refId, ref.commitId));
			list.add(map);
		}
		return list;
	}

}
