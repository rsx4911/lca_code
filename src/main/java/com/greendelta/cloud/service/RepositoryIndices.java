package com.greendelta.cloud.service;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.greendelta.cloud.index.DatasetIndex;

@Singleton
public class RepositoryIndices {

	private final static Map<String, DatasetIndex> indices = new HashMap<>();
	private final Provider<HistoryService> historyServiceProvider;

	@Inject
	public RepositoryIndices(Provider<HistoryService> historyServiceProvider) {
		this.historyServiceProvider = historyServiceProvider;
	}

	public DatasetIndex get(Repository repo) {
		DatasetIndex index = indices.get(repo.toId());
		if (index == null) {
			index = new DatasetIndex(repo, repo.getIndexDir(), historyServiceProvider);
			indices.put(repo.toId(), index);
		}
		return index;
	}

}
