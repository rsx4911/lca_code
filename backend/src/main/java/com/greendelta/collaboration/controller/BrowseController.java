package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Entry.EntryType;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.HistoryService;
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
	private final HistoryService historyService;
	private final SettingsService settings;

	public BrowseController(RepositoryService repoService, UserService userService, LibraryService libraryService,
			HistoryService historyService, SettingsService settings) {
		this.repoService = repoService;
		this.userService = userService;
		this.libraryService = libraryService;
		this.historyService = historyService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}")
	public SearchResult<Map<String, Object>> getCategoryContent(
			@PathVariable String group,
			@PathVariable String name,
			@RequestParam(defaultValue = "") String categoryPath,
			@RequestParam(required = false) String filter,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) String commitId) {
		try (var repo = repoService.get(group, name)) {
			var commit = historyService.getAccessibleCommit(repo, commitId);
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
		var entries = repo.entries.find().commit(commit.id).all();
		List<String> typesHidden = settings.get(ServerSetting.MODEL_TYPES_HIDDEN, new ArrayList<>());
		var info = repo.getInfo(commit);
		if (info != null && !info.libraries().isEmpty()) {
			entries.add(repo.entries.get(RepositoryInfo.FILE_NAME, commit.id));
		}
		entries = entries.stream().filter(e -> e.type == null || !typesHidden.contains(e.type.name())).toList();
		var mapped = entries.stream().map(e -> MetaData.get(e, repo));
		List<String> typesOrder = settings.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<>());
		return MetaData.sortByType(mapped, typesOrder).map(map -> {
			if (map.get("type") == null) {
				map.put("type", "LIBRARY");
			}
			return map;
		}).toList();
	}

	private List<Map<String, Object>> getLibraryEntries(Repository repo, Commit commit) {
		var info = repo.getInfo(commit);
		if (info == null || info.libraries().isEmpty())
			return new ArrayList<>();
		var mapped = info.libraries().stream().map(
				lib -> createLibraryEntry(lib.id(), commit.id));
		return MetaData.sortByName(mapped).toList();
	}

	private Map<String, Object> createLibraryEntry(String lib, String commitId) {
		var map = Maps.of("path", RepositoryInfo.FILE_NAME + "/" + lib);
		map.put("refId", lib);
		map.put("type", "LIBRARY");
		map.put("typeOfEntry", "LIBRARY");
		map.put("name", lib);
		map.put("commitId", commitId);
		map.put("available", libraryService.get(lib) != null);
		map.put("public", libraryService.isPublic(lib));
		return map;
	}

	private List<Map<String, Object>> getEntries(Repository repo, Commit commit, String categoryPath) {
		var entries = repo.entries.find().commit(commit.id).path(categoryPath).all();
		var mapped = entries.stream().map(e -> MetaData.get(e, repo));
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
			boolean isPackageInfo = entry.get("path").equals(RepositoryInfo.FILE_NAME);
			if (isPackageInfo) {
				if (categoryPath.isEmpty()) {
					var info = repo.getInfo(commit);
					entry.put("count", info.libraries().size());
				}
			} else {
				var entryPath = Strings.nullOrEmpty(categoryPath) ? name : categoryPath + "/" + name;
				entry.put("count", repo.references.find().commit(commit.id).path(entryPath).count());
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
		if (!path.startsWith(RepositoryInfo.FILE_NAME + "/"))
			return historyService.getLatestAccessibleCommit(repo,
					options -> options.path(path).until(commitId));
		var library = Maps.getString(entry, "refId");
		var commits = historyService.getAccessibleCommits(repo,
				options -> options.path(RepositoryInfo.FILE_NAME).until(commitId));
		for (var i = commits.size() - 1; i >= 0; i--) {
			var commit = commits.get(i);
			var info = repo.getInfo(commit);
			var libraries = info.libraries().stream().map(LibraryLink::id).toList();
			if (libraries.contains(library))
				continue;
			return commits.get(i + 1);
		}
		return commits.get(0);
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public Map<String, Object> getData(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable ModelType type,
			@PathVariable String refId,
			@RequestParam(required = false) String commitId) {
		if (commitId != null && commitId.contains("?gladview")) {
			// TODO this is a quickfix to support broken glad urls
			commitId = commitId.substring(0, commitId.indexOf("?gladview"));
		}
		try (var repo = repoService.get(group, name)) {
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound(notFoundMessage(type, refId, null));
			var ref = repo.references.get(type, refId, commit.id);
			if (ref == null) {
				var previousCommitId = repo.commits.find().before(commit.id).latestId();
				if (repo.references.get(type, refId, previousCommitId) == null)
					throw Response.notFound(notFoundMessage(type, refId, commitId));
			}
			var dataset = repo.datasets.get(ref);
			if (Strings.nullOrEmpty(dataset))
				return Map.of(
						"@type", type.getModelClass().getSimpleName(),
						"@id", refId,
						"commitId", commitId != null ? commitId : "",
						"deleted", true);
			var map = Maps.of(dataset);
			map.put("commitId", commit.id);
			new IsInRepoInfo(repo, commitId).addIn(map);
			return map;
		}
	}

	private String notFoundMessage(ModelType type, String refId, String commitId) {
		if (Strings.nullOrEmpty(commitId))
			return type + " " + refId + " not found";
		return type + " " + refId + " not found for commit " + commitId;
	}

	private class IsInRepoInfo {

		private final EnumMap<ModelType, Set<String>> refs = new EnumMap<>(ModelType.class);
		private final Set<String> categories = new HashSet<>();

		private IsInRepoInfo(Repository repo, String commitId) {
			repo.entries.iterate(commitId, entry -> {
				if (entry.typeOfEntry == EntryType.DATASET) {
					refs.computeIfAbsent(entry.type, key -> new HashSet<>()).add(entry.refId);
				} else if (entry.typeOfEntry == EntryType.CATEGORY) {
					categories.add(entry.path);
				}
			});
		}

		private void addIn(Map<String, Object> object) {
			for (var key : new HashSet<>(object.keySet())) {
				if (key.equals("category")) {
					var category = Maps.getString(object, "category");
					var type = Maps.getModelType(object);
					object.put("categoryIsInRepo", isCategoryInRepo(type, category));
				}
				if (Maps.isObject(object, key)) {
					addTo(Maps.getObject(object, key));
				} else if (Maps.isArray(object, key)) {
					for (var child : Maps.getArray(object, key)) {
						if (Maps.is(child)) {
							addTo(Maps.of(child));
						}
					}
				}
			}
		}

		private void addTo(Map<String, Object> object) {
			addIn(object);
			var type = Maps.getModelType(object);
			var refId = Maps.getString(object, "@id");
			if (type == null || Strings.nullOrEmpty(refId))
				return;
			object.put("isInRepo", refs.getOrDefault(type, Collections.emptySet()).contains(refId));
		}

		private boolean isCategoryInRepo(ModelType type, String category) {
			if (type == null || Strings.nullOrEmpty(category))
				return false;
			return categories.contains(type.name() + "/" + category);
		}

	}

}
