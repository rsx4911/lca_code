package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.springframework.http.ResponseEntity;
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
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.SearchResults;

@RestController
@RequestMapping("ws/history")
public class HistoryController {

	private final RepositoryService repoService;
	private final UserService userService;
	private final AccessService accessService;
	private final SettingsService settings;

	public HistoryController(RepositoryService repoService, UserService userService, AccessService accessService,
			SettingsService settings) {
		this.repoService = repoService;
		this.userService = userService;
		this.accessService = accessService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public ResponseEntity<List<Map<String, Object>>> getCommitHistory(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId) {
		try (var repo = repoService.get(group, name)) {
			var commits = repo.commits.find().model(type, refId).all();
			if (commits.size() == 0)
				return Response.noContent();
			Collections.reverse(commits);
			return Response.ok(putUserName(commits));
		}
	}

	@GetMapping("{group}/{name}")
	public ResponseEntity<List<Map<String, Object>>> getCommitHistory(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam(name = "path", required = false) String path,
			@RequestParam(name = "lastCommitId", required = false) String lastCommitId) {
		try (var repo = repoService.get(group, name)) {
			if (lastCommitId != null && !lastCommitId.isEmpty()) {
				var commit = repo.commits.get(lastCommitId);
				if (commit == null)
					throw Response.notFound("Commit " + lastCommitId + " not found");
			}
			var commits = repo.commits.find().after(lastCommitId).path(path).all();
			Collections.reverse(commits);
			return Response.ok(putUserName(commits));
		}
	}

	@GetMapping("search/{group}/{name}")
	public ResponseEntity<Map<String, Object>> getCommitHistory(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		try (var repo = repoService.get(group, name)) {
			var commits = repo.commits.find().all();
			Collections.reverse(commits);
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, commits, c -> c.message);
			var converted = SearchResults.convert(result, c -> Maps.of(c));
			var groupCount = new HashMap<String, Integer>();
			converted = SearchResults.convert(converted, this::putUserName);
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
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = repo.commits.get(commitId);
			if (commit == null)
				throw Response.notFound();
			var map = putUserName(commit);
			map.put("canCreateChangeLog", accessService.canCreateChangeLog(repo.path()));
			putCount(map, repo, commit);
			return map;
		}
	}

	@GetMapping("count/{group}/{name}/{commitId}")
	public Map<String, Object> getCount(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = repo.commits.get(commitId);
			if (commit == null)
				throw Response.notFound();
			var map = new HashMap<String, Object>();
			putCount(map, repo, commit);
			return map;
		}
	}
	
	private void putCount(Map<String, Object> map, Repository repo, Commit commit) {
		var diffs = repo.diffs.find().commit(commit).withPreviousCommit();		
		map.put("id", commit.id);
		map.put("additions", Diff.filter(diffs, DiffType.ADDED).size());
		map.put("deletions", Diff.filter(diffs, DiffType.DELETED).size());
		map.put("updates", Diff.filter(diffs, DiffType.MODIFIED).size());
	}

	@GetMapping("references/{group}/{name}/{commitId}")
	public Map<String, Object> getReferences(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId,
			@RequestParam(name = "type", required = false) ModelType type,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "filter", required = false) String filter) {
		try (var repo = repoService.get(group, name)) {
			var commit = repo.commits.get(commitId);
			if (commit == null)
				throw Response.notFound();
			var diff = repo.diffs.find().commit(commit);
			if (type != null) {
				if (type == ModelType.CATEGORY) {
					diff = diff.onlyCategories();
				} else {
					diff = diff.filter(type.name()).excludeCategories();
				}
			}
			var diffs = diff.withPreviousCommit();
			var mapped = diffs.stream().map(d -> MetaData.forBrowse(d, repo));
			List<String> typesOrder = settings.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<>());
			mapped = MetaData.sortByTypeAndName(mapped, typesOrder);
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, mapped.toList());
			var map = Maps.of(result);
			map.put("modelTypes", getModelTypes(diffs));
			return map;
		}
	}

	private Set<ModelType> getModelTypes(List<Diff> diffs) {
		var types = new HashSet<ModelType>();
		diffs.forEach(d -> {
			types.add(d.type);
			if (d.isCategory) {
				types.add(ModelType.CATEGORY);
			}
		});
		return types;
	}

	private List<Map<String, Object>> putUserName(List<Commit> commits) {
		return commits.stream().map(c -> putUserName(c)).toList();
	}

	private Map<String, Object> putUserName(Commit commit) {
		return putUserName(Maps.of(commit));
	}

	private Map<String, Object> putUserName(Map<String, Object> map) {
		var user = userService.getForUsername(Maps.getString(map, "user"));
		map.put("userDisplayName", user != null ? user.name : Maps.getString(map, "user"));
		return map;
	}

	@GetMapping("previousCommitId/{group}/{name}/{type}/{refId}/{commitId}")
	public String getPreviousCommitId(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@PathVariable("commitId") String commitId) {
		try (var repo = repoService.get(group, name)) {
			var lastCommit = repo.commits.find().model(type, refId).before(commitId).latest();
			if (lastCommit == null || lastCommit.id.equals(commitId))
				throw Response.notFound("No previous commit found for " + type.name() + " " + refId);
			return lastCommit.id;
		}
	}

}
