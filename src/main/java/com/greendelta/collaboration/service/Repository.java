package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Repository {

	private final static Logger log = LogManager.getLogger(Repository.class);
	final File repoDir;
	public final String group;
	public final String name;
	public final Settings settings;

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
			Settings settings = null;
			File settingsFile = new File(repoDir, "settings.json");
			if (!settingsFile.exists())
				settings = new Settings();
			else {
				try {
					settings = new Gson().fromJson(new FileReader(settingsFile), Settings.class);
				} catch (IOException e) {
					log.error("Error loading settings for repository");
				}
			}
			this.settings = settings;
			return;
		}
		throw new RepositoryNotFoundException(toId());
	}

	public String toId() {
		return toId(group, name);
	}

	void setSetting(String setting, boolean value) {
		try {
			Field field = Settings.class.getField(setting);
			field.set(this.settings, value);
			try (FileWriter writer = new FileWriter(new File(repoDir, "settings.json"))) {
				new Gson().toJson(settings, writer);
			} catch (IOException e) {
				log.error("Error saving settings", e);
			}
		} catch (NoSuchFieldException e) {
			log.debug("Tried to set non existing setting value: " + setting);
		} catch (IllegalAccessException e) {
			log.error("Error setting value for " + setting, e);
		}
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

	File getDatasetFile(ModelType type, String refId, String commitId,
			boolean create) {
		File datasetDir = getDatasetDir(type, refId, create);
		String filename = commitId + ".json.gzip";
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

	File getFile(File dir, String name, boolean create) {
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

	public static class Settings {

		public boolean publicAccess;
		public boolean commentApproval;

	}

}
