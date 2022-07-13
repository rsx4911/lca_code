package com.greendelta.collaboration.service.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import org.openlca.git.model.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.service.Repository;

@Service
public class IndexService {

	private final SearchService searchService;
	private final InputOutputDataService ioDataService;
	private Queue<Work> workQueue = new LinkedList<>();

	@Autowired
	public IndexService(SearchService searchService, InputOutputDataService ioDataService) {
		this.searchService = searchService;
		this.ioDataService = ioDataService;
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

	public Work clearIndex() {
		return offer("Clearing index", 1, work -> {
			searchService.clearIndex();
			ioDataService.clearIndex();
			work.worked++;
		});
	}

	public Work index(Repository repo) {
		return offer("Indexing " + repo.path(), 1, work -> {
			searchService.index(repo);
			ioDataService.index(repo);
			work.worked++;
		});
	}

	public Work index(Repository repo, Commit commit) {
		return offer("Indexing " + repo.path(), 1, work -> {
			searchService.index(repo, commit);
			ioDataService.index(repo);
			work.worked++;
		});
	}

	public Work moveIndex(Repository repo, Repository newRepo) {
		return offer("Moving index of " + repo.path() + " to " + newRepo.path(), 1, work -> {
			searchService.update(repo, newRepo);
			ioDataService.update(repo, newRepo);
			work.worked++;
		});
	}

	public Work updateIndex(Repository repo) {
		return offer("Updating index of " + repo.path(), 1, work -> {
			searchService.update(repo);
			work.worked++;
		});
	}

	public Work reindex(Repository repo) {
		return offer("Reindexing " + repo.path(), 1, work -> {
			searchService.remove(repo);
			ioDataService.remove(repo);
			searchService.index(repo);
			ioDataService.index(repo);
			work.worked++;
		});
	}

	public Work reindexAll(List<Repository> repositories) {
		return offer("Reindexing all repositories", repositories.size(), work -> {
			searchService.clearIndex();
			ioDataService.clearIndex();
			repositories.forEach(repo -> {
				searchService.index(repo);
				ioDataService.index(repo);
				work.worked++;
			});
		});
	}

	public Work deleteIndex(Repository repo) {
		return offer("Deleting index of " + repo.path(), 1, work -> {
			searchService.remove(repo);
			ioDataService.remove(repo);
			work.worked++;
		});
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