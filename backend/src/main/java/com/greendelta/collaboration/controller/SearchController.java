package com.greendelta.collaboration.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.search.DsEntry;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.IndexService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.search.UsageService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.results.AggregationResult;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("ws/public/search")
public class SearchController {

	private static final Logger log = LogManager.getLogger(SearchController.class);
	private final SearchService service;
	private final RepositoryService repoService;
	private final GroupService groupService;
	private final UserService userService;
	private final UsageService usageService;
	private final IndexService indexService;
	private final SettingsService settings;

	public SearchController(SearchService service, RepositoryService repoService, GroupService groupService,
			UserService userService, UsageService usageService, IndexService indexService, SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.groupService = groupService;
		this.userService = userService;
		this.usageService = usageService;
		this.indexService = indexService;
		this.settings = settings;
	}

	@GetMapping
	public Map<String, Object> search(@Autowired HttpServletRequest request) {
		if (!settings.searchConfig.isSearchAvailable())
			throw Response.unavailable("Search feature not enabled or search cluster unavailable");
		var parameters = getQueryParameters(request);
		var query = removeStringFilter("query", parameters);
		var page = removeIntFilter("page", parameters, 1);
		var pageSize = removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		log.info("Running search for '{}', page={}, pageSize={}, parameters={}", query, page, pageSize, parameters);
		var result = service.query(query, page, pageSize, parameters);
		result.aggregations.stream()
				.filter(r -> r.name.equals(Aggregations.CATEGORY.name)
						|| r.name.equals(Aggregations.FLOW_COMPLETENESS.name))
				.forEach(r -> r.group("/"));
		return map(result);
	}

	private Map<String, Object> map(SearchResult<DsEntry> result) {
		try (var accessible = repoService.getAllAccessible()) {
			var repositories = accessible.stream()
					.collect(Collectors.toMap(repo -> repo.path(), repo -> repo));
			var groups = accessible.stream().map(repo -> repo.group).toList();
			var loggedIn = userService.getCurrentUser().id != 0;
			var map = Maps.create();
			var resultInfo = Maps.of(result.resultInfo);
			resultInfo.put("indexing", indexService.isBeingUpdated(loggedIn
					? SearchIndex.PRIVATE
					: SearchIndex.PUBLIC));
			map.put("resultInfo", resultInfo);
			map.put("data", mapData(result, repositories, loggedIn));
			var aggregations = result.aggregations.stream().filter(a -> {
				if (!settings.is(ServerSetting.REPOSITORY_TAGS_ENABLED)
						&& a.name.equals(Aggregations.REPOSITORY_TAGS.name))
					return false;
				if (!settings.is(ServerSetting.DATASET_TAGS_ENABLED)
						&& a.name.equals(Aggregations.DATASET_TAGS.name))
					return false;
				return true;
			}).toList();
			map.put("aggregations", aggregations.stream().map(a -> {
				var aMap = Maps.of(a);
				if (a.name.equals(Aggregations.REPOSITORY.name)) {
					aMap.put("entries", mapRepositoryAggregationEntries(a, repositories));
				} else if (a.name.equals(Aggregations.GROUP.name)) {
					aMap.put("entries", mapGroupAggregationEntries(a, groups));
				}
				return aMap;
			}).toList());
			return map;
		}
	}

	private List<Map<String, Object>> mapRepositoryAggregationEntries(AggregationResult aggregationResult,
			Map<String, Repository> repositories) {
		return aggregationResult.entries.stream().map(entry -> {
			Map<String, Object> map = Maps.of(entry);
			Repository repo = repositories.get(entry.key);
			if (repo == null)
				return null;
			try {
				map.put("label", repo.settings.get(RepositorySetting.LABEL, repo.name));
			} catch (ResponseStatusException ex) {
				if (ex.getStatusCode() != HttpStatus.FORBIDDEN)
					throw ex;
			}
			return map;
		}).filter(Objects::nonNull).toList();
	}

	private List<Map<String, Object>> mapGroupAggregationEntries(AggregationResult aggregationResult,
			List<String> groups) {
		return aggregationResult.entries.stream().map(entry -> {
			Map<String, Object> map = Maps.of(entry);
			if (!groups.contains(entry.key))
				return null;
			try {
				String label = groupService.getSettings(entry.key).get(GroupSetting.LABEL, entry.key);
				map.put("label", label);
			} catch (ResponseStatusException ex) {
				if (ex.getStatusCode() != HttpStatus.FORBIDDEN)
					throw ex;
			}
			return map;
		}).filter(Objects::nonNull).toList();
	}

	private List<Map<String, Object>> mapData(SearchResult<DsEntry> result, Map<String, Repository> repositories,
			boolean loggedIn) {
		return result.data.stream().map(dsEntry -> {
			var entry = Maps.of(dsEntry);
			var versions = dsEntry.versions.stream().map(dsVersion -> {
				var version = Maps.of(dsVersion);
				var repos = dsVersion.repos.stream().map(dsRepo -> {
					var repo = Maps.of(dsRepo);
					var repository = repositories.get(dsRepo.path);
					if (repository == null)
						return null;
					repo.put("label", repository.getLabel());
					if (!loggedIn) {
						Maps.nullify(repo, "commitId", "commitMessage");
					}
					return repo;
				}).filter(Objects::nonNull).toList();
				if (repos.isEmpty())
					return null;
				version.put("repos", repos);
				return version;
			}).filter(Objects::nonNull).toList();
			if (versions.isEmpty())
				return null;
			entry.put("versions", versions);
			return entry;
		}).filter(Objects::nonNull).toList();
	}

	@GetMapping("usage/{refId}")
	public SearchResult<Map<String, Object>> searchUsage(
			@PathVariable String refId,
			@RequestParam String repositoryId,
			@RequestParam(required = false) String field,
			@RequestParam(required = false) String filter,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize) {
		if (!settings.searchConfig.isUsageSearchEnabled())
			throw Response.unavailable("Show usage feature not enabled or search cluster not available");
		try (var repo = repoService.get(repositoryId)) {
			return usageService.query(repo, refId, field, page, pageSize, filter);
		}
	}

	private String removeStringFilter(String name, Map<String, Set<String>> filters) {
		return removeFilter(name, filters, "");
	}

	private int removeIntFilter(String name, Map<String, Set<String>> filters, int defaultValue) {
		var value = removeFilter(name, filters, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}

	private static String removeFilter(String name, Map<String, Set<String>> filters, String defaultValue) {
		var value = filters.remove(name);
		if (value == null)
			return defaultValue;
		if (value.size() == 0)
			return defaultValue;
		var first = value.iterator().next();
		if (Strings.nullOrEmpty(first))
			return defaultValue;
		return first;
	}

	private Map<String, Set<String>> getQueryParameters(HttpServletRequest request) {
		var filters = new HashMap<String, Set<String>>();
		for (var key : request.getParameterMap().keySet()) {
			var filterBy = filters.get(key);
			if (filterBy == null)
				filters.put(UriUtils.decode(key, "UTF-8"), filterBy = new HashSet<>());
			var values = request.getParameterMap().get(key);
			if (values == null)
				continue;
			for (String value : values) {
				filterBy.add(UriUtils.decode(value, "UTF-8"));
			}
		}
		return filters;
	}

}
