package com.greendelta.collaboration.webservice.util;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.Comment;

public class Comments {

	private Comments() {
		// only static access
	}

	public static ObjectMap map(Comment comment) {
		ObjectMap map = ObjectMap.fromObject(comment);
		map.put("user", Users.mapForOthers(comment.user));
		map.put("approvedBy", Users.mapForOthers(comment.approvedBy));
		return map;
	}

}
