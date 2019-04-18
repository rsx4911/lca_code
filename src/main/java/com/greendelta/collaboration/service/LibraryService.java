package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.service.user.AccessService;

@Singleton
public class LibraryService {

	private static final Logger log = LogManager.getLogger(LibraryService.class);
	private final Map<String, Set<String>> refIds = new HashMap<>();
	private final SettingsService settingsService;
	private final AccessService accessService;

	@Inject
	public LibraryService(SettingsService settingsService, AccessService accessService) {
		this.settingsService = settingsService;
		this.accessService = accessService;
		resetLibraries();
	}

	public void resetLibraries() {
		refIds.clear();
		String path = settingsService.get(Key.LIBRARY_PATH);
		if (path == null || path.isEmpty())
			return;
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
			return;
		for (File file : dir.listFiles()) {
			String filename = file.getName();
			String libraryName = filename.substring(0, filename.lastIndexOf('.'));
			initRefIds(libraryName);
		}
	}

	private void initRefIds(String libraryName) {
		File file = getFile(libraryName);
		if (!file.exists())
			return;
		try {
			Set<String> ids = new HashSet<>(Files.readAllLines(file.toPath()));
			refIds.put(libraryName, ids);
		} catch (IOException e) {
			String m = "Error loading ref ids of library " + libraryName;
			log.error(m, e);
		}
	}

	public Set<String> getLibraryNames(String refId) {
		Set<String> names = new HashSet<>();
		for (String libraryName : refIds.keySet()) {
			Set<String> ids = refIds.get(libraryName);
			if (!ids.contains(refId))
				continue;
			names.add(libraryName);
		}
		return names;
	}

	public Set<String> getLibraryNames() {
		return refIds.keySet();
	}

	public void putLibrary(String name, Collection<String> refIds) {
		removeLibrary(name);
		this.refIds.put(name, new HashSet<>(refIds));
		File file = getFile(name);
		try {
			Files.write(file.toPath(), refIds);
		} catch (IOException e) {
			log.error("Error saving ref ids of library " + name, e);
		}
	}

	public void removeLibrary(String name) {
		refIds.remove(name);
		File file = getFile(name);
		if (!file.exists())
			return;
		file.delete();
	}

	public Set<String> getRefIds(String library) {
		return refIds.get(library);
	}

	private File getFile(String libraryName) {
		return new File(settingsService.get(Key.LIBRARY_PATH) + File.separator + libraryName + ".txt");
	}

	public void setRestriction(Repository repo, String library, Role restriction) {
		if (!accessService.canSetSettings(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "SET_SETTING");
		repo.setRestriction(library, restriction);
	}

	public Map<String, Role> getRestrictions(Repository repo) {
		Map<String, Role> restrictions = new HashMap<>();
		for (String library : getLibraryNames()) {
			restrictions.put(library, repo.libraryRestrictions.get(library));
		}
		return restrictions;
	}

}
