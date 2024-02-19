package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FileService {

	private static final Logger log = LogManager.getLogger(FileService.class);
	private final Map<String, Long> directories = new HashMap<>();

	public File createTempFile() throws IOException {
		var dir = Files.createTempDirectory("lca-cs");
		var file = new File(dir.toFile(), dir.getFileName() + ".zip");
		file.deleteOnExit();
		directories.put(file.getParentFile().getAbsolutePath(), Calendar.getInstance().getTimeInMillis());
		return file;
	}

	@Scheduled(fixedRate = 3, timeUnit = TimeUnit.HOURS)
	public void cleanupTempFiles() {
		for (var d : directories.keySet()) {
			var cal = Calendar.getInstance();
			cal.setTimeInMillis(directories.get(d));
			var before = Calendar.getInstance();
			before.add(Calendar.HOUR_OF_DAY, -3);
			if (!cal.before(before))
				continue;
			try {
				var dir = new File(d);
				if (!dir.exists())
					continue;
				var files = dir.listFiles();
				if (files != null) {
					for (var file : files) {
						if (!file.exists())
							continue;
						Files.delete(file.toPath());
					}
				}
				Files.delete(dir.toPath());
			} catch (IOException e) {
				log.error("Could not delete temp file", e);
			}
		}
	}

}
