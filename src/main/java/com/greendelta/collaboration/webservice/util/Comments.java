package com.greendelta.collaboration.webservice.util;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.util.ObjectMap;

public class Comments {

	private Comments() {
		// only static access
	}

	public static ObjectMap map(Comment comment) {
		ObjectMap map = ObjectMap.fromObject(comment);
		map.put("user", Users.mapForOthers(comment.user));
		if (comment.replyTo != null) {
			map.put("replyTo", comment.replyTo.id);
		}
		return map;
	}
}
