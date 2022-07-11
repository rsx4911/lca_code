package com.greendelta.collaboration.service.search;

import java.util.List;

import org.openlca.git.model.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.io.RepositoryJsonWriter;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.SearchService.ReindexingStatus;

@Service
public class IndexService {

	private final SearchService searchService;
	private final InputOutputDataService ioDataService;

	@Autowired
	public IndexService(SearchService searchService, InputOutputDataService ioDataService) {
		this.searchService = searchService;
		this.ioDataService = ioDataService;
	}

	@Async
	public void index(Repository repo) {
		searchService.index(repo);
		ioDataService.update(repo);
	}

	@Async
	public void index(Repository repo, Commit commit) {
		searchService.index(repo, commit);
		ioDataService.update(repo);
	}

	@Async
	public void update(Repository repo, Repository newRepo) {
		searchService.update(repo, newRepo);
		ioDataService.update(repo, newRepo);
	}

	@Async
	public void update(Repository repo) {
		searchService.update(repo);
	}

	@Async
	public void remove(Repository repo) {
		searchService.remove(repo);
		ioDataService.delete(repo);
	}

	@Async
	public void generateJson(Repository repo) {
		RepositoryJsonWriter.writeCurrent(repo);
	}

	@Async
	public void index(List<Repository> repositories, ReindexingStatus status) {
		try {
			repositories.forEach(repo -> {
				index(repo);
				status.worked++;
			});
		} finally {
			searchService.endReindexing();
		}
	}

}