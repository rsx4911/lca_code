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

import com.greendelta.collaboration.error.ForbiddenAccessException;
import com.greendelta.collaboration.error.ServiceUnavailableException;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.LibrarySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class LibraryService {

	private static final Logger log = LogManager.getLogger(LibraryService.class);
	private final UserService userService;
	private final RepositoryService repoService;
	private final TeamService teamService;
	private final SettingsService settings;

	@Autowired
	public LibraryService(UserService userService, RepositoryService repoService, TeamService teamService,
			SettingsService settings) {
		this.userService = userService;
		this.repoService = repoService;
		this.teamService = teamService;
		this.settings = settings;
	}

	public List<String> getAllAccessible() {
		return getAccessibleForTeam(null);
	}

	public List<String> getAccessibleForTeam(String teamname) {
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isDataManager() && (teamname == null || !isTeamMember(teamname, currentUser)))
			throw new ForbiddenAccessException("LIBRARIES", "READ");
		var libraryPath = getLibraryPath();
		if (libraryPath == null)
			return new ArrayList<String>();
		var accessCheck = new AccessCheck();
		var libraries = new ArrayList<String>();
		for (var file : new File(libraryPath).listFiles()) {
			if (!file.getName().endsWith(".zip"))
				continue;
			var id = file.getName().substring(file.getName().indexOf("_") + 1, file.getName().lastIndexOf(".zip"));
			if (!accessCheck.canAccess(id, teamname))
				continue;
			libraries.add(id);
		}
		return libraries;
	}

	public String insert(InputStream stream, String access) throws IOException {
		if (stream == null)
			return null;
		var currentUser = userService.getCurrentUser();
		if (!currentUser.isDataManager() && (!LibraryAccess.isTeamAccess(access) || !isTeamMember(access, currentUser)))
			throw new ForbiddenAccessException("LIBRARIES", "WRITE");
		Path tmpFile = null;
		try {
			tmpFile = Files.createTempFile("cs-lib-", ".zip");
			Files.copy(stream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
			var info = LibraryPackage.getInfo(tmpFile.toFile());
			if (info == null)
				return null;
			var id = info.name();
			var file = getLibraryFile(id, access);
			if (file.exists())
				throw new IOException("existed");
			Files.copy(tmpFile, file.toPath());
			setAccessType(info.name(), access);
			return info.name();
		} catch (IOException e) {
			if (!"existed".equals(e.getMessage())) {
				log.error("Error writing library", e);
			}
			throw e;
		} finally {
			Dirs.delete(tmpFile);
		}
	}

	public boolean delete(String id) {
		var access = getAccessType(id);
		checkWriteAccess(id, access);
		var file = getLibraryFile(id, access);
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

	private void checkWriteAccess(String id, String access) {
		var currentUser = userService.getCurrentUser();
		if (currentUser.isDataManager())
			return;
		if (!LibraryAccess.isTeamAccess(access))
			throw new ForbiddenAccessException(id, "WRITE");
		var team = teamService.getForTeamname(access);
		if (team == null || !team.users.contains(currentUser))
			throw new ForbiddenAccessException(id, "WRITE");
	}

	private boolean isTeamMember(String teamname, User user) {
		var team = teamService.getForTeamname(teamname);
		if (team == null)
			return false;
		return team.users.contains(user);
	}

	public File get(String id) {
		if (!new AccessCheck().canAccess(id, null))
			throw new ForbiddenAccessException(id, "READ");
		var file = getLibraryFile(id, getAccessType(id));
		if (!file.exists())
			return null;
		return file;
	}

	public LibraryInfo getInfo(String id) {
		var file = getLibraryFile(id, getAccessType(id));
		if (!file.exists())
			return null;
		try (var repos = repoService.getAllAccessible()) {
			var linkedIn = repos.stream()
					.filter(repo -> repo.linkedLibraries().contains(id))
					.map(Repository::toId)
					.distinct().toList();
			var access = getAccessType(id);
			return new LibraryInfo(LibraryPackage.getInfo(file), linkedIn, access);
		}
	}

	private File getLibraryFile(String id, String access) {
		return new File(access + "_" + id + ".zip");
	}

	private String getLibraryPath() {
		String libraryPath = settings.get(ServerSetting.LIBRARY_PATH);
		if (libraryPath == null)
			throw new ServiceUnavailableException("Library service unavailable because library path is not set");
		return libraryPath;
	}

	private String getAccessType(String id) {
		return settings.get(SettingType.LIBRARY_SETTING, id, settings.ACCESS.USER)
				.get(LibrarySetting.ACCESS);
	}

	private void setAccessType(String id, String access) {
		var settingsAccess = LibraryAccess.isTeamAccess(access)
				? settings.ACCESS.TEAM_DATA(teamService.getForTeamname(access))
				: settings.ACCESS.DATA_MANAGER;
		settings.get(SettingType.LIBRARY_SETTING, id, settingsAccess)
				.set(LibrarySetting.ACCESS, access);
	}

	private class AccessCheck {

		private User user;
		private Set<String> linkedLibraries;

		private AccessCheck() {
			this.user = userService.getCurrentUser();
		}

		private boolean canAccess(String library, String teamname) {
			if (user.isDataManager())
				return true;
			String access = getAccessType(library);
			if (access == null)
				return false;
			if (teamname != null && !access.equals(teamname))
				return false;
			return switch (access) {
				case "PUBLIC" -> true;
				case "USER" -> !user.isAnonymous();
				case "MEMBER" -> linkedLibraries().contains(library);
				default -> isTeamMember(access, user);
			};
		}

		private Set<String> linkedLibraries() {
			if (linkedLibraries != null)
				return linkedLibraries;
			try (var accessible = repoService.getAllAccessible()) {
				linkedLibraries = accessible.stream()
						.map(Repository::gitRepo)
						.map(Repositories::infoOf)
						.filter(Objects::nonNull)
						.map(PackageInfo::libraries)
						.flatMap(List::stream)
						.distinct()
						.collect(Collectors.toSet());
			}
			return linkedLibraries;
		}

	}

	public record LibraryInfo(String name, String description, boolean isRegionalized, List<String> linkedIn,
			String access) {

		private LibraryInfo(org.openlca.core.library.LibraryInfo info, List<String> linkedIn, String access) {
			this(info.name(), info.description(), info.isRegionalized(), linkedIn, access);
		}

	}

}
