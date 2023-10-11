package com.greendelta.collaboration.service.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.openlca.git.model.Commit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryList;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class IndexService {

	private final Executor threads;
	private final RepositoryService repoService;
	private final SearchService searchService;
	private final InputOutputDataService ioDataService;
	private final SettingsService settings;
	private Queue<Work> workQueue = new LinkedList<>();

	public IndexService(@Qualifier("taskExecutor") Executor threads, RepositoryService repoService,
			SearchService searchService,
			InputOutputDataService ioDataService, SettingsService settings) {
		this.threads = threads;
		this.repoService = repoService;
		this.searchService = searchService;
		this.ioDataService = ioDataService;
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
		threads.execute(() -> {
			try {
				work.run();
			} finally {
				synchronized (workQueue) {
					workQueue.poll();
					runNext();
				}
			}
		});
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
			searchService.clearIndex();
			if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
				ioDataService.clearIndex();
			}
			work.worked++;
		});
	}

	public Work indexAsync(RepositoryPath path) {
		var repo = repoService.get(path);
		return offer("Indexing " + repo.path(), 1, work -> {
			try {
				searchService.index(repo);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.index(repo);
				}
				setCommitId(repo, repo.commits().head());
			} finally {
				repo.close();
				work.worked++;
			}
		});
	}

	public Work moveIndexAsync(RepositoryPath path, RepositoryPath newPath) {
		var repo = repoService.get(path);
		var newRepo = repoService.get(newPath);
		return offer("Moving index of " + repo.path() + " to " + newRepo.path(), 1, work -> {
			try {
				searchService.move(repo, newRepo);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.move(repo, newRepo);
				}
				setCommitId(repo, null);
				setCommitId(newRepo, newRepo.commits().head());
			} finally {
				repo.close();
				newRepo.close();
				work.worked++;
			}
		});
	}

	public Work updateTagsAsync(RepositoryPath path) {
		var repo = repoService.get(path);
		return offer("Reindexing " + repo.path(), 1, work -> {
			try {
				searchService.updateTags(repo);
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
				searchService.remove(repo);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.remove(repo);
				}
				setCommitId(repo, null);
				searchService.index(repo);
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.index(repo);
				}
				setCommitId(repo, repo.commits().head());
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
				searchService.clearIndex();
				if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
					ioDataService.clearIndex();
				}
				repos.forEach(repo -> {
					setCommitId(repo, null);
					searchService.index(repo);
					if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
						ioDataService.index(repo);
					}
					setCommitId(repo, repo.commits().head());
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
		searchService.remove(repo);
		if (settings.is(ServerSetting.SEARCH_LINKS_ENABLED)) {
			ioDataService.remove(repo);
		}
		setCommitId(repo, null);
	}

	private void setCommitId(Repository repo, Commit commit) {
		var repoSettings = settings.get(SettingType.REPOSITORY_SETTING, repo.path(), null);
		if (commit == null) {
			repoSettings.delete(RepositorySetting.SEARCH_COMMIT_ID);
		} else {
			repoSettings.set(RepositorySetting.SEARCH_COMMIT_ID, commit.id);
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