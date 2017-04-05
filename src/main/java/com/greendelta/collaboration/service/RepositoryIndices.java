package com.greendelta.collaboration.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.greendelta.collaboration.index.DatasetIndex;

@Singleton
public class RepositoryIndices {

	private final HistoryService historyService;

	@Inject
	public RepositoryIndices(HistoryService historyService) {
		this.historyService = historyService;
	}

	public DatasetIndex get(Repository repo) {
		return new DatasetIndex(repo, repo.getIndexDir(), historyService);
	}

}
