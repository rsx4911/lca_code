package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.Strings;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.openlca.util.Dirs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.service.GroupService.GroupSettings;

public class Repository {

	private final static Logger log = LogManager.getLogger(Repository.class);
	private final Gson gson = new Gson();
	public final String group;
	public final String name;
	public final RepositorySettings settings;
	public GroupSettings groupSettings;
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

	private RepositorySettings loadSettings() {
		File file = new File(repoDir, "settings.json");
		RepositorySettings settings = SettingsCache.loadSettings(file, RepositorySettings.class);
		if (settings == null)
			return new RepositorySettings();
		return settings;
	}

	private Map<String, Role> loadLibraryRestrictions() {
		File file = new File(repoDir, "library-restrictions.json");
		Map<String, Role> restrictions = SettingsCache.loadSettings(file, new TypeToken<Map<String, Role>>() {
		}.getType());
		if (restrictions == null)
			return new HashMap<>();
		return restrictions;
	}

	public String toId() {
		return toId(group, name);
	}

	public String toFilename() {
		return group + '-' + name + ".zip";
	}

	void setSetting(RepositorySetting setting, Object value) {
		setting.set(settings, value);
		SettingsCache.saveSettings(new File(repoDir, "settings.json"), settings);
	}

	void setRestriction(String library, Role role) {
		if (role == null) {
			libraryRestrictions.remove(library);
		} else {
			libraryRestrictions.put(library, role);
		}
		SettingsCache.saveSettings(new File(repoDir, "library-restrictions.json"), libraryRestrictions);
	}

	public static String toId(String group, String name) {
		return group + "/" + name;
	}

	public String getSchemaVersion() {
		try {
			File file = new File(repoDir, "context.json");
			JsonElement context = SettingsCache.loadSettings(file, JsonElement.class);
			if (context == null)
				return null;
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

	public String getLabel() {
		String group = Strings.isNullOrEmpty(groupSettings.label) ? this.group : groupSettings.label;
		String repo = Strings.isNullOrEmpty(settings.label) ? this.name : settings.label;
		return group + "/" + repo;
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
		for (ModelType type : ModelType.values()) {
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

	File getDatasetFile(ModelType type, String refId, String commitId, boolean create) {
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

	public File getCachedJsonFile() {
		return new File(repoDir, "cached-json.zip");
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

	public static class RepositorySettings {

		public boolean publicAccess;
		public boolean prohibitCommits;
		public boolean commentApproval;
		public boolean jsonFileGeneration;
		public long maxSize;
		public String label;
		public String version;
		public List<String> tags;
		public String description;
		public String sourceInfo;
		public String contactInfo;
		public String projectInfo;
		public String projectFunding;
		public String appropriateUse;
		public String dqAssessment;
		public String citation;
		public String typeOfData;

		private RepositorySettings() {

		}

	}

	public static enum RepositorySetting {

		PUBLIC_ACCESS("publicAccess", RepositorySetting::parseBoolean),
		PROHIBIT_COMMITS("prohibitCommits", RepositorySetting::parseBoolean),
		COMMENT_APPROVAL("commentApproval", RepositorySetting::parseBoolean),
		JSON_FILE_GENERATION("jsonFileGeneration", RepositorySetting::parseBoolean),
		MAX_SIZE("maxSize", RepositorySetting::parseLong), LABEL("label", RepositorySetting::parseString),
		VERSION("version", RepositorySetting::parseString), TAGS("tags", RepositorySetting::parseStringList),
		DESCRIPTION("description", RepositorySetting::parseString),
		SOURCE_INFO("sourceInfo", RepositorySetting::parseString),
		CONTACT_INFO("contactInfo", RepositorySetting::parseString),
		PROJECT_INFO("projectInfo", RepositorySetting::parseString),
		PROJECT_FUNDING("projectFunding", RepositorySetting::parseString),
		APPROPRIATE_USE("appropriateUse", RepositorySetting::parseString),
		DQ_ASSESSMENT("dqAssessment", RepositorySetting::parseString),
		CITATION("citation", RepositorySetting::parseString),
		TYPE_OF_DATA("typeOfData", RepositorySetting::parseString);

		private final Field field;
		private final Function<Object, ?> converter;

		private RepositorySetting(String fieldName, Function<Object, ?> converter) {
			this.converter = converter;
			this.field = getField(fieldName);
		}

		private Field getField(String fieldName) {
			try {
				return RepositorySettings.class.getDeclaredField(fieldName);
			} catch (Exception e) {
				log.error("Error registering repository settings field " + fieldName, e);
				return null;
			}
		}

		private void set(RepositorySettings settings, Object value) {
			try {
				value = converter.apply(value);
				boolean wasAccessible = field.isAccessible();
				if (!wasAccessible) {
					field.setAccessible(true);
				}
				field.set(settings, value);
				if (!wasAccessible) {
					field.setAccessible(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private static boolean parseBoolean(Object value) {
			if (value == null)
				return false;
			if (value instanceof Boolean)
				return (boolean) value;
			return Boolean.parseBoolean(value.toString());
		}

		private static long parseLong(Object value) {
			if (value == null)
				return 0;
			if (value instanceof Long || value instanceof Integer)
				return (long) value;
			return Long.parseLong(value.toString());
		}

		private static String parseString(Object value) {
			if (value == null)
				return null;
			return value.toString();
		}

		@SuppressWarnings("unchecked")
		private static List<String> parseStringList(Object value) {
			if (value == null)
				return new ArrayList<>();
			if (value instanceof String[])
				return Arrays.asList((String[]) value);
			if (value instanceof List)
				return (List<String>) value;
			return new ArrayList<>();
		}

		@SuppressWarnings("unchecked")
		public <T> T parse(Object value) {
			return (T) converter.apply(value);
		}

	}

}
