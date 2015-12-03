package com.greendelta.cloud.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openlca.cloud.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryService {

	private static final String libraryPath = "libraries";
	private static final String namesFile = "names.properties";
	private static final Map<String, String> libraries = new HashMap<>();
	private static final Map<String, Set<String>> refIds = new HashMap<>();
	private static final Logger log = LoggerFactory
			.getLogger(LibraryService.class);

	static {
		initLibraries();
		for (String id : libraries.keySet())
			initRefIds(id);
	}

	private static void initLibraries() {
		String path = Strings.concat(libraryPath, "/", namesFile);
		try (InputStream s = LibraryService.class.getResourceAsStream(path)) {
			Properties properties = new Properties();
			properties.load(s);
			for (Object key : properties.keySet()) {
				String id = key.toString();
				libraries.put(id, properties.getProperty(id));
			}
		} catch (IOException e) {
			log.error("Error loading library names", e);
		}
	}

	private static void initRefIds(String libraryId) {
		String path = Strings.concat(libraryPath, "/", libraryId, ".txt");
		try (InputStream s = LibraryService.class.getResourceAsStream(path);
				InputStreamReader r = new InputStreamReader(s);
				BufferedReader reader = new BufferedReader(r)) {
			Set<String> ids = new HashSet<>();
			String line = null;
			while ((line = reader.readLine()) != null)
				if (!line.trim().isEmpty())
					ids.add(line);
			refIds.put(libraryId, ids);
		} catch (IOException e) {
			String m = Strings.concat("Error loading ref ids of ", libraryId);
			log.error(m, e);
		}
	}

	public String getLibraryName(String refId) {
		for (String libraryId : libraries.keySet()) {
			Set<String> ids = refIds.get(libraryId);
			if (ids.contains(refId))
				return libraries.get(libraryId);
		}
		return null;
	}

}
