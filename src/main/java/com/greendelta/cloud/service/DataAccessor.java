package com.greendelta.cloud.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openlca.cloud.model.data.CommitDescriptor;
import org.openlca.cloud.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DataAccessor {

	private final static Charset charset = Charset.forName("utf-8");
	private final static Logger log = LoggerFactory
			.getLogger(DataAccessor.class);

	public void writeDataset(File file, String data) {
		try {
			if (data == null)
				file.createNewFile();
			else
				Files.write(file.toPath(), data.getBytes(charset));
		} catch (IOException e) {
			log.error(
					Strings.concat("Error writing json data to file ",
							file.getAbsolutePath()), e);
		}
	}

	public String readDataset(File file) {
		if (file == null)
			return null;
		if (!file.exists())
			return null;
		if (file.length() == 0)
			return "";
		try {
			byte[] jsonData = Files.readAllBytes(file.toPath());
			return new String(jsonData, charset);
		} catch (IOException e) {
			log.error(
					Strings.concat("Error reading json data from file ",
							file.getAbsolutePath()), e);
			return null;
		}
	}

	public List<CommitDescriptor> readHistory(File file,
			Filter<CommitDescriptor> filter) {
		if (file == null)
			return Collections.emptyList();
		if (!file.exists())
			return Collections.emptyList();
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			if (lines.isEmpty())
				return Collections.emptyList();
			List<CommitDescriptor> descriptors = new ArrayList<>();
			for (String entry : lines) {
				if (entry.trim().isEmpty())
					continue;
				CommitDescriptor descriptor = CommitDescriptor.parse(entry);
				if (!filter.filter(descriptor))
					descriptors.add(descriptor);
			}
			return descriptors;

		} catch (IOException e) {
			log.error("Unexpected error appending to commit history", e);
			return Collections.emptyList();
		}
	}

	public void appendToHistory(File file, CommitDescriptor commit) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(file, true)))) {
			out.println(commit.toString());
		} catch (IOException e) {
			log.error("Unexpected error appending to commit history", e);
		}
	}

	interface Filter<T> {
		boolean filter(T element);
	}
}
