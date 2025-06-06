package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.openlca.core.model.ModelType;
import org.openlca.git.Compatibility;
import org.openlca.git.model.Commit;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.jsonld.SchemaVersion;
import org.openlca.util.Dirs;
import org.openlca.util.Strings;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.SettingsService.Settings;
import com.greendelta.collaboration.util.Routes;

public class Repository extends OlcaRepository implements AutoCloseable {

	public final String group;
	public final String name;
	public final Settings<RepositorySetting> settings;
	public final Settings<GroupSetting> groupSettings;
	private final RepositoryPath path;

	Repository(String root, String group, String name, Settings<RepositorySetting> settings,
			Settings<GroupSetting> groupSettings) throws IOException {
		super(getDir(root, group, name));
		this.group = group;
		this.name = name;
		this.path = new RepositoryPath(group, name);
		if (!dir.exists())
			throw Response.notFound("No repository '" + path.toString() + "' found");
		this.settings = settings;
		this.groupSettings = groupSettings;
	}

	static File getDir(String root, String group, String name) {
		var fsPath = root + File.separator + group + File.separator + name;
		return new File(fsPath);
	}

	public Set<String> getLinkedLibraries() {
		return commits.find().all().stream()
				.map(this::getLibraries)
				.flatMap(Set::stream)
				.distinct()
				.collect(Collectors.toSet());
	}

	public int getServerVersion() {
		return Compatibility.getRepositoryServerVersion(this);
	}

	public int getSchemaVersion() {
		var head = getHeadCommit();
		if (head == null)
			return SchemaVersion.current().value();
		var info = getInfo();
		if (info == null)
			return SchemaVersion.fallback().value();
		return info.schemaVersion().value();
	}

	public Set<ModelType> getModelTypes(Commit commit) {
		var types = new HashSet<ModelType>();
		if (commit == null)
			return types;
		references.find().includeCategories().commit(commit.id).iterate(entry -> {
			if (entry.isModelType) {
				types.add(entry.type);
			}
		});
		return types;
	}

	public String path() {
		return path.toString();
	}

	public String toFilename() {
		return group + '-' + name + ".zip";
	}

	public long getSize() {
		return Dirs.size(dir.toPath());
	}

	public String getLabel() {
		return groupSettings.get(GroupSetting.LABEL, this.group) + "/"
				+ settings.get(RepositorySetting.LABEL, this.name);
	}

	File getAvatarFile() {
		return new File(dir, "avatar");
	}

	public File getCachedJsonFile(String commitId) {
		return new File(dir, "cached-json-" + commitId + ".zip");
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

		private RepositoryPath(String group, String repo) {
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

		public static RepositoryPath of(String id) {
			var split = split(id);
			return of(split[0], split[1]);
		}

		public static RepositoryPath of(String group, String name) {
			return new RepositoryPath(group, name);
		}

		private static String[] split(String string) {
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

		@Override
		public String toString() {
			if (Strings.nullOrEmpty(repo))
				return group;
			return group + "/" + repo;
		}

	}

}
