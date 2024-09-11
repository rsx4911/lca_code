package com.greendelta.collaboration.service.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import org.openlca.git.model.Commit;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class IndexService {

	private final RepositoryService repoService;
	private final SearchService searchService;
	private final UsageService usageService;
	private final HistoryService historyService;
	private final ReleaseService releaseService;
	private final SettingsService settings;
	private final Queue<Work> workQueue = new LinkedList<>();

	public IndexService(RepositoryService repoService, SearchService searchService, UsageService usageService,
			HistoryService historyService, ReleaseService releaseService, SettingsService settings) {
		this.repoService = repoService;
		this.searchService = searchService;
		this.usageService = usageService;
		this.historyService = historyService;
		this.releaseService = releaseService;
		this.settings = settings;
	}

	private void offer(SearchIndex index, String task, Runnable actualWork) {
		offer(index, null, task, repo -> actualWork.run());
	}

	private void offer(SearchIndex index, RepositoryPath repoPath, String task, Consumer<Repository> actualWork) {
		synchronized (workQueue) {
			var repo = repoPath != null ? repoService.get(repoPath) : null;
			var isFirst = workQueue.isEmpty();
			var work = new Work(index, repo, task, actualWork);
			workQueue.offer(work);
			if (isFirst) {
				runNext();
			}
		}
	}

	private void runNext() {
		Work work;
		synchronized (workQueue) {
			work = workQueue.peek();
			if (work == null)
				return;
		}
		new Thread(() -> {
			try {
				work.run();
			} finally {
				if (work.repo != null) {
					work.repo.close();
				}
				synchronized (workQueue) {
					workQueue.poll();
					runNext();
				}
			}
		}).start();
	}

	public List<String> getIndexingTasks() {
		synchronized (workQueue) {
			if (workQueue.isEmpty())
				return null;
			return workQueue.stream().map(work -> {
				var task = switch(work.index) {
					case PUBLIC -> "[Public search] ";
					case PRIVATE -> "[Private search] ";
					case USAGE -> "[Usage search] ";
				};
				task += work.task;
				if (work.repo != null) {
					task += " " + work.repo.path();
				}
				return task;
			}).toList();
		}
	}

	public boolean isBeingUpdated(SearchIndex index) {
		synchronized (workQueue) {
			if (workQueue.isEmpty())
				return false;
			return workQueue.stream().filter(work -> work.index == index).count() > 0;
		}
	}

	public void clearIndexAsync() {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		if (settings.is(ServerSetting.RELEASES_ENABLED)) {
			offer(SearchIndex.PUBLIC, "Clearing index", searchService.on(SearchIndex.PUBLIC)::clear);
		}
		offer(SearchIndex.PRIVATE, "Clearing index", searchService.on(SearchIndex.PRIVATE)::clear);
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			offer(SearchIndex.USAGE, "Clearing index", usageService::clearIndex);
		}
	}

	public void indexPrivateAsync(RepositoryPath path, Commit previousCommit, Commit commit) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		if (commit == null)
			return;
		offer(SearchIndex.PRIVATE, path, "Indexing", repo -> {
			List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
			searchService.on(SearchIndex.PRIVATE).index(repo, tags, previousCommit, commit);
		});
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			offer(SearchIndex.USAGE, path, "Indexing", repo -> {
				usageService.index(repo, previousCommit, commit);
			});
		}
	}

	public void indexPublicAsync(RepositoryPath path, Commit previousCommit, Commit commit) {
		if (!settings.searchConfig.isSearchAvailable() || !settings.is(ServerSetting.RELEASES_ENABLED))
			return;
		if (commit == null)
			return;
		offer(SearchIndex.PUBLIC, path, "Indexing", repo -> {
			var release = releaseService.get(repo.path(), commit.id);
			searchService.on(SearchIndex.PUBLIC).index(repo, release.getTags(), previousCommit, commit);
		});
	}

	public void moveIndexAsync(RepositoryPath path, RepositoryPath newPath) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		try (var r = repoService.get(newPath)) {
			var newHead = r.commits.head();
			if (newHead == null)
				return;
			var latestRelease = historyService.getLatestReleasedCommit(r);
			if (latestRelease != null) {
				offer(SearchIndex.PUBLIC, newPath, "Moving " + path.toString() + " to", repo -> {
					searchService.on(SearchIndex.PUBLIC).move(path, repo, latestRelease);
				});
			}
			offer(SearchIndex.PRIVATE, newPath, "Moving " + path.toString() + " to",
					repo -> searchService.on(SearchIndex.PRIVATE).move(path, repo, newHead));
			if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
				offer(SearchIndex.USAGE, newPath, "Moving index of " + path.toString() + " to", repo -> {
					usageService.move(path, repo);
				});
			}
		}
	}

	public void updatePrivateTagsAsync(RepositoryPath path) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		try (var r = repoService.get(path)) {
			var head = r.commits.head();
			if (head == null)
				return;
			offer(SearchIndex.PRIVATE, path, "Updating tags", repo -> {
				List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
				searchService.on(SearchIndex.PRIVATE).updateTags(repo, head, tags);
			});
		}
	}

	public void updatePublicTagsAsync(RepositoryPath path) {
		if (!settings.searchConfig.isSearchAvailable() || !settings.is(ServerSetting.RELEASES_ENABLED))
			return;
		try (var r = repoService.get(path)) {
			var latestRelease = historyService.getLatestReleasedCommit(r);
			if (latestRelease == null)
				return;
			var release = releaseService.get(r.path(), latestRelease.id);
			offer(SearchIndex.PUBLIC, path, "Updating tags", repo -> {
				searchService.on(SearchIndex.PUBLIC).updateTags(repo, latestRelease, release.getTags());
			});
		}
	}

	public void reindexAsync(RepositoryPath path) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		try (var r = repoService.get(path)) {
			var head = r.commits.head();
			if (head == null)
				return;
			var latestRelease = historyService.getLatestReleasedCommit(r);
			if (latestRelease != null) {
				offer(SearchIndex.PUBLIC, path, "Reindexing", repo -> {
					var release = releaseService.get(r.path(), latestRelease.id);
					searchService.on(SearchIndex.PUBLIC).remove(repo, latestRelease);
					searchService.on(SearchIndex.PUBLIC).index(repo, release.getTags(), null, latestRelease);
				});
			}
			offer(SearchIndex.PRIVATE, path, "Reindexing", repo -> {
				List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
				searchService.on(SearchIndex.PRIVATE).remove(repo, head);
				searchService.on(SearchIndex.PRIVATE).index(repo, tags, null, head);
			});
			if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
				offer(SearchIndex.USAGE, path, "Reindexing", repo -> {
					usageService.remove(repo);
					usageService.index(repo, null, head);
				});
			}
		}
	}

	public void reindexAllAsync(List<RepositoryPath> paths) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		if (settings.is(ServerSetting.RELEASES_ENABLED)) {
			offer(SearchIndex.PUBLIC, "Clearing index", searchService.on(SearchIndex.PUBLIC)::clear);
			for (var path : paths) {
				try (var r = repoService.get(path)) {
					var latestRelease = historyService.getLatestReleasedCommit(r);
					if (latestRelease == null)
						continue;
					offer(SearchIndex.PUBLIC, path, "Indexing", repo -> {
						var release = releaseService.get(repo.path(), latestRelease.id);
						searchService.on(SearchIndex.PUBLIC).index(repo, release.getTags(), null, latestRelease);
					});
				}
			}
		}
		offer(SearchIndex.PRIVATE, "Clearing index", searchService.on(SearchIndex.PRIVATE)::clear);
		for (var path : paths) {
			try (var r = repoService.get(path)) {
				var head = r.commits.head();
				if (head == null)
					continue;
				offer(SearchIndex.PRIVATE, path, "Indexing", repo -> {
					List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
					searchService.on(SearchIndex.PRIVATE).index(repo, tags, null, head);
				});
			}
		}
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			offer(SearchIndex.USAGE, "Clearing index", usageService::clearIndex);
			for (var path : paths) {
				try (var r = repoService.get(path)) {
					var head = r.commits.head();
					if (head == null)
						continue;
					offer(SearchIndex.USAGE, path, "Indexing", repo -> {
						usageService.index(repo, null, head);
					});
				}
			}
		}
	}

	public void deleteIndex(Repository repo) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		var head = repo.commits.head();
		if (head == null)
			return;
		if (settings.is(ServerSetting.RELEASES_ENABLED)) {
			var latestRelease = historyService.getLatestReleasedCommit(repo);
			if (latestRelease != null) {
				searchService.on(SearchIndex.PUBLIC).remove(repo, latestRelease);
			}
		}
		searchService.on(SearchIndex.PRIVATE).remove(repo, head);
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			usageService.remove(repo);
		}
	}

	public class Work implements Runnable {

		public final SearchIndex index;
		public final Repository repo;
		public final String task;
		private final Consumer<Repository> work;

		private Work(SearchIndex index, Repository repo, String task, Consumer<Repository> work) {
			this.index = index;
			this.repo = repo;
			this.task = task;
			this.work = work;
		}

		@Override
		public void run() {
			work.accept(repo);
		}

	}

	public class IndexingStatus {

		public final List<String> tasks;

		private IndexingStatus(List<String> tasks) {
			this.tasks = tasks;
		}

	}

}