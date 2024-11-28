package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.library.LibraryPackage;
import org.openlca.git.model.Commit;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Dirs;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.settings.LibrarySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class LibraryService {

	private static final Logger log = LogManager.getLogger(LibraryService.class);
	private final UserService userService;
	private final RepositoryService repoService;
	private final SettingsService settings;

	public LibraryService(UserService userService, RepositoryService repoService, SettingsService settings) {
		this.userService = userService;
		this.repoService = repoService;
		this.settings = settings;
	}

	public List<String> getAllAccessible() {
		var libraryPath = getLibraryPath();
		if (libraryPath == null)
			return new ArrayList<String>();
		var libraries = new ArrayList<String>();
		var linkedLibraries = getLinkedLibraries();
		for (var file : new File(libraryPath).listFiles()) {
			var name = file.getName();
			if (!name.endsWith(".zip"))
				continue;
			var id = name.substring(0, name.lastIndexOf(".zip"));
			if (!canAccess(id, linkedLibraries))
				continue;
			libraries.add(id);
		}
		return libraries;
	}

	public File get(String library) {
		if (!canAccess(library))
			throw Response.forbidden(library, Permission.READ);
		var file = getLibraryFile(library);
		if (!file.exists())
			return null;
		return file;
	}

	public LibraryInfo getInfo(String library) {
		var file = getLibraryFile(library);
		if (!file.exists())
			return null;
		try (var repos = repoService.getAllAccessible()) {
			var linkedIn = repos.stream()
					.filter(repo -> repo.getLinkedLibraries().contains(library))
					.map(Repository::path)
					.distinct().toList();
			var currentlyLinkedIn = repos.stream()
					.filter(repo -> repo.getLibraries().contains(library))
					.map(Repository::path)
					.distinct().toList();
			LibraryAccess access = getSetting(library, LibrarySetting.ACCESS);
			String owner = getSetting(library, LibrarySetting.OWNER);
			return new LibraryInfo(LibraryPackage.getInfo(file), linkedIn, currentlyLinkedIn, owner, access);
		}
	}

	public boolean isPublic(String library) {
		var file = getLibraryFile(library);
		if (!file.exists())
			return false;
		LibraryAccess access = getSetting(library, LibrarySetting.ACCESS);
		return access == LibraryAccess.PUBLIC;
	}

	public String getLibraryUrl(String library) {
		var serverUrl = settings.serverConfig.getServerUrl();
		return serverUrl + "/ws/" + (isPublic(library) ? "public/libraries/" : "libraries/") + library;
	}

	public File getLibraryFile(String library) {
		return new File(getLibraryPath(), library + ".zip");
	}

	private String getLibraryPath() {
		String libraryPath = settings.get(ServerSetting.LIBRARY_PATH);
		if (libraryPath == null)
			throw Response.unavailable("Library service unavailable because library path is not set");
		return libraryPath;
	}

	public List<LibraryLink> getLinkedLibraries(Repository repo, Commit commit) {
		return repo.getLibraries(commit).stream()
				.map(lib -> new LibraryLink(lib, getLibraryUrl(lib)))
				.collect(Collectors.toList());
	}

	public String insert(InputStream stream, LibraryAccess access) throws IOException {
		if (stream == null)
			return null;
		Path tmpFile = null;
		try {
			tmpFile = Files.createTempFile("cs-lib-", ".zip");
			Files.copy(stream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
			var info = LibraryPackage.getInfo(tmpFile.toFile());
			if (info == null)
				return null;
			var library = info.name();
			if (!canManage(library))
				throw Response.forbidden(library, Permission.CREATE);
			var user = userService.getCurrentUser();
			var owner = !user.isLibraryManager()
					? user.username
					: null;
			var file = getLibraryFile(library);
			Files.copy(tmpFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			setSetting(library, LibrarySetting.ACCESS, access);
			setSetting(library, LibrarySetting.OWNER, owner);
			return info.name();
		} finally {
			Dirs.delete(tmpFile);
		}
	}

	public boolean update(String library, LibraryAccess access) {
		if (!canManage(library))
			throw Response.forbidden(library, Permission.WRITE);
		setSetting(library, LibrarySetting.ACCESS, access);
		return true;
	}

	public boolean delete(String library) {
		if (!canManage(library))
			throw Response.forbidden(library, Permission.DELETE);
		var file = getLibraryFile(library);
		if (!file.exists())
			return false;
		try {
			deleteSetting(library, LibrarySetting.ACCESS);
			Files.delete(file.toPath());
			return true;
		} catch (IOException e) {
			log.error("Error deleting library from disk", e);
			return false;
		}
	}

	private boolean canAccess(String library) {
		return canAccess(library, getLinkedLibraries());
	}

	private boolean canAccess(String library, Set<String> linkedLibraries) {
		var user = userService.getCurrentUser();
		if (user.isLibraryManager())
			return true;
		LibraryAccess access = getSetting(library, LibrarySetting.ACCESS);
		if (access == LibraryAccess.PUBLIC)
			return true;
		if (access == LibraryAccess.USER)
			return !user.isAnonymous();
		return linkedLibraries.contains(library);
	}

	private boolean canManage(String library) {
		var file = getLibraryFile(library);
		if (!file.exists())
			return true;
		var user = userService.getCurrentUser();
		if (user.isLibraryManager())
			return true;
		var owner = getSetting(library, LibrarySetting.OWNER);
		return user.username.equals(owner);
	}

	private Set<String> getLinkedLibraries() {
		try (var accessible = repoService.getAllAccessible()) {
			return accessible.stream()
					.map(Repository::getLinkedLibraries)
					.flatMap(Set::stream)
					.collect(Collectors.toSet());
		}
	}

	private <T> T getSetting(String name, LibrarySetting setting) {
		return settings.get(SettingType.LIBRARY_SETTING, name, null).get(setting);
	}

	private void setSetting(String name, LibrarySetting setting, Object value) {
		settings.get(SettingType.LIBRARY_SETTING, name, null).set(setting, value);
	}

	private void deleteSetting(String library, LibrarySetting setting) {
		settings.get(SettingType.LIBRARY_SETTING, library, null).delete(setting);
	}

	public record LibraryInfo(String name, String description, boolean isRegionalized, List<String> linkedIn,
			List<String> currentlyLinkedIn,
			String owner, LibraryAccess access) {

		private LibraryInfo(org.openlca.core.library.LibraryInfo info, List<String> linkedIn,
				List<String> currentlyLinkedIn, String owner,
				LibraryAccess access) {
			this(info.name(), info.description(), info.isRegionalized(), linkedIn, currentlyLinkedIn, owner, access);
		}

	}

}
