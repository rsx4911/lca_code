package com.greendelta.cloud.service;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.greendelta.cloud.index.DatasetIndex;

@Singleton
class RepositoryIndices {

	private final Map<String, DatasetIndex> indices = new HashMap<>();

	public DatasetIndex get(Repository repo) {
		DatasetIndex index = indices.get(repo.toId());
		if (index == null) {
			index = new DatasetIndex(repo, repo.getIndexDir());
			indices.put(repo.toId(), index);
		}
		return index;
	}

}
