package com.greendelta.collaboration.controller.util;

import java.util.Map;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.util.Maps;

public class Comments {

	private Comments() {
		// only static access
	}

	public static Map<String, Object> map(Comment comment) {
		var map = Maps.of(comment);
		map.put("user", Users.mapForOthers(comment.user));
		if (comment.replyTo != null) {
			map.put("replyTo", comment.replyTo.id);
		}
		return map;
	}
}
