package com.greendelta.cloud.service;

import java.io.File;

import org.openlca.cloud.error.InvalidRepositoryNameException;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.cloud.util.Strings;
import org.openlca.core.model.ModelType;

class Repository {

	private final File repository;

	Repository(String path) {
		repository = new File(path);
		if (!repository.exists()) {
			String[] split = path.split("/");
			String id = Strings.concat(split[split.length - 2], "/",
					split[split.length - 1]);
			throw new RepositoryNotFoundException(id);
		}
	}

	static void create(File repository) {
		repository.mkdirs();
		for (ModelType type : ModelType.values())
			internalGetModelDirectory(repository, type).mkdir();
		File historyDirectory = internalGetCommitDirectory(repository);
		historyDirectory.mkdir();
	}

	static void checkIdForValidity(String id) {
		if (!id.contains("/") || id.indexOf('/') != id.lastIndexOf('/'))
			throw new InvalidRepositoryNameException(id);
	}

	static void checkNameForValidity(String name) {
		if (name.contains("/"))
			throw new InvalidRepositoryNameException(name);
	}

	File getCommitHistoryFile() {
		File historyDirectory = internalGetCommitDirectory(repository);
		return new File(historyDirectory, "history.txt");
	}

	File getCommitFile(String commitId) {
		File historyDirectory = internalGetCommitDirectory(repository);
		return new File(historyDirectory, commitId + ".txt");
	}

	File getDatasetFile(ModelType type, String refId, String commitId) {
		File datasetDirectory = getDatasetDirectory(type, refId);
		if (datasetDirectory == null)
			return null;
		return new File(datasetDirectory, Strings.concat(commitId, ".json"));
	}

	File getDatasetDirectory(ModelType type, String refId) {
		File modelDirectory = internalGetModelDirectory(repository, type);
		return new File(modelDirectory, refId);
	}

	File getModelDirectory(ModelType type) {
		return internalGetModelDirectory(repository, type);
	}

	private static File internalGetModelDirectory(File repository,
			ModelType type) {
		return new File(repository, type.name().toLowerCase());
	}

	private static File internalGetCommitDirectory(File repository) {
		return new File(repository, "history");
	}

}
