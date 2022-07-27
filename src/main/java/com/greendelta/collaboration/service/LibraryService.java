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
			throw new ForbiddenAccessException("LIBRARIES", "WRITE");
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

	public boolean delete(String id, String access) {
		checkWriteAccess(id, Arrays.asList(access));
		var file = getLibraryFile(id);
		if (!file.exists())
			return false;
		try {
			var accessTypes = removeAccessType(id, access);
			if (accessTypes.isEmpty()) {
				Files.delete(file.toPath());
			}
			return true;
		} catch (IOException e) {
			log.error("Error deleting library from disk", e);
			return false;
		}
	}

	private void checkWriteAccess(String id, List<String> accessTypes) {
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
		throw new ForbiddenAccessException(id, "WRITE");
	}

	private boolean isTeamMember(String teamname, User user) {
		var team = teamService.getForTeamname(teamname);
		if (team == null)
			return false;
		return team.users.contains(user);
	}

	public File get(String id) {
		if (!new AccessCheck().canAccess(id, new ArrayList<>()))
			throw new ForbiddenAccessException(id, "READ");
		var file = getLibraryFile(id);
		if (!file.exists())
			return null;
		return file;
	}

	public LibraryInfo getInfo(String id, boolean isAdminArea) {
		var file = getLibraryFile(id);
		if (!file.exists())
			return null;
		try (var repos = repoService.getAllAccessible()) {
			var linkedIn = repos.stream()
					.filter(repo -> repo.linkedLibraries().contains(id))
					.map(Repository::path)
					.distinct().toList();
			var accesses = getAccessTypes(id);
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

	private File getLibraryFile(String id) {
		return new File(getLibraryPath(), id + ".zip");
	}

	private String getLibraryPath() {
		String libraryPath = settings.get(ServerSetting.LIBRARY_PATH);
		if (libraryPath == null)
			throw new ServiceUnavailableException("Library service unavailable because library path is not set");
		return libraryPath;
	}

	private List<String> getAccessTypes(String id) {
		return settings.get(SettingType.LIBRARY_SETTING, id, null)
				.get(LibrarySetting.ACCESS, new ArrayList<>());
	}

	private List<String> addAccessType(String id, String access) {
		var settingsAccess = LibraryAccess.isTeamAccess(access)
				? settings.ACCESS.TEAM_DATA(teamService.getForTeamname(access))
				: settings.ACCESS.DATA_MANAGER;
		var accessTypes = getAccessTypes(id);
		if (accessTypes.contains(access))
			return accessTypes;
		accessTypes.add(access);
		settings.get(SettingType.LIBRARY_SETTING, id, settingsAccess)
				.set(LibrarySetting.ACCESS, accessTypes);
		return accessTypes;
	}

	private List<String> removeAccessType(String id, String access) {
		var settingsAccess = LibraryAccess.isTeamAccess(access)
				? settings.ACCESS.TEAM_DATA(teamService.getForTeamname(access))
				: settings.ACCESS.DATA_MANAGER;
		var accessTypes = getAccessTypes(id);
		if (!accessTypes.contains(access))
			return accessTypes;
		accessTypes.remove(access);
		if (accessTypes.isEmpty()) {
			settings.get(SettingType.LIBRARY_SETTING, id, settingsAccess)
			.set(LibrarySetting.ACCESS, null);
		} else {
			settings.get(SettingType.LIBRARY_SETTING, id, settingsAccess)
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
			List<String> accessTypes) {

		private LibraryInfo(org.openlca.core.library.LibraryInfo info, List<String> linkedIn,
				List<String> accessTypes) {
			this(info.name(), info.description(), info.isRegionalized(), linkedIn, accessTypes);
		}

	}

}
