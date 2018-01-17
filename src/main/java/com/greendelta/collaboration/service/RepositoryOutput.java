package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

class RepositoryOutput implements StreamingOutput {

	private final Path repoPath;

	RepositoryOutput(Path repoPath) {
		this.repoPath = repoPath;
	}

	@Override
	public void write(OutputStream output) throws IOException, WebApplicationException {
		ZipOutputStream out = new ZipOutputStream(output);
		write(repoPath.toFile(), out);
		out.close();
	}

	private void write(File file, ZipOutputStream out) throws IOException {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				write(child, out);
			}
			return;
		}
		if (!file.isFile())
			return;
		Path path = file.toPath();
		String entry = repoPath.relativize(path).toString().replace('\\', '/');
		out.putNextEntry(new ZipEntry(entry));
		Files.copy(path, out);
		out.closeEntry();
	}
}
