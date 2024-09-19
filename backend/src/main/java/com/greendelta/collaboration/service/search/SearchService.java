package com.greendelta.collaboration.service.search;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.search.DsEntry;
import com.greendelta.collaboration.search.Index;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class SearchService {

	private final SettingsService settings;
	private final QueryService queryService;

	public SearchService(SettingsService settings, QueryService queryService) {
		this.settings = settings;
		this.queryService = queryService;
	}

	public SearchResult<DsEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	Index on(SearchIndex... indices) {
		var clients = Arrays.asList(indices).stream().map(settings.searchConfig::getSearchClient).toList();
		return new Index(clients.toArray(new SearchClient[clients.size()]));
	}

}
