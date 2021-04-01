package com.greendelta.collaboration.service.repository;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;

import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.SettingsService.Settings;

public class Repository {

	private static final Logger log = LogManager.getLogger(Repository.class);
	public final String group;
	public final String name;
	public final Settings<RepositorySetting> settings;
	public final Datasets datasets;
	public final Commits commits;
	public final Descriptors descriptors;
	public final Settings<GroupSetting> groupSettings;
	final File dir;

	public static String toId(String group, String name) {
		return group + "/" + name;
	}

	Repository(String root, String group, String name, Settings<RepositorySetting> settings,
			Settings<GroupSetting> groupSettings) {
		String path = root + File.separator + group + File.separator + name;
		dir = new File(path);
		this.group = group;
		this.name = name;
		String id = toId();
		if (!dir.exists())
			throw new RepositoryNotFoundException(id);
		checkVersion();
		this.settings = settings;
		this.groupSettings = groupSettings;
		this.datasets = new Datasets(this);
		this.commits = new Commits(this);
		this.descriptors = new Descriptors(this);
	}

	public String toId() {
		return toId(group, name);
	}

	public String toFilename() {
		return group + '-' + name + ".zip";
	}

	public String getSchemaVersion() {
		// TODO get schema version
		return null;
	}

	public long getSize() {
		// TODO determine size
		return 0;
	}

	public String getLabel() {
		return groupSettings.get(GroupSetting.LABEL, this.group) + "/"
				+ settings.get(RepositorySetting.LABEL, this.name);
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

	File getAvatarFile() {
		return new File(dir, "avatar");
	}

	public File getCachedJsonFile() {
		return new File(dir, "cached-json.zip");
	}

	public static class InsufficientStorageException extends RuntimeException {

		private static final long serialVersionUID = 543921197834005033L;

		InsufficientStorageException(String message) {
			super(message);
		}

	}

}
