package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class RepositoryInput {

	private final Path repoPath;

	RepositoryInput(Path repoPath) {
		this.repoPath = repoPath;
	}

	public void read(InputStream input) throws IOException {
		ZipInputStream in = new ZipInputStream(input);
		ZipEntry entry = null;
		while ((entry = in.getNextEntry()) != null) {
			String filename = entry.getName();
			Path path = repoPath.resolve(filename);
			File file = path.toFile();
			if (entry.isDirectory()) {
				file.mkdirs();
				continue;
			}	
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
		in.close();
	}

}
