package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.ServerSetting;

@Service
public class LibraryService {

	private static final Logger log = LogManager.getLogger(LibraryService.class);
	private final Map<String, Set<String>> refIds = new HashMap<>();
	private final SettingsService settingsService;

	@Autowired
	public LibraryService(SettingsService settingsService) {
		this.settingsService = settingsService;
		resetLibraries();
	}

	public void resetLibraries() {
		refIds.clear();
		String path = settingsService.get(ServerSetting.LIBRARY_PATH);
		if (path == null || path.isEmpty())
			return;
		var dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
			return;
		for (var file : dir.listFiles()) {
			var filename = file.getName();
			var libraryName = filename.substring(0, filename.lastIndexOf('.'));
			initRefIds(libraryName);
		}
	}

	private void initRefIds(String libraryName) {
		var file = getFile(libraryName);
		if (!file.exists())
			return;
		try {
			var ids = new HashSet<>(Files.readAllLines(file.toPath()));
			refIds.put(libraryName, ids);
		} catch (IOException e) {
			log.error("Error loading ref ids of library " + libraryName, e);
		}
	}

	public Set<String> getLibraryNames(String refId) {
		return refIds.keySet().stream().filter(lib -> refIds.get(lib).contains(refId)).collect(Collectors.toSet());
	}

	public Set<String> getLibraryNames() {
		return refIds.keySet();
	}

	public void putLibrary(String name, Collection<String> refIds) {
		removeLibrary(name);
		this.refIds.put(name, new HashSet<>(refIds));
		var file = getFile(name);
		try {
			Files.write(file.toPath(), refIds);
		} catch (IOException e) {
			log.error("Error saving ref ids of library " + name, e);
		}
	}

	public void removeLibrary(String name) {
		refIds.remove(name);
		var file = getFile(name);
		if (!file.exists())
			return;
		file.delete();
	}

	public Set<String> getRefIds(String library) {
		return refIds.get(library);
	}

	private File getFile(String libraryName) {
		return new File(settingsService.get(ServerSetting.LIBRARY_PATH) + File.separator + libraryName + ".txt");
	}

}
