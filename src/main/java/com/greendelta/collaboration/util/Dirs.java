package com.greendelta.collaboration.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class Dirs {

	private static final Logger log = LoggerFactory.getLogger(Dirs.class);

	/**
	 * Copies all files from the directory specified by the first argument to
	 * the directory specified by the second argument.
	 * 
	 * @param fromDir
	 * @param toDir
	 * @return true if moving the content was successful
	 * @throws IOException
	 */
	public static boolean moveContents(File fromDir, File toDir, boolean overwriteExistingFiles) throws IOException {
		if (!fromDir.exists() || !fromDir.isDirectory())
			return false;
		if (!toDir.exists() || !toDir.isDirectory())
			return false;
		if (fromDir.listFiles() == null)
			return false;
		for (File file : fromDir.listFiles()) {
			if (file.isDirectory()) {
				File subDir = new File(toDir, file.getName());
				if (!subDir.exists())
					subDir.mkdir();
				moveContents(file, subDir, overwriteExistingFiles);
			} else {
				File target = new File(toDir, file.getName());
				if (target.exists()) {
					if (!overwriteExistingFiles)
						continue;
					target.delete();
				}
				Files.move(file, target);
			}
		}
		return true;
	}

	public static long getSize(Path path) {
		AtomicLong size = new AtomicLong(0);
		try {
			java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					size.addAndGet(attrs.size());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			log.error("Error getting size of directory", e);
		}
		return size.get();
	}

}
