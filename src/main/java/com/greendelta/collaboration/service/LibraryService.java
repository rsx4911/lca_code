package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.library.LibraryPackage;
import org.openlca.git.util.Repositories;
import org.openlca.jsonld.PackageInfo;
import org.openlca.util.Dirs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.error.ServiceUnavailableException;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.model.User;
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

	@Autowired
	public LibraryService(UserService userService, RepositoryService repoService, SettingsService settings) {
		this.userService = userService;
		this.repoService = repoService;
		this.settings = settings;
	}

	public List<String> getAllAccessible() {
		String libraryPath = settings.get(ServerSetting.LIBRARY_PATH);
		if (libraryPath == null)
			return new ArrayList<String>();
		var accessCheck = new AccessCheck();
		var libraries = new ArrayList<String>();
		for (var file : new File(libraryPath).listFiles()) {
			if (!file.getName().endsWith(".zip"))
				continue;
			var id = file.getName().substring(0, file.getName().length() - 4);
			if (!accessCheck.canAccess(id))
				continue;
			libraries.add(id);
		}
		return libraries;
	}

	public String insert(InputStream stream, LibraryAccess access) throws IOException {
		if (stream == null)
			return null;
		var libraryPath = getLibraryPath();
		Path tmpFile = null;
		try {
			tmpFile = Files.createTempFile("cs-lib-", ".zip");
			Files.copy(stream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
			var info = LibraryPackage.getInfo(tmpFile.toFile());
			if (info == null)
				return null;
			var id = info.toId();
			var file = new File(libraryPath, id + ".zip");
			if (file.exists())
				throw new IOException("existed");
			Files.copy(tmpFile, file.toPath());
			settings.get(SettingType.LIBRARY_SETTING, id, settings.ACCESS.DATA_MANAGER)
					.set(LibrarySetting.ACCESS, access);
			return id;
		} catch (IOException e) {
			if (!"existed".equals(e.getMessage())) {
				log.error("Error writing library", e);
			}
			throw e;
		} finally {
			Dirs.delete(tmpFile);
		}
	}

	public void update(String id, LibraryAccess access) {
		settings.get(SettingType.LIBRARY_SETTING, id, settings.ACCESS.DATA_MANAGER)
				.set(LibrarySetting.ACCESS, access);
	}

	public boolean delete(String id) {
		var file = new File(getLibraryPath(), id + ".zip");
		if (!file.exists())
			return false;
		try {
			Files.delete(file.toPath());
			return true;
		} catch (IOException e) {
			log.error("Error deleting library from disk", e);
			return false;
		}
	}

	public File get(String id) {
		var file = new File(getLibraryPath(), id + ".zip");
		if (!file.exists())
			return null;
		return file;
	}

	public LibraryInfo getInfo(String id) {
		var file = new File(getLibraryPath(), id + ".zip");
		if (!file.exists())
			return null;
		return new LibraryInfo(LibraryPackage.getInfo(file));
	}

	private String getLibraryPath() {
		String libraryPath = settings.get(ServerSetting.LIBRARY_PATH);
		if (libraryPath == null)
			throw new ServiceUnavailableException("Library service unavailable because library path is not set");
		return libraryPath;
	}

	private class AccessCheck {

		private User user;
		private Set<String> linkedLibraries;

		private AccessCheck() {
			this.user = userService.getCurrentUser();
		}

		private boolean canAccess(String library) {
			if (user.isDataManager())
				return true;
			LibraryAccess access = settings
					.get(SettingType.LIBRARY_SETTING, library, settings.ACCESS.DATA_MANAGER)
					.get(LibrarySetting.ACCESS);
			if (access == null)
				return false;
			return switch (access) {
				case PUBLIC -> true;
				case USER -> !user.isAnonymous();
				case MEMBER -> linkedLibraries().contains(library);
			};
		}

		private Set<String> linkedLibraries() {
			if (linkedLibraries != null)
				return linkedLibraries;
			linkedLibraries = repoService.getAllAccessible().stream()
					.map(Repository::gitRepo)
					.map(Repositories::infoOf)
					.filter(Objects::nonNull)
					.map(PackageInfo::libraries)
					.flatMap(List::stream)
					.distinct()
					.collect(Collectors.toSet());
			return linkedLibraries;
		}

	}
	
	public record LibraryInfo(String name, String version, String description, boolean isRegionalized) {
		
		private LibraryInfo(org.openlca.core.library.LibraryInfo info) {
			this(info.name(), info.version(), info.description(), info.isRegionalized());
		}
		
	}

}
