package com.greendelta.collaboration.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dirs {

	private final static Logger log = LogManager.getLogger(Dirs.class);

	public static boolean move(File from, File to) {
		boolean copied = copy(from, to);
		if (!copied)
			return false;
		return delete(from);
	}

	public static boolean copy(File from, File to) {
		if (from == null || to == null || !Files.exists(from.toPath()))
			return false;
		try {
			Files.walkFileTree(from.toPath(), new Copy(from.toPath(), to.toPath()));
			return true;
		} catch (IOException e) {
			log.error("failed to copy " + from + " to " + to, e);
			delete(to);
			return false;
		}
	}

	public static boolean delete(File dir) {
		if (dir == null || !Files.exists(dir.toPath()))
			return false;
		try {
			Files.walkFileTree(dir.toPath(), new Delete());
			return true;
		} catch (IOException e) {
			log.error("failed to delete " + dir, e);
			return false;
		}
	}

	private static class Copy extends SimpleFileVisitor<Path> {

		private final Path from;
		private final Path to;

		public Copy(Path from, Path to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path fromDir,
				BasicFileAttributes attrs) throws IOException {
			Path toDir = to.resolve(from.relativize(fromDir));
			if (!Files.exists(toDir))
				Files.createDirectory(toDir);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException {
			Files.copy(file, to.resolve(from.relativize(file)),
					StandardCopyOption.REPLACE_EXISTING);
			return FileVisitResult.CONTINUE;
		}
	}

	private static class Delete extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes atts)
				throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc)
				throws IOException {
			if (exc != null)
				throw exc;
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}
	}
}
