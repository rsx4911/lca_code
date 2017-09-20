package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.DatasetIndexEntry;
import com.greendelta.collaboration.util.ModelTypes;

public class BrowseService {

	private final SearchService searchService;

	@Inject
	public BrowseService(SearchService searchService) {
		this.searchService = searchService;
	}

	public List<ModelType> getRootContent(Repository repo) {
		List<ModelType> types = new ArrayList<>();
		for (ModelType type : ModelTypes.SORTED) {
			File dir = repo.getModelDir(type, false);
			if (dir.exists())
				types.add(type);
		}
		return types;
	}

	public List<DatasetIndexEntry> getCategoryContent(Repository repo, ModelType type, String filter) {
		return searchService.getUncategorized(repo, type, filter);
	}

	public List<DatasetIndexEntry> getCategoryContent(Repository repo, String categoryId, String filter) {
		return searchService.getForCategory(repo, categoryId, filter);
	}

	public DatasetIndexEntry getDataset(Repository repo, ModelType type, String refId, String commitId) {
		return searchService.get(repo, refId, commitId);
	}

	public boolean categoryExists(Repository repo, String categoryId) {
		return searchService.contains(repo, categoryId);
	}

}
