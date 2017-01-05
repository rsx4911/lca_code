package com.greendelta.collaboration.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class LibraryService {

	private static final Logger log = LoggerFactory.getLogger(LibraryService.class);
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
		String path = libraryPath + File.separator + libraryName + ".txt";
		try (InputStream s = new FileInputStream(path);
				InputStreamReader r = new InputStreamReader(s);
				BufferedReader reader = new BufferedReader(r)) {
			Set<String> ids = new HashSet<>();
			String line = null;
			while ((line = reader.readLine()) != null)
				if (!line.trim().isEmpty())
					ids.add(line);
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
		File file = new File(libraryPath + File.separator + name + ".txt");
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (String refId : refIds) {
				writer.write(refId);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			String m = "Error saving ref ids of library " + name;
			log.error(m, e);
		}
	}

	public void removeLibrary(String name) {
		refIds.remove(name);
		File file = new File(libraryPath + File.separator + name + ".txt");
		if (!file.exists())
			return;
		file.delete();
	}

	public Set<String> getRefIds(String library) {
		return refIds.get(library);
	}

}
