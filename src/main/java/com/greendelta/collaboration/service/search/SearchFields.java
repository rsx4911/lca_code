package com.greendelta.collaboration.service.search;

import java.util.HashSet;

import org.openlca.core.model.ModelType;

public class SearchFields {

	public static String[] get(ModelType type, boolean loggedIn) {
		var fields = new HashSet<String>();
		fields.add("name");
		return fields.toArray(new String[fields.size()]);
	}

}
