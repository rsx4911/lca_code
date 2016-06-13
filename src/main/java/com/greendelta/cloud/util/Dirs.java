package com.greendelta.cloud.util;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

public class Dirs {

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
}
