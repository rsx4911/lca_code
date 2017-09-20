package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;

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

public class Repository {

	private final static Logger log = LoggerFactory.getLogger(Repository.class);
	final File repoDir;
	public final String group;
	public final String name;
	public final boolean publicAccess;

	static Repository get(String root, String group, String name) {
		Repository repo = new Repository(root, group, name);
		repo.checkVersion();
		return repo;
	}

	static Repository getIgnoreSchema(String root, String group, String name) {
		return new Repository(root, group, name);
	}

	private Repository(String root, String group, String name) {
		this.group = group;
		this.name = name;
		String path = root + File.separator + group + File.separator + name;
		repoDir = new File(path);
		if (repoDir.exists()) {
			publicAccess = new File(repoDir, ".public").exists();
			return;
		}
		throw new RepositoryNotFoundException(toId());
	}

	public String toId() {
		return toId(group, name);
	}

	public static String toId(String group, String name) {
		return group + "/" + name;
	}

	public String getSchemaVersion() {
		try {
			File file = new File(repoDir, "context.json");
			if (!file.exists())
				return null;
			byte[] data = Files.toByteArray(file);
			String json = new String(data, "utf-8");
			JsonElement context = new Gson().fromJson(json, JsonElement.class);
			return Schema.parseUri(context);
		} catch (Exception e) {
			log.error("Could not read context.json", e);
			return null;
		}
	}

	public void setSchemaVersion(String version) {
		File file = new File(repoDir, "context.json");
		JsonElement context = Context.write(version);
		String json = new Gson().toJson(context);
		try {
			Files.write(json.getBytes(), file);
		} catch (IOException e) {
			log.error("Could not write context.json", e);
		}
	}

	private void checkVersion() {
		try {
			String version = getSchemaVersion();
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
		String filename = commitId + ".txt";
		return getFile(historyDir, filename, create);
	}

	File getDatasetFile(ModelType type, String refId, String commitId,
			boolean create) {
		File datasetDir = getDatasetDir(type, refId, create);
		String filename = commitId + ".json";
		return getFile(datasetDir, filename, create);
	}

	File getBinDir(ModelType type, String refId, String commitId, boolean create) {
		File binDir = getBinDir(type, refId, create);
		return getDir(binDir, commitId, create);
	}

	File getAvatarFile() {
		return new File(repoDir, "avatar");
	}

	private File getDatasetDir(ModelType type, String refId, boolean create) {
		File modelDir = getModelDir(type, create);
		File intermediateDir = getDir(modelDir, refId.substring(0, 2), create);
		return getDir(intermediateDir, refId, create);
	}

	File getModelDir(ModelType type, boolean create) {
		return getDir(repoDir, type.name().toLowerCase(), create);
	}

	private File getBinDir(ModelType type, String refId, boolean create) {
		File typeBinDir = getBinDir(type, create);
		File intermediateDir = getDir(typeBinDir, refId.substring(0, 2), create);
		return getDir(intermediateDir, refId, create);
	}

	File getBinDir(ModelType type, boolean create) {
		File binDir = getBinDir(create);
		return getDir(binDir, type.name().toLowerCase(), create);
	}

	private File getBinDir(boolean create) {
		return getDir(repoDir, "bin", create);
	}

	File getHistoryDir(boolean create) {
		return getDir(repoDir, "history", true);
	}

	private File getFile(File dir, String name, boolean create) {
		File file = new File(dir, name);
		if (create && !file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				String path = file.getAbsolutePath();
				String message = "Error creating file " + path;
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
