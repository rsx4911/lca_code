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
	private final Map<String, Long> files = new HashMap<>();

	public File createTempFile() throws IOException {
		var file = Files.createTempFile("lca-cs", ".zip");
		files.put(file.toFile().getAbsolutePath(), Calendar.getInstance().getTimeInMillis());
		return file.toFile();
	}

	@Scheduled(fixedRate = 3, timeUnit = TimeUnit.HOURS)
	public void cleanupTempFiles() {
		for (var file : files.keySet()) {
			var cal = Calendar.getInstance();
			cal.setTimeInMillis(files.get(file));
			var before = Calendar.getInstance();
			before.add(Calendar.HOUR_OF_DAY, -3);
			if (cal.before(before)) {
				try {
					Files.delete(new File(file).toPath());
				} catch (IOException e) {
					log.error("Could not delete temp file", e);
				}
			}
		}
	}

}
