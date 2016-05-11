package com.greendelta.cloud.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.index.DatasetIndexEntry;

public class BrowseService {

	private final RepositoryIndices repositoryIndices;

	@Inject
	public BrowseService(RepositoryIndices repositoryIndices) {
		this.repositoryIndices = repositoryIndices;
	}

	public List<ModelType> getRootContent(Repository repo) {
		List<ModelType> types = new ArrayList<>();
		ModelType[] all = { ModelType.PROJECT, ModelType.PRODUCT_SYSTEM,
				ModelType.IMPACT_METHOD, ModelType.PARAMETER,
				ModelType.PROCESS, ModelType.FLOW, ModelType.SOCIAL_INDICATOR,
				ModelType.FLOW_PROPERTY, ModelType.UNIT_GROUP,
				ModelType.CURRENCY, ModelType.SOURCE, ModelType.ACTOR,
				ModelType.LOCATION };
		for (ModelType type : all) {
			File dir = repo.getModelDir(type, false);
			if (dir.exists())
				types.add(type);
		}
		return types;
	}

	public List<DatasetIndexEntry> getCategoryContent(Repository repo, ModelType type, String filter) {
		DatasetIndex index = repositoryIndices.get(repo);
		return index.getForModelType(type, filter);
	}

	public List<DatasetIndexEntry> getCategoryContent(Repository repo, String categoryId, String filter) {
		DatasetIndex index = repositoryIndices.get(repo);
		return index.getForCategory(categoryId, filter);
	}

	public DatasetIndexEntry getDataset(Repository repo, ModelType type, String refId, String commitId) {
		DatasetIndex index = repositoryIndices.get(repo);
		return index.getForId(refId, commitId);
	}

	public boolean categoryExists(Repository repo, String categoryId) {
		DatasetIndex index = repositoryIndices.get(repo);
		return index.categoryExists(categoryId);
	}

}
