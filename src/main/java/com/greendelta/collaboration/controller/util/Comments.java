package com.greendelta.collaboration.controller.util;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.util.ObjectMap;

public class Comments {

	private Comments() {
		// only static access
	}

	public static ObjectMap map(Comment comment) {
		var map = ObjectMap.fromObject(comment);
		map.put("user", Users.mapForOthers(comment.user));
		if (comment.replyTo != null) {
			map.put("replyTo", comment.replyTo.id);
		}
		return map;
	}
}
