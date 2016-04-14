package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.index.DatasetIndexEntry;
import com.greendelta.cloud.index.GlobalIndex;

public class SearchService {

	private final RepositoryService repoService;

	@Inject
	public SearchService(RepositoryService repoService) {
		this.repoService = repoService;
	}

	public PagedResult<DatasetIndexEntry> search(int page, String filter) {
		List<Repository> repos = repoService.getAllAccessible();
		List<DatasetIndex> indices = new ArrayList<>();
		for (Repository repo : repos)
			indices.add(repoService.getIndex(repo));
		return GlobalIndex.search(indices, page, filter);
	}
}
