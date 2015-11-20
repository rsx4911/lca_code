package com.greendelta.cloud.service;

import java.io.File;
import java.io.IOException;

import org.openlca.cloud.error.InvalidRepositoryNameException;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.cloud.util.Strings;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Repository {

	private final Logger log = LoggerFactory.getLogger(getClass());
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

	static void create(String path) {
		new File(path).mkdirs();
	}

	static void checkIdForValidity(String id) {
		if (!id.contains("/") || id.indexOf('/') != id.lastIndexOf('/'))
			throw new InvalidRepositoryNameException(id);
	}

	static void checkNameForValidity(String name) {
		if (name.contains("/"))
			throw new InvalidRepositoryNameException(name);
	}

	File getHistoryFile(boolean create) {
		File historyDir = getHistoryDir(create);
		return getFile(historyDir, "history.txt", create);
	}

	File getCommitFile(String commitId, boolean create) {
		File historyDir = getHistoryDir(create);
		String filename = Strings.concat(commitId, ".txt");
		return getFile(historyDir, filename, create);
	}

	File getDatasetFile(ModelType type, String refId, String commitId,
			boolean create) {
		File datasetDir = getDatasetDir(type, refId, create);
		String filename = Strings.concat(commitId, ".json");
		return getFile(datasetDir, filename, create);
	}

	File getBinDir(ModelType type, String refId, String commitId, boolean create) {
		File binDir = getBinDir(type, refId, create);
		return getDir(binDir, commitId, create);
	}

	private File getDatasetDir(ModelType type, String refId, boolean create) {
		File modelDir = getModelDir(type, create);
		return getDir(modelDir, refId, create);
	}

	private File getModelDir(ModelType type, boolean create) {
		return getDir(repository, type.name().toLowerCase(), create);
	}

	private File getBinDir(ModelType type, String refId, boolean create) {
		File binDir = getBinDir(create);
		return getDir(binDir, type.name().toLowerCase(), create);
	}

	private File getBinDir(boolean create) {
		return getDir(repository, "bin", create);
	}

	private File getHistoryDir(boolean create) {
		return getDir(repository, "history", true);
	}

	private File getFile(File dir, String name, boolean create) {
		File file = new File(dir, name);
		if (create && !file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				String message = Strings.concat("Error creating file ", file.getAbsolutePath());
				log.error(message, e);
			}
		return file;
	}

	private File getDir(File dir, String name, boolean create) {
		File file = new File(dir, name);
		if (create && !file.exists())
			file.mkdir();
		return file;
	}

}
