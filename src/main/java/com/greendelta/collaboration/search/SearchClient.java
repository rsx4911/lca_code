package com.greendelta.collaboration.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchClient {

	SearchResult search(SearchQuery searchQuery);
	
	void create(Map<String, Object> settings);
	
	void index(String type, String id, Map<String, Object> content);

	void remove(String type, String id);

	Map<String, Object> get(String type, String id);
	
	List<Map<String, Object>> get(String type, Set<String> ids);

	void delete();
	
}
