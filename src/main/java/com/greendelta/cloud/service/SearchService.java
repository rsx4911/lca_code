package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.index.DatasetIndexEntry;
import com.greendelta.cloud.index.GlobalIndex;

public class SearchService {

	private final RepositoryService repoService;
	private final RepositoryIndices repositoryIndices;

	@Inject
	public SearchService(RepositoryService repoService, RepositoryIndices repositoryIndices) {
		this.repoService = repoService;
		this.repositoryIndices = repositoryIndices;
	}

	public PagedResult<DatasetIndexEntry> search(int page, String filter, ModelType type) {
		List<Repository> repos = repoService.getAllAccessible();
		List<DatasetIndex> indices = new ArrayList<>();
		for (Repository repo : repos)
			indices.add(repositoryIndices.get(repo));
		return GlobalIndex.search(indices, page, filter, type);
	}
}
