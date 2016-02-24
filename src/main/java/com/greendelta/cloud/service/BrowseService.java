package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.index.DatasetIndexEntry;

public class BrowseService {

	private RepositoryService repoService;

	@Inject
	public BrowseService(RepositoryService repoService) {
		this.repoService = repoService;
	}

	public List<ModelType> getRootContent(Repository repo) {
		List<ModelType> types = new ArrayList<>();
		ModelType[] all = { ModelType.PROJECT, ModelType.PRODUCT_SYSTEM,
				ModelType.IMPACT_METHOD, ModelType.PARAMETER,
				ModelType.PROCESS, ModelType.FLOW, ModelType.SOCIAL_INDICATOR,
				ModelType.FLOW_PROPERTY, ModelType.UNIT_GROUP,
				ModelType.CURRENCY, ModelType.SOURCE, ModelType.ACTOR,
				ModelType.LOCATION };
		for (ModelType type : all)
			if (repo.getModelDir(type, false).exists())
				types.add(type);
		return types;
	}

	public List<DatasetIndexEntry> getCategoryContent(Repository repo, ModelType type) {
		DatasetIndex index = repoService.getIndex(repo);
		return index.getForModelType(type);
	}

	public List<DatasetIndexEntry> getCategoryContent(Repository repo, String categoryId) {
		DatasetIndex index = repoService.getIndex(repo);
		return index.getForCategory(categoryId);
	}

	public boolean categoryExists(Repository repo, String categoryId) {
		DatasetIndex index = repoService.getIndex(repo);
		return index.categoryExists(categoryId);
	}

}
