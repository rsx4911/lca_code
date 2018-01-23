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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class LibraryService {

	private static final Logger log = LogManager.getLogger(LibraryService.class);
	private final Map<String, Set<String>> refIds = new HashMap<>();
	private final String libraryPath;

	@Inject
	public LibraryService(@Named("library.path") String libraryPath) {
		this.libraryPath = libraryPath;
		initLibraries();
	}

	private void initLibraries() {
		File dir = new File(libraryPath);
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

	public String getLibraryName(String refId) {
		for (String libraryName : refIds.keySet()) {
			Set<String> ids = refIds.get(libraryName);
			if (ids.contains(refId))
				return libraryName;
		}
		return null;
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
		return new File(libraryPath + File.separator + libraryName + ".txt");
	}

}
