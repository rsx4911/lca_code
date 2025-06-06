package com.greendelta.collaboration.service.search;

import java.util.Arrays;
import java.util.Map;

import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.search.UsageIndex;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class UsageService {

	private final UserService userService;
	private final SettingsService settings;

	public UsageService(UserService userService, SettingsService settings) {
		this.userService = userService;
		this.settings = settings;
	}

	public SearchResult<Map<String, Object>> query(Repository repo, String refId, String field, int page, int pageSize,
			String filter) {
		var client = userService.getCurrentUser().isAnonymous()
				? settings.searchConfig.getSearchClient(SearchIndex.PUBLIC_USAGE)
				: settings.searchConfig.getSearchClient(SearchIndex.PRIVATE_USAGE);
		var query = new SearchQueryBuilder();
		query.page(page);
		query.pageSize(pageSize);
		query.filter("path", SearchFilterValue.term(repo.path()));
		query.fields("type", "refId", "name", "processType", "flowType");
		if (Strings.nullOrEmpty(field)) {
			field = "others";
		}
		query.filter(field, SearchFilterValue.term(refId));
		if (!Strings.nullOrEmpty(filter)) {
			query.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		return client.search(query.build());
	}

	UsageIndex on(SearchIndex... indices) {
		var clients = Arrays.asList(indices).stream().map(settings.searchConfig::getSearchClient).toList();
		return new UsageIndex(clients.toArray(new SearchClient[clients.size()]));
	}

}
