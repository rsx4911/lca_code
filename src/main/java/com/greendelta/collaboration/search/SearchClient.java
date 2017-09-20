package com.greendelta.collaboration.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchClient {

	SearchResult search(SearchQuery searchQuery);
	
	void initialize();
	
	void index(String id, Map<String, Object> content);

	void remove(String id);

	Map<String, Object> get(String id);
	
	List<Map<String, Object>> get(Set<String> ids);

	void clear();
	
}
