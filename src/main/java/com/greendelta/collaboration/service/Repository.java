package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.openlca.util.Dirs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.util.ModelTypes;

public class Repository {

	private final static Logger log = LogManager.getLogger(Repository.class);
	private final Gson gson = new Gson();
	public final String group;
	public final String name;
	public final Settings settings;
	final File repoDir;
	final Map<String, Role> libraryRestrictions;

	public static Repository get(String root, String group, String name) {
		Repository repo = new Repository(root, group, name);
		repo.checkVersion();
		return repo;
	}

	private Repository(String root, String group, String name) {
		this.group = group;
		this.name = name;
		String path = root + File.separator + group + File.separator + name;
		repoDir = new File(path);
		if (!repoDir.exists())
			throw new RepositoryNotFoundException(toId());
		this.settings = loadSettings();
		this.libraryRestrictions = loadLibraryRestrictions();
	}

	private Settings loadSettings() {
		File settingsFile = new File(repoDir, "settings.json");
		if (!settingsFile.exists())
			return new Settings();
		try (FileReader reader = new FileReader(settingsFile)) {
			return new Gson().fromJson(reader, Settings.class);
		} catch (IOException e) {
			log.error("Error loading settings for repository");
			return new Settings();
		}
	}

	private Map<String, Role> loadLibraryRestrictions() {
		File file = new File(repoDir, "library-restrictions.json");
		if (!file.exists())
			return new HashMap<>();
		try (FileReader reader = new FileReader(file)) {
			return new Gson().fromJson(reader, new TypeToken<Map<String, Role>>() {
			}.getType());
		} catch (IOException e) {
			log.error("Error loading library restrictions", e);
			return new HashMap<>();
		}
	}

	public String toId() {
		return toId(group, name);
	}

	void setSetting(String setting, String value) {
		switch (setting) {
		case "publicAccess":
			settings.publicAccess = Boolean.parseBoolean(value);
			break;
		case "prohibitCommits":
			settings.prohibitCommits = Boolean.parseBoolean(value);
			break;
		case "commentApproval":
			settings.commentApproval = Boolean.parseBoolean(value);
			break;
		case "maxSize":
			settings.maxSize = Long.parseLong(value);
			break;
		}
		try (FileWriter writer = new FileWriter(new File(repoDir, "settings.json"))) {
			new Gson().toJson(settings, writer);
		} catch (IOException e) {
			log.error("Error saving settings", e);
		}
	}

	void setRestriction(String library, Role role) {
		if (role == null) {
			libraryRestrictions.remove(library);
		} else {
			libraryRestrictions.put(library, role);
		}
		try (FileWriter writer = new FileWriter(new File(repoDir, "library-restrictions.json"))) {
			new Gson().toJson(libraryRestrictions, writer);
		} catch (IOException e) {
			log.error("Error saving library restrictions", e);
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
			byte[] data = com.google.common.io.Files.toByteArray(file);
			String json = new String(data, "utf-8");
			JsonElement context = new Gson().fromJson(json, JsonElement.class);
			return Schema.parseUri(context);
		} catch (Exception e) {
			log.error("Could not read context.json", e);
			return null;
		}
	}

	public long getSize() {
		try {
			File sizeInfo = new File(repoDir, ".size");
			if (!sizeInfo.exists()) {
				long size = determineSize();
				Files.write(sizeInfo.toPath(), Long.toString(size).getBytes(Charset.forName("utf-8")));
				return size;
			}
			return Long.parseLong(new String(Files.readAllBytes(sizeInfo.toPath()), Charset.forName("utf-8")));
		} catch (IOException e) {
			log.error("Error getting size of repository", e);
			return 0;
		}
	}

	public boolean has(ModelType type) {
		return getModelDir(type, false).exists();
	}

	void updateSize(long size) {
		try {
			File sizeInfo = new File(repoDir, ".size");
			Files.write(sizeInfo.toPath(), Long.toString(size).getBytes(Charset.forName("utf-8")));
		} catch (IOException e) {
			log.error("Error setting size of repository", e);
		}
	}

	private long determineSize() {
		long size = 0;
		for (ModelType type : ModelTypes.SORTED) {
			size += Dirs.size(getModelDir(type, false).toPath());
		}
		size += Dirs.size(getBinDir(false).toPath());
		return size;
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
		return getFile(repoDir, "history.txt", create);
	}

	public Map<String, Object> readData(ModelType type, String refId, String commitId) {
		File file = getDatasetFile(type, refId, commitId, false);
		try {
			if (Files.size(file.toPath()) == 0)
				return new HashMap<>();
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
		try (FileInputStream fis = new FileInputStream(file);
				GZIPInputStream gis = new GZIPInputStream(fis);
				InputStreamReader isr = new InputStreamReader(gis)) {
			return gson.fromJson(isr, new TypeToken<Map<String, Object>>() {
			}.getType());
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	File getDatasetFile(ModelType type, String refId, String commitId,
			boolean create) {
		File datasetDir = getDatasetDir(type, refId, create);
		String filename = commitId + ".json.gz";
		return getFile(datasetDir, filename, create);
	}

	File getBinFile(ModelType type, String refId, String commitId, String filename) {
		File binDir = getBinDir(type, refId, commitId, false);
		if (!binDir.exists())
			return null;
		File file = new File(binDir, filename + ".gz");
		if (file.exists())
			return file;
		return new File(binDir, filename);
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
		if (dir == null)
			throw new IllegalArgumentException("Repository.getDir: Illegal argument: directory is null");
		if (name == null)
			throw new IllegalArgumentException(
					"Repository.getDir(" + dir.getAbsolutePath() + ") : Illegal argument: name is null");
		File file = new File(dir, name);
		if (create && !file.exists())
			file.mkdir();
		return file;
	}

	public static class Settings {

		public boolean publicAccess;
		public boolean prohibitCommits;
		public boolean commentApproval;
		public long maxSize;

		private Settings() {

		}

	}

}
