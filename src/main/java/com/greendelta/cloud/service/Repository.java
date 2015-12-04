package com.greendelta.cloud.service;

import static org.openlca.cloud.util.Strings.concat;

import java.io.File;
import java.io.IOException;

import org.openlca.cloud.error.InvalidRepositoryNameException;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.openlca.jsonld.output.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Repository {

	private final static Logger log = LoggerFactory.getLogger(Repository.class);
	private final File repo;

	Repository(String path) {
		repo = new File(path);
		if (repo.exists()) {
			checkVersion(path);
			return;
		}
		String[] split = path.split("/");
		String owner = split[split.length - 2];
		String name = split[split.length - 1];
		String id = concat(owner, "/", name);
		throw new RepositoryNotFoundException(id);
	}

	private void checkVersion(String path) {
		try {
			byte[] data = Files.toByteArray(new File(path, "context.json"));
			String json = new String(data, "utf-8");
			JsonElement context = new Gson().fromJson(json, JsonElement.class);
			String version = Schema.parseUri(context);
			if (!Schema.isSupportedSchema(version))
				throw new UnsupportedSchemaException(version);
		} catch (Exception e) {
			log.error("Could not read context.json", e);
		}
	}

	static void create(String path) {
		new File(path).mkdirs();
		putJsonContext(path);
	}

	private static void putJsonContext(String path) {
		JsonObject context = Context.write();
		try {
			File file = new File(path, "context.json");
			file.createNewFile();
			String json = new Gson().toJson(context);
			byte[] data = json.getBytes("utf-8");
			Files.write(data, file);
		} catch (Exception e) {
			log.error("Could not create context.json", e);
		}
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

	private File getDatasetDir(ModelType type, String refId, boolean create) {
		File modelDir = getModelDir(type, create);
		return getDir(modelDir, refId, create);
	}

	private File getModelDir(ModelType type, boolean create) {
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
