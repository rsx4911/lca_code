package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.util.ObjectMap;

import com.greendelta.collaboration.model.Comment;

public class Comments {

	private Comments() {
		// only static access
	}

	public static List<ObjectMap> map(List<Comment> comments) {
		List<ObjectMap> all = new ArrayList<>();
		for (Comment comment : comments)
			all.add(map(comment));
		return all;
	}

	public static ObjectMap map(Comment comment) {
		ObjectMap map = ObjectMap.fromObject(comment);
		map.put("user", Users.mapForOthers(comment.user));
		return map;
	}

}
