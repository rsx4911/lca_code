package com.greendelta.collaboration.service;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.greendelta.collaboration.index.DatasetIndex;

@Singleton
public class RepositoryIndices {

	private final HistoryService historyService;
	private final Map<String, DatasetIndex> indices = new HashMap<>();

	@Inject
	public RepositoryIndices(HistoryService historyService) {
		this.historyService = historyService;
	}

	public DatasetIndex get(Repository repo) {
		DatasetIndex index = indices.get(repo.toId());
		if (index == null) {
			index = new DatasetIndex(repo.toId(), repo.getIndexDir());
			indices.put(repo.toId(), index);
		}
		index.setHistoryService(historyService);
		return index;
	}

}
