package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.git.find.Entries;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Entry.EntryType;
import org.openlca.git.util.Repositories;
import org.openlca.jsonld.PackageInfo;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.LibraryService;
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
	private final LibraryService libraryService;
	private final SettingsService settings;

	@Autowired
	public BrowseController(RepositoryService repoService, UserService userService, LibraryService libraryService,
			SettingsService settings) {
		this.repoService = repoService;
		this.userService = userService;
		this.libraryService = libraryService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}")
	public SearchResult<Map<String, Object>> getCategoryContent(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam(name = "categoryPath", defaultValue = "") String categoryPath,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "commitId", required = false) String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = repo.commits().find().until(commitId).latest();
			if (commit == null)
				return SearchResults.from(new ArrayList<>(), page, pageSize, 0);
			var data = switch (categoryPath) {
				case "" -> getModelTypeEntries(repo, commit);
				case "LIBRARY" -> getLibraryEntries(repo, commit);
				default -> getEntries(repo, commit, categoryPath);
			};
			var paged = SearchResults.pagedAndFiltered(page, pageSize, filter, data, "name");
			putOtherInfo(paged.data, repo, commit, categoryPath);
			return paged;
		}
	}

	private List<Map<String, Object>> getModelTypeEntries(Repository repo, Commit commit) {
		var entries = repo.entries().find().commit(commit.id).all();
		List<String> typesHidden = settings.get(ServerSetting.MODEL_TYPES_HIDDEN, new ArrayList<String>());
		var info = Repositories.infoOf(repo.gitRepo(), commit);
		if (info != null && !info.libraries().isEmpty()) {
			entries.add(Entries.of(repo.gitRepo()).get(PackageInfo.FILE_NAME, commit.id));
		}
		entries = entries.stream().filter(e -> e.type == null || !typesHidden.contains(e.type.name())).toList();
		var mapped = entries.stream().map(e -> MetaData.forBrowse(e, repo));
		List<String> typesOrder = settings.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<String>());
		return MetaData.sortByType(mapped, typesOrder).map(map -> {
			if (map.get("type") == null) {
				map.put("type", "LIBRARY");
			}
			return map;
		}).toList();
	}

	private List<Map<String, Object>> getLibraryEntries(Repository repo, Commit commit) {
		var info = Repositories.infoOf(repo.gitRepo(), commit);
		if (info == null || info.libraries().isEmpty())
			return new ArrayList<>();
		var mapped = info.libraries().stream().map(
				lib -> createLibraryEntry(lib, commit.id));
		return MetaData.sortByName(mapped).toList();
	}

	private Map<String, Object> createLibraryEntry(String lib, String commitId) {
		var map = Maps.of("path", PackageInfo.FILE_NAME + "/" + lib);
		map.put("refId", lib);
		map.put("type", "LIBRARY");
		map.put("typeOfEntry", "LIBRARY");
		map.put("name", lib);
		map.put("commitId", commitId);
		map.put("available", libraryService.get(lib) != null);
		return map;
	}

	private List<Map<String, Object>> getEntries(Repository repo, Commit commit, String categoryPath) {
		var entries = repo.entries().find().commit(commit.id).path(categoryPath).all();
		var mapped = entries.stream().map(e -> MetaData.forBrowse(e, repo));
		return MetaData.sortByName(mapped).toList();
	}

	private void putOtherInfo(List<Map<String, Object>> entries, Repository repo, Commit commit, String categoryPath) {
		var user = userService.getCurrentUser();
		var loggedIn = user.id != 0;
		for (var entry : entries) {
			var name = Maps.getString(entry, "name");
			if (loggedIn) {
				putCommitInfo(entry, repo);
			}
			if (entry.get("typeOfEntry").equals(EntryType.DATASET.name()))
				continue;
			boolean isPackageInfo = entry.get("path").equals(PackageInfo.FILE_NAME);
			if (isPackageInfo) {
				if (categoryPath.isEmpty()) {
					var info = Repositories.infoOf(repo.gitRepo(), commit);
					entry.put("count", info.libraries().size());
				}
			} else {
				var entryPath = Strings.nullOrEmpty(categoryPath) ? name : categoryPath + "/" + name;
				entry.put("count", repo.references().find().commit(commit.id).path(entryPath).count());
			}
		}
	}

	private void putCommitInfo(Map<String, Object> entry, Repository repo) {
		var commit = getCommit(entry, repo);
		entry.put("commitId", commit.id);
		entry.put("commitMessage", commit.message);
		entry.put("commitTimestamp", commit.timestamp);
	}

	private Commit getCommit(Map<String, Object> entry, Repository repo) {
		var path = Maps.getString(entry, "path");
		var commitId = Maps.getString(entry, "commitId");
		if (!path.startsWith(PackageInfo.FILE_NAME + "/"))
			return repo.commits().find().path(path).until(commitId).latest();
		var library = Maps.getString(entry, "refId");
		var commits = repo.commits().find().path(PackageInfo.FILE_NAME).until(commitId).all();
		for (var i = commits.size() - 1; i >= 0; i--) {
			var commit = commits.get(i);
			var info = Repositories.infoOf(repo.gitRepo(), commit);
			if (info.libraries().contains(library))
				continue;
			return commits.get(i + 1);
		}
		return commits.get(0);
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
