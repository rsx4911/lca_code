package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.openlca.git.find.Commits;
import org.openlca.git.find.Datasets;
import org.openlca.git.find.Entries;
import org.openlca.git.find.Ids;
import org.openlca.git.find.References;
import org.openlca.git.util.Repositories;
import org.openlca.jsonld.SchemaVersion;
import org.openlca.util.Dirs;
import org.openlca.util.Strings;

import com.greendelta.collaboration.error.RepositoryNotFoundException;
import com.greendelta.collaboration.error.UnsupportedSchemaException;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.SettingsService.Settings;
import com.greendelta.collaboration.util.Routes;

public class Repository implements AutoCloseable {

	private static final Logger log = LogManager.getLogger(Repository.class);
	public final String group;
	public final String name;
	public final Settings<RepositorySetting> settings;
	public final Settings<GroupSetting> groupSettings;
	private final RepositoryPath path;
	final File dir;
	// is only initialized when helpers are initialized, released on close()
	private FileRepository gitRepo;

	Repository(String root, String group, String name, Settings<RepositorySetting> settings,
			Settings<GroupSetting> groupSettings) {
		var fsPath = root + File.separator + group + File.separator + name;
		dir = new File(fsPath);
		this.group = group;
		this.name = name;
		this.path = new RepositoryPath(group, name);
		if (!dir.exists())
			throw new RepositoryNotFoundException(path.toString());
		checkVersion();
		this.settings = settings;
		this.groupSettings = groupSettings;
	}

	public FileRepository gitRepo() {
		if (gitRepo == null) {
			try {
				gitRepo = new FileRepository(dir);
			} catch (IOException e) {
				log.error("Error accessing file repository", e);
				return null;
			}
		}
		return gitRepo;
	}

	public List<String> linkedLibraries() {
		var info = Repositories.infoOf(gitRepo());
		if (info == null || info.libraries() == null)
			return new ArrayList<>();
		return info.libraries();
	}

	public Commits commits() {
		return Commits.of(gitRepo());
	}

	public Datasets datasets() {
		return Datasets.of(gitRepo());
	}

	public References references() {
		return References.of(gitRepo());
	}

	public Entries entries() {
		return Entries.of(gitRepo());
	}

	public Ids ids() {
		return Ids.of(gitRepo());
	}

	public String path() {
		return path.toString();
	}

	public String toFilename() {
		return group + '-' + name + ".zip";
	}

	public SchemaVersion getSchemaVersion() {
		var info = Repositories.infoOf(gitRepo());
		if (info == null)
			return null;
		return info.schemaVersion();
	}

	public long getSize() {
		return Dirs.size(dir.toPath());
	}

	public String getLabel() {
		return groupSettings.get(GroupSetting.LABEL, this.group) + "/"
				+ settings.get(RepositorySetting.LABEL, this.name);
	}

	private void checkVersion() {
		try {
			if (commits().find().all().isEmpty())
				return;
			var version = getSchemaVersion();
			if (version == null || !version.isCurrent())
				throw new UnsupportedSchemaException(version);
		} catch (Exception e) {
			log.error("Could not read context.json", e);
			throw new UnsupportedSchemaException(null);
		}
	}

	File getAvatarFile() {
		return new File(dir, "avatar");
	}

	public File getCachedJsonFile() {
		return new File(dir, "cached-json.zip");
	}

	public String toId() {
		return group + "/" + name;
	}

	@Override
	public void close() {
		if (gitRepo != null) {
			gitRepo.close();
		}
		gitRepo = null;
	}

	public static class InsufficientStorageException extends RuntimeException {

		private static final long serialVersionUID = 543921197834005033L;

		InsufficientStorageException(String message) {
			super(message);
		}

	}

	public static class RepositoryPath {

		public final String group;
		public final String repo;

		public RepositoryPath(String string) {
			var split = split(string);
			this.group = split[0];
			this.repo = split[1];
		}

		private String[] split(String string) {
			if (Strings.nullOrEmpty(string))
				return new String[] { null, null };
			if (string.startsWith("/")) {
				string = string.substring(1);
			}
			if (string.endsWith("/")) {
				string = string.substring(0, string.length() - 1);
			}
			if (!string.contains("/"))
				return new String[] { string, null };
			return string.split("/");
		}

		public RepositoryPath(String group, String repo) {
			this.group = group;
			this.repo = repo;
		}

		public boolean isGroupOrRepo() {
			return isGroup() || isRepo();
		}

		public boolean isGroup() {
			return !Strings.nullOrEmpty(group) && !Routes.isReserved(group);
		}

		public boolean isRepo() {
			if (Strings.nullOrEmpty(group) || Routes.isReserved(group))
				return false;
			return !Strings.nullOrEmpty(repo) && !Routes.isReserved(repo);
		}

		@Override
		public String toString() {
			if (Strings.nullOrEmpty(repo))
				return group;
			return group + "/" + repo;
		}

	}

}
