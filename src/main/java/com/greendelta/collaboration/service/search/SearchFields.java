package com.greendelta.collaboration.service.search;

import java.util.HashSet;
import java.util.Set;

import org.openlca.core.model.ModelType;

public class SearchFields {

	public static String[] get(ModelType type, boolean loggedIn) {
		Set<String> fields = new HashSet<>();
		fields.add("name");
		return fields.toArray(new String[fields.size()]);
	}

}
