package com.greendelta.collaboration.service.search;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

	private void offer(String task, Runnable actualWork) {
		offer(null, null, null, task, repo -> actualWork.run());
	}

	private void offer(SearchIndex index, RepositoryPath repoPath, String task, Consumer<Repository> actualWork) {
		offer(index, null, repoPath, task, actualWork);
	}

	private void offer(SearchIndex index, SearchIndex secondIndex, RepositoryPath repoPath, String task,
			Consumer<Repository> actualWork) {
		synchronized (workQueue) {
			var repo = repoPath != null ? repoService.get(repoPath) : null;
			var isFirst = workQueue.isEmpty();
			var work = new Work(repo, task, actualWork, index, secondIndex);
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
				var task = "";
				if (!work.indices.isEmpty()) {
					for (var index : work.indices) {
						task += switch (index) {
							case PUBLIC -> "[Public search] ";
							case PRIVATE -> "[Private search] ";
							case PRIVATE_USAGE -> "[Private usage] ";
							case PUBLIC_USAGE -> "[Public usage] ";
						};
					}
				}
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
			return workQueue.stream().filter(work -> work.indices.contains(index)).count() > 0;
		}
	}

	public void clearIndexAsync() {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		offer("Clearing indices", () -> {
			searchService.on(SearchIndex.PUBLIC, SearchIndex.PRIVATE).clear();
			usageService.on(SearchIndex.PUBLIC_USAGE, SearchIndex.PRIVATE_USAGE).clear();
		});
	}

	public void indexPrivateAsync(RepositoryPath path, Commit previousCommit, Commit commit) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		if (commit == null)
			return;
		offer(SearchIndex.PRIVATE, path, "Indexing", repo -> {
			List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
			searchService.on(SearchIndex.PRIVATE).index(repo.path(), repo, tags, previousCommit, commit);
		});
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			offer(SearchIndex.PRIVATE_USAGE, path, "Indexing", repo -> {
				usageService.on(SearchIndex.PRIVATE_USAGE).index(repo.path(), repo, previousCommit, commit);
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
			searchService.on(SearchIndex.PUBLIC).index(repo.path(), repo, release.getTags(), previousCommit, commit);
		});
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			offer(SearchIndex.PUBLIC_USAGE, path, "Indexing", repo -> {
				usageService.on(SearchIndex.PUBLIC_USAGE).index(repo.path(), repo, previousCommit, commit);
			});
		}
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
					searchService.on(SearchIndex.PUBLIC).move(path.toString(), repo.path(), repo, latestRelease);
				});
			}
			offer(SearchIndex.PRIVATE, newPath, "Moving " + path.toString() + " to",
					repo -> searchService.on(SearchIndex.PRIVATE).move(path.toString(), repo.path(), repo, newHead));
			if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
				if (latestRelease != null) {
					offer(SearchIndex.PUBLIC_USAGE, newPath, "Moving index of " + path.toString() + " to", repo -> {
						usageService.on(SearchIndex.PUBLIC_USAGE).move(path.toString(), repo.path());
					});
				}
				offer(SearchIndex.PRIVATE_USAGE, newPath, "Moving index of " + path.toString() + " to", repo -> {
					usageService.on(SearchIndex.PRIVATE_USAGE).move(path.toString(), repo.path());
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
				searchService.on(SearchIndex.PRIVATE).updateTags(repo.path(), repo, head, tags);
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
				searchService.on(SearchIndex.PUBLIC).updateTags(repo.path(), repo, latestRelease,
						release.getTags());
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
			if (latestRelease == null) {
				offer(SearchIndex.PRIVATE, path, "Reindexing", repo -> {
					List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
					var indexer = searchService.on(SearchIndex.PRIVATE);
					indexer.remove(repo.path(), repo, head);
					indexer.index(repo.path(), repo, tags, null, head);
				});
				if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
					offer(SearchIndex.PRIVATE_USAGE, path, "Reindexing", repo -> {
						var indexer = usageService.on(SearchIndex.PRIVATE_USAGE);
						indexer.remove(repo.path());
						indexer.index(repo.path(), repo, null, head);
					});
				}
			} else {
				var release = releaseService.get(r.path(), latestRelease.id);
				if (head.equals(latestRelease)) {
					offer(SearchIndex.PUBLIC, SearchIndex.PRIVATE, path, "Reindexing", repo -> {
						var indexer = searchService.on(SearchIndex.PUBLIC, SearchIndex.PRIVATE);
						indexer.remove(repo.path(), repo, head);
						indexer.index(repo.path(), repo, release.getTags(), null, head);
					});
					if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
						offer(SearchIndex.PUBLIC_USAGE, SearchIndex.PRIVATE_USAGE, path, "Reindexing", repo -> {
							var indexer = usageService.on(SearchIndex.PUBLIC_USAGE, SearchIndex.PRIVATE_USAGE);
							indexer.remove(repo.path());
							indexer.index(repo.path(), repo, null, head);
						});
					}
				} else {
					offer(SearchIndex.PUBLIC, path, "Reindexing", repo -> {
						var indexer = searchService.on(SearchIndex.PUBLIC);
						indexer.remove(repo.path(), repo, latestRelease);
						indexer.index(repo.path(), repo, release.getTags(), null, latestRelease);
					});
					offer(SearchIndex.PRIVATE, path, "Reindexing", repo -> {
						var indexer = searchService.on(SearchIndex.PRIVATE);
						indexer.remove(repo.path(), repo, latestRelease);
						indexer.index(repo.path(), repo, release.getTags(), null, head);
					});
					if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
						offer(SearchIndex.PUBLIC_USAGE, path, "Reindexing", repo -> {
							var indexer = usageService.on(SearchIndex.PUBLIC_USAGE);
							indexer.remove(repo.path());
							indexer.index(repo.path(), repo, null, latestRelease);
						});
						offer(SearchIndex.PRIVATE_USAGE, path, "Reindexing", repo -> {
							var indexer = usageService.on(SearchIndex.PRIVATE_USAGE);
							indexer.remove(repo.path());
							indexer.index(repo.path(), repo, null, head);
						});
					}
				}
			}
		}
	}

	public void reindexAllAsync(List<RepositoryPath> paths) {
		if (!settings.searchConfig.isSearchAvailable())
			return;
		searchService.on(SearchIndex.PUBLIC, SearchIndex.PRIVATE).clear();
		usageService.on(SearchIndex.PUBLIC_USAGE, SearchIndex.PRIVATE_USAGE).clear();
		for (var path : paths) {
			try (var r = repoService.get(path)) {
				var head = r.commits.head();
				if (head == null)
					continue;
				var latestRelease = historyService.getLatestReleasedCommit(r);
				if (latestRelease == null) {
					offer(SearchIndex.PRIVATE, path, "Reindexing", repo -> {
						List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
						var indexer = searchService.on(SearchIndex.PRIVATE);
						indexer.remove(path.toString(), repo, head);
						indexer.index(repo.path(), repo, tags, null, head);
					});
				} else {
					var release = releaseService.get(r.path(), latestRelease.id);
					if (head.equals(latestRelease)) {
						offer(SearchIndex.PUBLIC, SearchIndex.PRIVATE, path, "Reindexing", repo -> {
							var indexer = searchService.on(SearchIndex.PUBLIC, SearchIndex.PRIVATE);
							indexer.remove(path.toString(), repo, head);
							indexer.index(repo.path(), repo, release.getTags(), null, head);
						});
					} else {
						offer(SearchIndex.PUBLIC, path, "Reindexing", repo -> {
							var indexer = searchService.on(SearchIndex.PUBLIC);
							indexer.remove(path.toString(), repo, latestRelease);
							indexer.index(repo.path(), repo, release.getTags(), null, latestRelease);
						});
						offer(SearchIndex.PRIVATE, path, "Reindexing", repo -> {
							var indexer = searchService.on(SearchIndex.PRIVATE);
							indexer.remove(path.toString(), repo, latestRelease);
							indexer.index(repo.path(), repo, release.getTags(), null, head);
						});
					}
				}
			}
		}
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			for (var path : paths) {
				try (var r = repoService.get(path)) {
					var head = r.commits.head();
					if (head == null)
						continue;
					var latestRelease = historyService.getLatestReleasedCommit(r);
					if (latestRelease == null) {
						offer(SearchIndex.PRIVATE_USAGE, path, "Reindexing", repo -> {
							var indexer = usageService.on(SearchIndex.PRIVATE_USAGE);
							indexer.remove(path.toString());
							indexer.index(repo.path(), repo, null, head);
						});
					} else {
						if (head.equals(latestRelease)) {
							offer(SearchIndex.PUBLIC_USAGE, SearchIndex.PRIVATE_USAGE, path, "Reindexing", repo -> {
								var indexer = usageService.on(SearchIndex.PUBLIC_USAGE, SearchIndex.PRIVATE_USAGE);
								indexer.remove(path.toString());
								indexer.index(repo.path(), repo, null, head);
							});
						} else {
							offer(SearchIndex.PUBLIC_USAGE, path, "Reindexing", repo -> {
								var indexer = usageService.on(SearchIndex.PUBLIC_USAGE);
								indexer.remove(path.toString());
								indexer.index(repo.path(), repo, null, latestRelease);
							});
							offer(SearchIndex.PRIVATE_USAGE, path, "Reindexing", repo -> {
								var indexer = usageService.on(SearchIndex.PRIVATE_USAGE);
								indexer.remove(path.toString());
								indexer.index(repo.path(), repo, null, head);
							});
						}
					}
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
				searchService.on(SearchIndex.PUBLIC).remove(repo.path(), repo, latestRelease);
			}
			if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
				usageService.on(SearchIndex.PUBLIC_USAGE).remove(repo.path());
			}
		}
		searchService.on(SearchIndex.PRIVATE).remove(repo.path(), repo, head);
		if (settings.is(ServerSetting.USAGE_SEARCH_ENABLED)) {
			usageService.on(SearchIndex.PRIVATE_USAGE).remove(repo.path());
		}
	}

	private class Work implements Runnable {

		private final List<SearchIndex> indices;
		private final Repository repo;
		private final String task;
		private final Consumer<Repository> work;

		private Work(Repository repo, String task, Consumer<Repository> work, SearchIndex... indices) {
			this.indices = Arrays.asList(indices).stream().filter(Objects::nonNull).toList();
			this.repo = repo;
			this.task = task;
			this.work = work;
		}

		@Override
		public void run() {
			work.accept(repo);
		}

	}

}