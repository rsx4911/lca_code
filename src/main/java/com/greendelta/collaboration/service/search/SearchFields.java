package com.greendelta.collaboration.service.search;

import java.util.HashSet;
import java.util.Set;

import org.openlca.core.model.ModelType;

public class SearchFields {

//	public static final String[] PROCESS_FULL_TEXT_FIELDS = {
//			"name", "completeness", "sampleRepresentativeness", "samplingProcedure", "technology",
//			"representativeness", "biogenicCarbon", "reviewer", "copyrightHolder", "contact", "description"
//	};

	public static String[] get(ModelType type, boolean loggedIn) {
		Set<String> fields = new HashSet<>();
		fields.add("name");
//		if (loggedIn) {
//			fields.add("commitMessage");
//		}
//		fields.addAll(Arrays.asList(PROCESS_FULL_TEXT_FIELDS));
		return fields.toArray(new String[fields.size()]);
	}

}
