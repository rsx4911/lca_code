package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.SearchResults;

@RestController
@RequestMapping("ws/public/history")
public class HistoryController {

	private final HistoryService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final PermissionsService permissions;
	private final ReleaseService releaseService;
	private final LibraryService libraryService;
	private final SettingsService settings;

	public HistoryController(HistoryService service, RepositoryService repoService, UserService userService,
			PermissionsService permissions, ReleaseService releaseService, LibraryService libraryService,
			SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.permissions = permissions;
		this.releaseService = releaseService;
		this.libraryService = libraryService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public ResponseEntity<List<Map<String, Object>>> getCommitHistory(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable ModelType type,
			@PathVariable String refId) {
		try (var repo = repoService.get(group, name)) {
			var commits = service.getAccessibleCommits(repo,
					options -> options.model(type, refId));
			Collections.reverse(commits);
			return Response.ok(putAdditionalInfo(repo, commits));
		}
	}

	@GetMapping("{group}/{name}")
	public ResponseEntity<List<Map<String, Object>>> getCommitHistory(
			@PathVariable String group,
			@PathVariable String name,
			@RequestParam(required = false) String path) {
		try (var repo = repoService.get(group, name)) {
			var p = "LIBRARY".equals(path)
					? RepositoryInfo.FILE_NAME
					: path;
			var commits = service.getAccessibleCommits(repo, options -> options.path(p));
			Collections.reverse(commits);
			return Response.ok(putAdditionalInfo(repo, commits));
		}
	}

	@GetMapping("search/{group}/{name}")
	public ResponseEntity<Map<String, Object>> getCommitHistory(
			@PathVariable String group,
			@PathVariable String name,
			@RequestParam(required = false) String filter,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize) {
		try (var repo = repoService.get(group, name)) {
			var commits = service.getAccessibleCommits(repo);
			Collections.reverse(commits);
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, commits, c -> c.message);
			var converted = SearchResults.convert(result, Maps::of);
			var groupCount = new HashMap<String, Integer>();
			converted = SearchResults.convert(converted, mapped -> putAdditionalInfo(repo, mapped));
			converted.data.forEach(commitData -> {
				var count = commits.stream()
						.filter(c -> isSameDay(Maps.getLong(commitData, "timestamp"), c.timestamp))
						.toList().size();
				var commitId = Maps.getString(commitData, "id");
				groupCount.put(commitId, count);
			});
			var map = Maps.of(converted);
			Maps.put(map, "resultInfo.groupCount", groupCount);
			return Response.ok(map);
		}
	}

	private boolean isSameDay(long d1, long d2) {
		var c1 = Calendar.getInstance();
		c1.setTimeInMillis(d1);
		var c2 = Calendar.getInstance();
		c2.setTimeInMillis(d2);
		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
			return false;
		return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

	@GetMapping("commit/{group}/{name}/{commitId}")
	public Map<String, Object> getCommit(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = service.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound();
			var map = putAdditionalInfo(repo, commit);
			map.put("canCreateChangeLog", permissions.canCreateChangeLogOf(repo.path()));
			putCount(map, repo, commit);
			return map;
		}
	}

	@GetMapping("count/{group}/{name}/{commitId}")
	public Map<String, Object> getCount(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = service.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound();
			var map = new HashMap<String, Object>();
			putCount(map, repo, commit);
			return map;
		}
	}

	private void putCount(Map<String, Object> map, Repository repo, Commit commit) {
		var previousCommit = service.getLatestAccessibleCommit(repo, options -> options.before(commit.id));
		var diffs = repo.diffs.find().unsorted().commit(previousCommit).with(commit);
		map.put("id", commit.id);
		map.put("additions", Diff.filter(diffs, DiffType.ADDED).size());
		map.put("deletions", Diff.filter(diffs, DiffType.DELETED).size());
		map.put("updates", Diff.filter(diffs, DiffType.MODIFIED, DiffType.MOVED).size());
	}

	@GetMapping("references/{group}/{name}/{commitId}")
	public Map<String, Object> getReferences(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId,
			@RequestParam(required = false) String categoryPath,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) String filter) {
		try (var repo = repoService.get(group, name)) {
			var commit = service.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound();
			if ("LIBRARY".equals(categoryPath)) {
				categoryPath = RepositoryInfo.FILE_NAME;
			}
			var previousCommit = service.getLatestAccessibleCommit(repo, options -> options.before(commit.id));
			var diff = repo.diffs.find().commit(previousCommit);
			if (!Strings.nullOrEmpty(categoryPath)) {
				if (categoryPath.equals(ModelType.CATEGORY.name())) {
					diff = diff.onlyCategories();
				} else {
					diff = diff.filter(categoryPath).excludeCategories();
				}
			}
			var diffs = diff.with(commit);
			var mapped = diffs.stream().map(d -> MetaData.get(d, repo, libraryService));
			List<String> typesOrder = settings.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<>());
			mapped = MetaData.sortByTypeAndName(mapped, typesOrder);
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, mapped.toList());
			var map = Maps.of(result);
			map.put("modelTypes", getModelTypes(result.data));
			return map;
		}
	}

	private Set<String> getModelTypes(List<Map<String, Object>> diffs) {
		var types = new HashSet<String>();
		diffs.forEach(d -> {
			var type = Maps.getString(d, "type");
			if (Maps.getBoolean(d, "isCategory")) {
				types.add(ModelType.CATEGORY.name());
			} else {
				types.add(type);
			}
		});
		return types;
	}

	private List<Map<String, Object>> putAdditionalInfo(Repository repo, List<Commit> commits) {
		return commits.stream().map(c -> putAdditionalInfo(repo, c)).toList();
	}

	private Map<String, Object> putAdditionalInfo(Repository repo, Commit commit) {
		return putAdditionalInfo(repo, Maps.of(commit));
	}

	private Map<String, Object> putAdditionalInfo(Repository repo, Map<String, Object> map) {
		var user = userService.getForUsernameOrEmail(Maps.getString(map, "user"));
		var id = Maps.getString(map, "id");
		var isReleased = releaseService.isReleased(repo.path(), id);
		map.put("userDisplayName", user != null ? user.name : Maps.getString(map, "user"));
		map.put("isReleased", isReleased);
		if (isReleased) {
			var releaseInfo = Maps.of(releaseService.get(repo.path(), id));
			Maps.remove(releaseInfo, "id");
			map.put("releaseInfo", releaseInfo);
		}
		return map;
	}

}
