package com.greendelta.collaboration.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DataAccessor {

	private final static Logger log = LoggerFactory.getLogger(DataAccessor.class);

	public void write(File file, byte[] data) {
		try {
			file.mkdirs();
			if (data == null || data.length == 0)
				file.createNewFile();
			else
				Files.write(file.toPath(), data);
		} catch (IOException e) {
			log.error("Error writing json data to file " + file.getAbsolutePath(), e);
		}
	}

	public byte[] read(File file) {
		if (file == null || !file.exists())
			return null;
		if (file.length() == 0)
			return new byte[0];
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			String path = file.getAbsolutePath();
			String message = "Error reading json data from file " + path;
			log.error(message, e);
			return null;
		}
	}

	public List<Commit> readHistory(File file, Filter<Commit> filter) {
		if (file == null)
			return Collections.emptyList();
		if (!file.exists())
			return Collections.emptyList();
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			if (lines.isEmpty())
				return Collections.emptyList();
			List<Commit> commits = new ArrayList<>();
			for (String entry : lines) {
				if (entry.trim().isEmpty())
					continue;
				Commit commit = Commit.parse(entry);
				if (!filter.filter(commit))
					commits.add(commit);
			}
			return commits;
		} catch (IOException e) {
			log.error("Unexpected error reading to commit history", e);
			return Collections.emptyList();
		}
	}

	public void appendToHistory(File file, Commit commit) {
		try (FileWriter fWriter = new FileWriter(file, true);
				BufferedWriter writer = new BufferedWriter(fWriter);
				PrintWriter out = new PrintWriter(writer)) {
			out.println(commit.toString());
		} catch (IOException e) {
			log.error("Unexpected error appending to commit history", e);
		}
	}

	interface Filter<T> {
		boolean filter(T element);
	}
}
