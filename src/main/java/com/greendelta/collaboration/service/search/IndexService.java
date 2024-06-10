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
import com.greendelta.collaboration.service.RepositoryList;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class IndexService {

	private final RepositoryService repoService;
	private final SearchService searchService;
	private final InputOutputDataService ioDataService;
	private final HistoryService historyService;
	private final ReleaseService releaseService;
	private final SettingsService settings;
	private final Queue<Work> workQueue = new LinkedList<>();

	public IndexService(RepositoryService repoService, SearchService searchService,
			InputOutputDataService ioDataService, HistoryService historyService, ReleaseService releaseService,
			SettingsService settings) {
		this.repoService = repoService;
		this.searchService = searchService;
		this.ioDataService = ioDataService;
		this.historyService = historyService;
		this.releaseService = releaseService;
		this.settings = settings;
	}

	private Work offer(String title, int total, Consumer<Work> actualWork) {
		if (!settings.searchConfig.isSearchAvailable())
			return null;
		synchronized (workQueue) {
			boolean isFirst = workQueue.isEmpty();
			var work = new Work(title, total, actualWork);
			workQueue.offer(work);
			if (isFirst) {
				runNext();
			}
			return work;
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
				synchronized (workQueue) {
					workQueue.poll();
					runNext();
				}
			}
		}).start();
	}

	public IndexingStatus getIndexingStatus() {
		synchronized (workQueue) {
			if (workQueue.isEmpty())
				return null;
			var titles = workQueue.stream().map(work -> work.title).toList();
			var current = workQueue.peek();
			return new IndexingStatus(titles, current.total, current.worked);
		}
	}

	public Work clearIndexAsync() {
		return offer("Clearing index", 1, work -> {
			searchService.on(SearchIndex.PUBLIC).clear();
			searchService.on(SearchIndex.PRIVATE).clear();
			if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
				ioDataService.clearIndex();
			}
			work.worked++;
		});
	}

	public Work indexPrivateAsync(RepositoryPath path, Commit previousCommit, Commit commit) {
		var repo = repoService.get(path);
		return offer("Indexing " + repo.path(), 1, work -> {
			try {
				List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
				searchService.on(SearchIndex.PRIVATE).index(repo, tags, previousCommit, commit);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.index(repo, previousCommit, commit);
				}
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}

	public Work indexPublicAsync(RepositoryPath path, Commit previousCommit, Commit commit) {
		var repo = repoService.get(path);
		return offer("Indexing " + repo.path(), 1, work -> {
			try {
				var release = releaseService.get(repo.path(), commit.id);
				searchService.on(SearchIndex.PUBLIC).index(repo, release.getTags(), previousCommit, commit);
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}

	public Work moveIndexAsync(RepositoryPath path, RepositoryPath newPath) {
		var newRepo = repoService.get(newPath);
		return offer("Moving index of " + path.toString() + " to " + newRepo.path(), 1, work -> {
			try {
				searchService.on(SearchIndex.PUBLIC).move(path, newRepo, newRepo.commits.head());
				var latestRelease = historyService.getLatestReleasedCommit(newRepo);
				searchService.on(SearchIndex.PRIVATE).move(path, newRepo, latestRelease);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.move(path, newRepo);
				}
			} finally {
				newRepo.close();
				work.worked++;
			}
		});
	}

	public Work updatePrivateTagsAsync(RepositoryPath path) {
		var repo = repoService.get(path);
		return offer("Reindexing " + repo.path(), 1, work -> {
			try {
				List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
				searchService.on(SearchIndex.PRIVATE).updateTags(repo, repo.commits.head(), tags);
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}
	
	public Work updatePublicTagsAsync(RepositoryPath path) {
		var repo = repoService.get(path);
		return offer("Reindexing " + repo.path(), 1, work -> {
			try {
				var latestRelease = historyService.getLatestReleasedCommit(repo);
				if (latestRelease != null) {
					var release = releaseService.get(repo.path(), latestRelease.id);
					searchService.on(SearchIndex.PUBLIC).updateTags(repo, latestRelease, release.getTags());
				}
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}

	public Work reindexAsync(RepositoryPath path) {
		var repo = repoService.get(path);
		return offer("Reindexing " + repo.path(), 1, work -> {
			try {
				var latestRelease = historyService.getLatestReleasedCommit(repo);
				var head = repo.commits.head();
				searchService.on(SearchIndex.PUBLIC).remove(repo, latestRelease);
				searchService.on(SearchIndex.PRIVATE).remove(repo, head);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.remove(repo);
				}
				if (latestRelease != null) {
					var release = releaseService.get(repo.path(), latestRelease.id);
					searchService.on(SearchIndex.PUBLIC).index(repo, release.getTags(), null, latestRelease);
				}
				List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
				searchService.on(SearchIndex.PRIVATE).index(repo, tags, null, head);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.index(repo, null, head);
				}
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}

	public Work reindexAllAsync(List<RepositoryPath> paths) {
		var repos = new RepositoryList();
		paths.forEach(path -> repos.add(repoService.get(path)));
		return offer("Reindexing all repositories", repos.size(), work -> {
			try {
				searchService.on(SearchIndex.PUBLIC).clear();
				searchService.on(SearchIndex.PRIVATE).clear();
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.clearIndex();
				}
				repos.forEach(repo -> {
					var latestRelease = historyService.getLatestReleasedCommit(repo);
					if (latestRelease != null) {
						var release = releaseService.get(repo.path(), latestRelease.id);
						searchService.on(SearchIndex.PUBLIC).index(repo, release.getTags(), null, latestRelease);
					}
					var head = repo.commits.head();
					List<String> tags = repo.settings != null ? repo.settings.get(RepositorySetting.TAGS) : null;
					searchService.on(SearchIndex.PRIVATE).index(repo, tags, null, head);
					if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
						ioDataService.index(repo, null, head);
					}
					work.worked++;
				});
			} finally {
				repos.close();
			}
		});
	}

	public Work deleteIndexAsync(String repoId) {
		var repo = repoService.get(repoId);
		return offer("Deleting index of " + repo.path(), 1, work -> {
			try {
				deleteIndex(repo);
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}

	public void deleteIndex(Repository repo) {
		var latestRelease = historyService.getLatestReleasedCommit(repo);
		var head = repo.commits.head();
		searchService.on(SearchIndex.PUBLIC).remove(repo, latestRelease);
		searchService.on(SearchIndex.PRIVATE).remove(repo, head);
		if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
			ioDataService.remove(repo);
		}
	}

	public class Work implements Runnable {

		public final String title;
		public final int total;
		public int worked;
		private final Consumer<Work> work;
		private boolean done;

		private Work(String title, int total, Consumer<Work> work) {
			this.title = title;
			this.total = total;
			this.work = work;
		}

		@Override
		public void run() {
			try {
				work.accept(this);
			} finally {
				done = true;
			}
		}

		public boolean isDone() {
			return done;
		}

	}

	public class IndexingStatus {

		public final List<String> titles;
		public final int total;
		public final int worked;

		private IndexingStatus(List<String> titles, int total, int worked) {
			this.titles = titles;
			this.total = total;
			this.worked = worked;
		}

	}

}