package com.greendelta.cloud.service.repository;

import java.io.File;

import org.openlca.core.model.ModelType;

import com.greendelta.cloud.error.RepositoryNotFoundException;
import com.greendelta.cloud.util.Strings;

class RepositoryPaths {

	private final File repository;

	RepositoryPaths(String path) {
		repository = new File(path);
		if (!repository.exists()) {
			String[] split = path.split("/");
			String id = Strings.concat(split[split.length - 2], "/", split[split.length - 1]);
			throw new RepositoryNotFoundException(id);
		}
	}

	static void create(File repository) {
		repository.mkdirs();
		for (ModelType type : ModelType.values())
			internalGetModelDirectory(repository, type).mkdir();
		new File(repository, "dataset_index").mkdir();
		new File(repository, "commit_index").mkdir();
	}

	File getDatasetFile(ModelType type, String refId, String commitId) {
		File datasetDirectory = getDatasetDirectory(type, refId);
		if (datasetDirectory == null)
			return null;
		return new File(datasetDirectory, Strings.concat(commitId, ".json"));
	}

	File getCommitHistoryFile() {
		return new File(repository, "history.json");
	}

	File getCommitIndexDirectory() {
		return new File(repository, "commit_index");
	}

	File getDatasetIndexDirectory() {
		return new File(repository, "dataset_index");
	}

	File getDatasetDirectory(ModelType type, String refId) {
		File modelDirectory = internalGetModelDirectory(repository, type);
		return new File(modelDirectory, refId);
	}

	File getModelDirectory(ModelType type) {
		return new File(repository, type.name().toLowerCase());
	}

	private static File internalGetModelDirectory(File repository, ModelType type) {
		return new File(repository, type.name().toLowerCase());
	}

}
