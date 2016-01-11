package com.greendelta.cloud.service;

import static org.openlca.cloud.util.Strings.concat;

import java.io.File;
import java.io.IOException;

import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Repository {

	private final static Logger log = LoggerFactory.getLogger(Repository.class);
	private final File repo;
	public final String group;
	public final String name;

	Repository(String root, String group, String name) {
		this.group = group;
		this.name = name;
		String path = concat(root, "/", toId());
		repo = new File(path);
		if (repo.exists()) {
			checkVersion();
			return;
		}
		throw new RepositoryNotFoundException(toId());
	}

	public String toId() {
		return toId(group, name);
	}

	public static String toId(String group, String name) {
		return concat(group, "/", name);
	}

	private void checkVersion() {
		try {
			File file = new File(repo, "context.json");
			if (!file.exists())
				throw new UnsupportedSchemaException("null");
			byte[] data = Files.toByteArray(file);
			String json = new String(data, "utf-8");
			JsonElement context = new Gson().fromJson(json, JsonElement.class);
			String version = Schema.parseUri(context);
			if (!Schema.isSupportedSchema(version))
				throw new UnsupportedSchemaException(version);
		} catch (Exception e) {
			log.error("Could not read context.json", e);
			throw new UnsupportedSchemaException("null");
		}
	}

	File getHistoryFile(boolean create) {
		File historyDir = getHistoryDir(create);
		return getFile(historyDir, "history.txt", create);
	}

	File getCommitFile(String commitId, boolean create) {
		File historyDir = getHistoryDir(create);
		String filename = concat(commitId, ".txt");
		return getFile(historyDir, filename, create);
	}

	File getDatasetFile(ModelType type, String refId, String commitId,
			boolean create) {
		File datasetDir = getDatasetDir(type, refId, create);
		String filename = concat(commitId, ".json");
		return getFile(datasetDir, filename, create);
	}

	File getBinDir(ModelType type, String refId, String commitId, boolean create) {
		File binDir = getBinDir(type, refId, create);
		return getDir(binDir, commitId, create);
	}

	File getIndexDir() {
		return new File(repo, "ds_index");
	}

	File getAvatarFile() {
		return new File(repo, "avatar");
	}

	private File getDatasetDir(ModelType type, String refId, boolean create) {
		File modelDir = getModelDir(type, create);
		return getDir(modelDir, refId, create);
	}

	File getModelDir(ModelType type, boolean create) {
		return getDir(repo, type.name().toLowerCase(), create);
	}

	private File getBinDir(ModelType type, String refId, boolean create) {
		File binDir = getBinDir(create);
		return getDir(binDir, type.name().toLowerCase(), create);
	}

	private File getBinDir(boolean create) {
		return getDir(repo, "bin", create);
	}

	private File getHistoryDir(boolean create) {
		return getDir(repo, "history", true);
	}

	private File getFile(File dir, String name, boolean create) {
		File file = new File(dir, name);
		if (create && !file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				String path = file.getAbsolutePath();
				String message = concat("Error creating file ", path);
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
