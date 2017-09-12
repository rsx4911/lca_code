package com.greendelta.collaboration.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchClient<I extends SearchIndex, Q extends SearchQuery<?>> {

	SearchResult search(I index, Q searchQuery);
	
	void initialize(I index);
	
	void index(String id, Map<String, Object> content, I index);

	void remove(String id, I index);

	Map<String, Object> get(String id, I index);
	
	List<Map<String, Object>> get(Set<String> ids, I index);

	void clear(I index);
	
}
