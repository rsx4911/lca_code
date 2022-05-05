package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Entry.EntryType;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@RestController
@RequestMapping("ws/public/browse")
public class BrowseController {

	private final RepositoryService repoService;
	private final UserService userService;
	private final SettingsService settingsService;

	@Autowired
	public BrowseController(RepositoryService repoService, UserService userService, SettingsService settingsService) {
		this.repoService = repoService;
		this.userService = userService;
		this.settingsService = settingsService;
	}

	@GetMapping("{group}/{name}")
	public SearchResult<Map<String, Object>> getCategoryContent(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam(name = "categoryPath", required = false) String categoryPath,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "commitId", required = false) String commitId) {
		var path = categoryPath != null ? categoryPath : "";
		try (var repo = repoService.get(group, name)) {
			var entries = repo.entries().find().commit(commitId).path(categoryPath).all();
			if (path.isEmpty()) {
				List<String> typesHidden = settingsService.get(ServerSetting.MODEL_TYPES_HIDDEN, new ArrayList<>());
				entries = entries.stream().filter(e -> !typesHidden.contains(e.type.name())).toList();
			}
			var mapped = entries.stream().map(e -> MetaData.forBrowse(e, repo));
			if (!path.isEmpty()) {
				mapped = MetaData.sortByName(mapped);
			} else {
				List<String> typesOrder = settingsService.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<>());
				mapped = MetaData.sortByType(mapped, typesOrder);
			}
			var paged = SearchResults.pagedAndFiltered(page, pageSize, filter, mapped.toList(),
					m -> Maps.getString(m, "name"));
			putOtherInfo(paged.data, repo, commitId, categoryPath);
			return paged;
		}
	}

	private void putOtherInfo(List<Map<String, Object>> entries, Repository repo, String commitId,
			String categoryPath) {
		var user = userService.getCurrentUser();
		var loggedIn = user.id != 0;
		var commits = new HashMap<String, Commit>();
		entries.forEach(entry -> {
			var entryPath = Strings.nullOrEmpty(categoryPath)
					? Maps.getString(entry, "name")
					: categoryPath + "/" + Maps.getString(entry, "name");
			if (loggedIn) {
				putCommitInfo(entry, repo, commits);
			}
			if (!Maps.getString(entry, "typeOfEntry").equals(EntryType.DATASET.name())) {
				entry.put("count", repo.references().find().commit(commitId).path(entryPath).count());
			}
		});
	}

	private void putCommitInfo(Map<String, Object> entry, Repository repo, Map<String, Commit> commits) {
		var commitId = Maps.getString(entry, "commitId");
		var path = Maps.getString(entry, "path");
		commitId = repo.commits().find().path(path).until(commitId).latestId();
		var commit = commits.computeIfAbsent(commitId, repo.commits()::get);
		entry.put("commitId", commit.id);
		entry.put("commitMessage", commit.message);
		entry.put("commitTimestamp", commit.timestamp);
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public Map<String, Object> getData(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@RequestParam(name = "commitId", required = false) String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = repo.commits().find().model(type, refId).until(commitId).latest();
			if (commit == null)
				throw Response.notFound(type + " " + refId + " not found for commit " + commitId);
			var modelCommitId = repo.commits().find().model(type, refId).latestId();
			if (commitId == null) {
				commitId = modelCommitId;
			}
			var loggedIn = userService.getCurrentUser().id != 0;
			if (!loggedIn && !commit.id.equals(commitId))
				throw Response.unauthorized();
			var ref = repo.references().get(type, refId, commit.id);
			var dataset = repo.datasets().get(ref);
			if (Strings.nullOrEmpty(dataset))
				throw Response.notFound(type + " " + refId + " not found for commit " + commitId);
			var map = Maps.of(dataset);
			if (loggedIn) {
				map.put("commitId", modelCommitId);
			}
			return map;
		}
	}

}
