package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

class SettingsCache {

	private final static Logger log = LogManager.getLogger(SettingsCache.class);
	private final static Map<String, Object> cache = new HashMap<>();

	@SuppressWarnings("unchecked")
	static synchronized <T> T loadSettings(File file, Type typeOfT) {
		if (!file.exists())
			return null;
		if (cache.containsKey(file.getAbsolutePath()))
			return (T) cache.get(file.getAbsolutePath());
		try (FileReader reader = new FileReader(file)) {
			T settings = new Gson().fromJson(reader, typeOfT);
			cache.put(file.getAbsolutePath(), settings);
			return settings;
		} catch (Exception e) {
			log.error("Error loading settings " + file.getAbsolutePath());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	static synchronized <T> T loadSettings(File file, Class<T> clazz, T defaultValue) {
		if (cache.containsKey(file.getAbsolutePath()))
			return (T) cache.get(file.getAbsolutePath());
		try (FileReader reader = new FileReader(file)) {
			T settings = new Gson().fromJson(reader, clazz);
			cache.put(file.getAbsolutePath(), settings);
			return settings;
		} catch (Exception e) {
			log.error("Error loading settings " + file.getAbsolutePath());
			return null;
		}
	}

	static synchronized void saveSettings(File file, Object settings) {
		try (FileWriter writer = new FileWriter(file)) {
			new Gson().toJson(settings, writer);
			cache.put(file.getAbsolutePath(), settings);
		} catch (IOException e) {
			log.error("Error saving settings", e);
			cache.remove(file.getAbsolutePath());
		}
	}

}
