package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.library.LibraryPackage;
import org.openlca.git.RepositoryInfo;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Dirs;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.model.Permission;
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

	public LibraryService(UserService userService, RepositoryService repoService, TeamService teamService,
			SettingsService settings) {
		this.userService = userService;
		this.repoService = repoService;
		this.teamService = teamService;
		this.settings = settings;
	}

	public List<String> getAllAccessible() {
		return getAccessibleForTeams(new ArrayList<>());
	}

	public List<String> getAccessibleForTeams() {
		var currentUser = userService.getCurrentUser();
		var teams = teamService.getTeamsFor(currentUser);
		if (teams.isEmpty())
			return new ArrayList<>();
		return getAccessibleForTeams(teams.stream().map(team -> team.teamname).toList());
	}

	public List<String> getAccessibleForTeams(List<String> teams) {
		var libraryPath = getLibraryPath();
		if (libraryPath == null)
			return new ArrayList<String>();
		var accessCheck = new AccessCheck();
		var libraries = new ArrayList<String>();
		for (var file : new File(libraryPath).listFiles()) {
			var name = file.getName();
			if (!name.endsWith(".zip"))
				continue;
			var id = name.substring(0, name.lastIndexOf(".zip"));
			if (!accessCheck.canAccess(id, teams))
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
			throw Response.forbidden("LIBRARIES", Permission.WRITE);
		Path tmpFile = null;
		try {
			tmpFile = Files.createTempFile("cs-lib-", ".zip");
			Files.copy(stream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
			var info = LibraryPackage.getInfo(tmpFile.toFile());
			if (info == null)
				return null;
			var id = info.name();
			var file = getLibraryFile(id);
			if (file.exists() && getAccessTypes(id).contains(access))
				throw new IOException("existed");
			if (!file.exists()) {
				Files.copy(tmpFile, file.toPath());
			}
			addAccessType(info.name(), access);
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

	public boolean delete(String name, String access) {
		checkWriteAccess(name, Arrays.asList(access));
		var file = getLibraryFile(name);
		if (!file.exists())
			return false;
		try {
			var accessTypes = removeAccessType(name, access);
			if (accessTypes.isEmpty()) {
				Files.delete(file.toPath());
			}
			return true;
		} catch (IOException e) {
			log.error("Error deleting library from disk", e);
			return false;
		}
	}

	private void checkWriteAccess(String name, List<String> accessTypes) {
		var currentUser = userService.getCurrentUser();
		if (currentUser.isDataManager())
			return;
		for (var access : accessTypes) {
			if (LibraryAccess.isTeamAccess(access)) {
				var team = teamService.getForTeamname(access);
				if (team != null && team.users.contains(currentUser))
					return;
			}
		}
		throw Response.forbidden(name, Permission.WRITE);
	}

	private boolean isTeamMember(String teamname, User user) {
		var team = teamService.getForTeamname(teamname);
		if (team == null)
			return false;
		return team.users.contains(user);
	}

	public File get(String name) {
		if (!new AccessCheck().canAccess(name, new ArrayList<>()))
			throw Response.forbidden(name, Permission.READ);
		var file = getLibraryFile(name);
		if (!file.exists())
			return null;
		return file;
	}

	public LibraryInfo getInfo(String name, boolean isAdminArea) {
		var file = getLibraryFile(name);
		if (!file.exists())
			return null;
		try (var repos = repoService.getAllAccessible()) {
			var linkedIn = repos.stream()
					.filter(repo -> repo.linkedLibraries().contains(name))
					.map(Repository::path)
					.distinct().toList();
			var accesses = getAccessTypes(name);
			if (!isAdminArea) {
				for (var access : LibraryAccess.values()) {
					accesses.remove(access.name());
				}
			}
			if (accesses.isEmpty())
				return null;
			return new LibraryInfo(LibraryPackage.getInfo(file), linkedIn, accesses);
		}
	}

	public boolean isPublic(String name) {
		var file = getLibraryFile(name);
		if (!file.exists())
			return false;
		var accesses = getAccessTypes(name);
		return accesses.contains(LibraryAccess.PUBLIC.name());
	}

	private File getLibraryFile(String name) {
		return new File(getLibraryPath(), name + ".zip");
	}

	private String getLibraryPath() {
		String libraryPath = settings.get(ServerSetting.LIBRARY_PATH);
		if (libraryPath == null)
			throw Response.unavailable("Library service unavailable because library path is not set");
		return libraryPath;
	}

	private List<String> getAccessTypes(String name) {
		return settings.get(SettingType.LIBRARY_SETTING, name, null)
				.get(LibrarySetting.ACCESS, new ArrayList<>());
	}

	private List<String> addAccessType(String name, String access) {
		var settingsAccess = LibraryAccess.isTeamAccess(access)
				? settings.ACCESS.TEAM_DATA(teamService.getForTeamname(access))
				: settings.ACCESS.DATA_MANAGER;
		var accessTypes = getAccessTypes(name);
		if (accessTypes.contains(access))
			return accessTypes;
		accessTypes.add(access);
		settings.get(SettingType.LIBRARY_SETTING, name, settingsAccess)
				.set(LibrarySetting.ACCESS, accessTypes);
		return accessTypes;
	}

	private List<String> removeAccessType(String name, String access) {
		var settingsAccess = LibraryAccess.isTeamAccess(access)
				? settings.ACCESS.TEAM_DATA(teamService.getForTeamname(access))
				: settings.ACCESS.DATA_MANAGER;
		var accessTypes = getAccessTypes(name);
		if (!accessTypes.contains(access))
			return accessTypes;
		accessTypes.remove(access);
		if (accessTypes.isEmpty()) {
			settings.get(SettingType.LIBRARY_SETTING, name, settingsAccess)
					.set(LibrarySetting.ACCESS, null);
		} else {
			settings.get(SettingType.LIBRARY_SETTING, name, settingsAccess)
					.set(LibrarySetting.ACCESS, accessTypes);
		}
		return accessTypes;
	}

	private class AccessCheck {

		private User user;
		private Set<String> linkedLibraries;

		private AccessCheck() {
			this.user = userService.getCurrentUser();
		}

		private boolean canAccess(String library, List<String> teamnames) {
			var accesses = getAccessTypes(library);
			if (accesses.isEmpty())
				return true;
			if (!teamnames.isEmpty())
				return teamnames.stream()
						.filter(accesses::contains)
						.filter(access -> isTeamMember(access, user))
						.count() > 0;
			if (user.isDataManager())
				return true;
			for (var access : accesses) {
				var hasAccess = switch (access) {
					case "PUBLIC" -> true;
					case "USER" -> !user.isAnonymous();
					case "MEMBER" -> linkedLibraries().contains(library);
					default -> isTeamMember(access, user);
				};
				if (hasAccess)
					return true;
			}
			return false;
		}

		private Set<String> linkedLibraries() {
			if (linkedLibraries != null)
				return linkedLibraries;
			try (var accessible = repoService.getAllAccessible()) {
				linkedLibraries = accessible.stream()
						.map(Repository::getInfo)
						.filter(Objects::nonNull)
						.map(RepositoryInfo::libraries)
						.flatMap(List::stream)
						.map(LibraryLink::id)
						.distinct()
						.collect(Collectors.toSet());
			}
			return linkedLibraries;
		}

	}

	public Function<LibraryLink, String> getLibraryUrlResolver() {
		var serverUrl = settings.serverConfig.getServerUrl();
		return lib -> serverUrl + "/ws/" + (isPublic(lib.id()) ? "public/libraries/" : "libraries/") + lib.id();
	}

	public record LibraryInfo(String name, String description, boolean isRegionalized, List<String> linkedIn,
			List<String> accessTypes) {

		private LibraryInfo(org.openlca.core.library.LibraryInfo info, List<String> linkedIn,
				List<String> accessTypes) {
			this(info.name(), info.description(), info.isRegionalized(), linkedIn, accessTypes);
		}

	}

}
