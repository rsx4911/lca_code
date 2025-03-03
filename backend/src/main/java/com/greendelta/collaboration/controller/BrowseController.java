package com.greendelta.collaboration.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.util.Strings;
import org.openlca.util.TypedRefIdMap;
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
			if ("LIBRARY".equals(categoryPath)) {
				categoryPath = RepositoryInfo.FILE_NAME;
			}
			var data = getEntries(repo, commit, categoryPath);
			var paged = SearchResults.pagedAndFiltered(page, pageSize, filter, data, "name");
			var user = userService.getCurrentUser();
			var loggedIn = user.id != 0;
			for (var entry : paged.data) {
				if (loggedIn) {
					var eCommit = getCommit(entry, repo);
					entry.put("commitId", eCommit.id);
					entry.put("commitMessage", eCommit.message);
					entry.put("commitTimestamp", eCommit.timestamp);
				}
				if (Maps.getBoolean(entry, "isDataset"))
					continue;
				var eName = Maps.getString(entry, "name");
				var entryPath = Strings.nullOrEmpty(categoryPath) ? eName : categoryPath + "/" + eName;
				if (Maps.getBoolean(entry, "isRepositoryInfo")) {
					entry.put("count",
							repo.references.find().includeCategories().includeLibraries().nonRecursive()
									.commit(commit.id).path(entryPath).count());
				} else if (!Maps.getBoolean(entry, "isLibrary")) {
					entry.put("count", repo.references.find().commit(commit.id).path(entryPath).count());
				}
			}
			return paged;
		}

	}

	private List<Map<String, Object>> getEntries(Repository repo, Commit commit, String categoryPath) {
		var entries = repo.references.find().includeCategories().includeLibraries().nonRecursive().commit(commit.id)
				.path(categoryPath).all();
		var mapped = entries.stream().map(e -> MetaData.get(e, repo, libraryService));
		if (!Strings.nullOrEmpty(categoryPath))
			return MetaData.sortByName(mapped).toList();
		List<String> typesHidden = settings.get(ServerSetting.MODEL_TYPES_HIDDEN, new ArrayList<>());
		entries = entries.stream().filter(e -> e.type == null || !typesHidden.contains(e.type.name())).toList();
		List<String> typesOrder = settings.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<>());
		return MetaData.sortByType(mapped, typesOrder).toList();
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
			var libraries = repo.getLibraries(commit);
			if (libraries.contains(library))
				continue;
			if ((i + 1) < commits.size())
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
		if (commitId != null && commitId.contains("-"))
			throw Response.notFound(type + " " + refId + " not found for commit " + commitId);
		try (var repo = repoService.get(group, name)) {
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound(type + " " + refId + " not found");
			var ref = repo.references.get(type, refId, commit.id);
			if (ref == null) {
				var previousCommitId = repo.commits.find().before(commit.id).latestId();
				if (repo.references.get(type, refId, previousCommitId) == null)
					throw Response.notFound(type + " " + refId + " not found for commit " + commitId);
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
			new CategoryInfo(repo, commitId).addTo(map);
			return map;
		}
	}

	private class CategoryInfo {

		private final TypedRefIdMap<Reference> refs = new TypedRefIdMap<>();
		private final Set<String> categories = new HashSet<>();

		private CategoryInfo(Repository repo, String commitId) {
			repo.references.find().includeCategories().commit(commitId).iterate(ref -> {
				if (ref.isDataset) {
					refs.put(ref, ref);
				} else if (ref.isCategory) {
					categories.add(ref.path);
				}
			});
		}

		private void addTo(Map<String, Object> object) {
			for (var key : new HashSet<>(object.keySet())) {
				if (key.equals("category")) {
					var category = Maps.getString(object, "category");
					var type = Maps.getModelType(object);
					object.put("categoryIsInRepo", isCategoryInRepo(type, category));
				} else if (Maps.isObject(object, key)) {
					_addTo(Maps.getObject(object, key));
				} else if (Maps.isArray(object, key)) {
					for (var child : Maps.getArray(object, key)) {
						if (Maps.is(child)) {
							_addTo(Maps.of(child));
						}
					}
				}
			}
		}

		private void _addTo(Map<String, Object> object) {
			var type = Maps.getModelType(object);
			var refId = Maps.getString(object, "@id");
			if (type == null || Strings.nullOrEmpty(refId)) {
				addTo(object);
				return;
			}
			var ref = refs.get(type, refId);
			object.put("isInRepo", ref != null);
			if (ref != null) {
				object.put("category", ref.category);
			}
			addTo(object);
		}

		private boolean isCategoryInRepo(ModelType type, String category) {
			if (type == null || Strings.nullOrEmpty(category))
				return false;
			return categories.contains(type.name() + "/" + category);
		}

	}
}
