package com.greendelta.collaboration.service.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import org.openlca.git.model.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryList;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class IndexService {

	private final SearchService searchService;
	private final InputOutputDataService ioDataService;
	private final SettingsService settings;
	private Queue<Work> workQueue = new LinkedList<>();

	@Autowired
	public IndexService(SearchService searchService, InputOutputDataService ioDataService, SettingsService settings) {
		this.searchService = searchService;
		this.ioDataService = ioDataService;
		this.settings = settings;
	}

	private Work offer(String title, int total, Consumer<Work> actualWork) {
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

	public Work clearIndex(RepositoryList repos) {
		return offer("Clearing index", 1, work -> {
			searchService.clearIndex(repos);
			ioDataService.clearIndex();
			repos.forEach(repo -> setCommitId(repo, null));
			work.worked++;
		});
	}

	public Work index(Repository repo) {
		return offer("Indexing " + repo.path(), 1, work -> {
			searchService.index(repo);
			ioDataService.index(repo);
			setCommitId(repo, repo.commits().head());
			work.worked++;
		});
	}

	public Work moveIndex(Repository repo, Repository newRepo) {
		return offer("Moving index of " + repo.path() + " to " + newRepo.path(), 1, work -> {
			searchService.move(repo, newRepo);
			ioDataService.move(repo, newRepo);
			setCommitId(repo, null);
			setCommitId(newRepo, newRepo.commits().head());
			work.worked++;
		});
	}

	public Work updateTags(Repository repo) {
		return offer("Reindexing " + repo.path(), 1, work -> {
			searchService.updateTags(repo);
			work.worked++;
		});
	}

	public Work reindex(Repository repo) {
		return offer("Reindexing " + repo.path(), 1, work -> {
			searchService.remove(repo);
			ioDataService.remove(repo);
			setCommitId(repo, null);
			searchService.index(repo);
			ioDataService.index(repo);
			setCommitId(repo, repo.commits().head());
			work.worked++;
		});
	}

	public Work reindexAll(RepositoryList repos) {
		return offer("Reindexing all repositories", repos.size(), work -> {
			searchService.clearIndex(repos);
			ioDataService.clearIndex();
			repos.forEach(repo -> {
				setCommitId(repo, null);
				searchService.index(repo);
				ioDataService.index(repo);
				setCommitId(repo, repo.commits().head());
				work.worked++;
			});
		});
	}

	public Work deleteIndex(Repository repo) {
		return offer("Deleting index of " + repo.path(), 1, work -> {
			searchService.remove(repo);
			ioDataService.remove(repo);
			setCommitId(repo, null);
			work.worked++;
		});
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