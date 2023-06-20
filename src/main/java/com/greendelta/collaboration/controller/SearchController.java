package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.Direction;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.error.ForbiddenAccessException;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.DsEntry;
import com.greendelta.collaboration.service.search.IndexService;
import com.greendelta.collaboration.service.search.InputOutputDataService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchResult;

@RestController
@RequestMapping("ws/public/search")
public class SearchController {

	private static final Logger log = LogManager.getLogger(SearchController.class);
	private final SearchService service;
	private final RepositoryService repoService;
	private final GroupService groupService;
	private final UserService userService;
	private final InputOutputDataService ioDataService;
	private final IndexService indexService;
	private final SettingsService settings;

	@Autowired
	public SearchController(SearchService service, RepositoryService repoService, GroupService groupService,
			UserService userService, InputOutputDataService ioDataService, IndexService indexService,
			SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.groupService = groupService;
		this.userService = userService;
		this.ioDataService = ioDataService;
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
		var result = service.search(query, page, pageSize, parameters);
		result.aggregations.stream()
				.filter(r -> r.name.equals(Aggregations.CATEGORY.name))
				.forEach(r -> r.group("/"));
		return map(result);
	}

	private Map<String, Object> map(SearchResult<DsEntry> result) {
		try (var accessible = repoService.getAllAccessible()) {
			var repositories = accessible.stream()
					.collect(Collectors.toMap(repo -> repo.path(), repo -> repo));
			var loggedIn = userService.getCurrentUser().id != 0;
			var map = Maps.create();
			var resultInfo = Maps.of(result.resultInfo);
			resultInfo.put("indexing", indexService.getIndexingStatus() != null);
			map.put("resultInfo", resultInfo);
			var data = result.data.stream().map(dsEntry -> {
				var e = Maps.of(dsEntry);
				var versions = new ArrayList<Map<String, Object>>();
				dsEntry.versions.forEach(dsVersion -> {
					var v = Maps.of(dsVersion);
					var repos = new ArrayList<Map<String, Object>>();
					dsVersion.repos.forEach(dsRepo -> {
						var r = Maps.of(dsRepo);
						var repo = repositories.get(dsRepo.path);
						if (repo == null)
							return;
						r.put("label", repo.getLabel());
						if (!loggedIn) {
							Maps.nullify(r, "commitId", "commitMessage");
						}
						repos.add(r);
					});
					v.put("repos", repos);
					versions.add(v);
				});
				e.put("versions", versions);
				return e;
			}).toList();
			map.put("data", data);
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
					aMap.put("entries", a.entries.stream().map(e -> {
						Map<String, Object> eMap = Maps.of(e);
						Repository repo = repositories.get(e.key);
						if (repo != null) {
							try {
								eMap.put("label", repo.settings.get(RepositorySetting.LABEL, repo.name));
							} catch (ForbiddenAccessException ex) {
								// ignore
							}
						}
						return eMap;
					}).toList());
				} else if (a.name.equals(Aggregations.GROUP.name)) {
					aMap.put("entries", a.entries.stream().map(e -> {
						Map<String, Object> eMap = Maps.of(e);
						try {
							String label = groupService.getSettings(e.key).get(GroupSetting.LABEL, e.key);
							eMap.put("label", label);
						} catch (ForbiddenAccessException ex) {
							// ignore
						}
						return eMap;
					}).toList());
				}
				return aMap;
			}).toList());
			return map;
		}
	}

	@GetMapping("flowLinks/{flowRefId}")
	public SearchResult<Map<String, Object>> searchFlowLinks(
			@PathVariable("flowRefId") String flowRefId,
			@RequestParam(name = "repositoryId") String repositoryId,
			@RequestParam(name = "direction", required = false) Direction direction,
			@RequestParam(name = "commitId", required = false) String commitId,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		if (!settings.searchConfig.isIoDataAvailable())
			throw Response.unavailable("Search links feature not enabled or search cluster not available");
		try (var repo = repoService.get(repositoryId)) {
			if (commitId == null) {
				commitId = repo.commits().find().latestId();
			}
			if (commitId == null)
				throw Response.notFound();
			var commit = repo.commits().get(commitId);
			var result = ioDataService.query(repo, commit, flowRefId, direction, page, pageSize, filter);
			return SearchResults.convert(result, entry -> addProcessInfo(repo, commit, entry));
		}
	}

	private Map<String, Object> addProcessInfo(Repository repo, Commit commit, Map<String, Object> entry) {
		var refId = Maps.getString(entry, "refId");
		var ref = repo.references().get(ModelType.PROCESS, refId, commit.id);
		entry.put("type", ModelType.PROCESS.name());
		return MetaData.forBrowse(entry, ref, repo);
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
