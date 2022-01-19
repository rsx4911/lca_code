package com.greendelta.collaboration.controller.user;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@RestController
@RequestMapping("ws/history")
public class HistoryController {

	private final RepositoryService repoService;
	private final UserService userService;
	private final AccessService accessService;
	private final SettingsService settingsService;

	@Autowired
	public HistoryController(RepositoryService repoService, UserService userService, AccessService accessService,
			SettingsService settingsService) {
		this.repoService = repoService;
		this.userService = userService;
		this.accessService = accessService;
		this.settingsService = settingsService;
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public ResponseEntity<List<ObjectMap>> getCommitHistory(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId) {
		try (var repo = repoService.get(group, name)) {
			var commits = repo.commits().find().model(type, refId).all();
			if (commits.size() == 0)
				return Response.noContent();
			Collections.reverse(commits);
			return Response.ok(putUserName(commits));
		}
	}

	@GetMapping("{group}/{name}")
	public ResponseEntity<List<ObjectMap>> getCommitHistory(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam(name = "path", required = false) String path,
			@RequestParam(name = "lastCommitId", required = false) String lastCommitId) {
		try (var repo = repoService.get(group, name)) {
			if (lastCommitId != null && !lastCommitId.isEmpty()) {
				var commit = repo.commits().get(lastCommitId);
				if (commit == null)
					throw Response.notFound("Commit " + lastCommitId + " not found");
			}
			var commits = repo.commits().find().after(lastCommitId).path(path).all();
			if (commits.size() == 0)
				return Response.noContent();
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
			var commits = repo.commits().find().all();
			if (commits.size() == 0)
				return Response.noContent();
			Collections.reverse(commits);
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, commits, (c) -> c.message);
			var converted = SearchResults.convert(result, c -> ObjectMap.fromObject(c));
			return Response.ok(putAdditionalInfo(converted, repo, commits));
		}
	}

	private Map<String, Object> putAdditionalInfo(SearchResult<ObjectMap> result, Repository repo,
			List<Commit> commits) {
		var groupCount = new HashMap<String, Integer>();
		result = SearchResults.convert(result, this::putUserName);
		result.data.forEach(commitData -> {
			var count = commits.stream()
					.filter(c -> isSameDay(commitData.getLong("timestamp"), c.timestamp))
					.toList().size();
			var commitId = commitData.getString("id");
			groupCount.put(commitId, count);
			var commit = repo.commits().get(commitId);
			var diffs = repo.diffs().find().withPrevious(commit.id).all();
			commitData.put("additions", Diff.filter(diffs, DiffType.ADDED).size());
			commitData.put("deletions", Diff.filter(diffs, DiffType.DELETED).size());
			commitData.put("updates", Diff.filter(diffs, DiffType.MODIFIED).size());
		});
		var map = ObjectMap.fromObject(result);
		map.put("resultInfo.groupCount", groupCount);
		return map;
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
			var commit = repo.commits().get(commitId);
			if (commit == null)
				throw Response.notFound();
			var map = putUserName(commit);
			var diffs = repo.diffs().find().withPrevious(commit.id).all();
			map.put("additions", Diff.filter(diffs, DiffType.ADDED).size());
			map.put("deletions", Diff.filter(diffs, DiffType.DELETED).size());
			map.put("updates", Diff.filter(diffs, DiffType.MODIFIED).size());
			map.put("canCreateChangeLog", accessService.canCreateChangeLog(repo.path()));
			return map;
		}
	}

	@GetMapping("references/{group}/{name}/{commitId}")
	public SearchResult<ObjectMap> getReferences(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("commitId") String commitId,
			@RequestParam(name = "type", required = false) ModelType type,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "filter", required = false) String filter) {
		try (var repo = repoService.get(group, name)) {
			var commit = repo.commits().get(commitId);
			if (commit == null)
				throw Response.notFound();
			var refs = repo.diffs().find().type(type).withPrevious(commit.id).all();
			var mapped = refs.stream().map(r -> MetaData.forBrowse(r.right, r.type, repo));
			List<String> typesOrder = settingsService.get(ServerSetting.MODEL_TYPES_ORDER);
			mapped = MetaData.sortByTypeAndName(mapped, typesOrder);
			return SearchResults.pagedAndFiltered(page, pageSize, filter, mapped.toList());
		}
	}

	private List<ObjectMap> putUserName(List<Commit> commits) {
		return commits.stream().map(c -> putUserName(c)).toList();
	}

	private ObjectMap putUserName(Commit commit) {
		return putUserName(ObjectMap.fromObject(commit));
	}

	private ObjectMap putUserName(ObjectMap map) {
		var user = userService.getForUsername(map.getString("user"));
		map.put("userDisplayName", user != null ? user.name : map.getString("user"));
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
			var lastCommit = repo.commits().find().model(type, refId).before(commitId).latest();
			if (lastCommit == null || lastCommit.id.equals(commitId))
				throw Response.notFound("No previous commit found for " + type.name() + " " + refId);
			return lastCommit.id;
		}
	}

}
