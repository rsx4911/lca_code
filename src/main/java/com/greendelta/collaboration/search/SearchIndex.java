package com.greendelta.collaboration.search;

import java.util.Map;

public interface SearchIndex {

	String getName();
	String getType();
	Map<String, String> getData();
	
}
