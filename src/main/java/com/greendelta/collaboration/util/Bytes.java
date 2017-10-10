package com.greendelta.collaboration.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

public class Bytes {

	private static final Logger log = LoggerFactory.getLogger(Bytes.class);

	public static byte[] readStream(InputStream file) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteStreams.copy(file, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			log.error("Error reading data from stream", e);
			return null;
		}
	}

	public static void write(File file, byte[] data) {
		try {
			file.mkdirs();
			if (data == null || data.length == 0)
				file.createNewFile();
			else
				Files.write(file.toPath(), data);
		} catch (IOException e) {
			log.error("Error writing data to file " + file.getAbsolutePath(), e);
		}
	}

	public static byte[] read(File file) {
		if (file == null || !file.exists())
			return null;
		if (file.length() == 0)
			return new byte[0];
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			String path = file.getAbsolutePath();
			String message = "Error reading data from file " + path;
			log.error(message, e);
			return null;
		}
	}

	public static void appendTo(File file, String data) {
		try (FileWriter fWriter = new FileWriter(file, true);
				BufferedWriter writer = new BufferedWriter(fWriter);
				PrintWriter out = new PrintWriter(writer)) {
			out.println(data);
		} catch (IOException e) {
			log.error("Unexpected error appending data to file " + file.getAbsolutePath(), e);
		}
	}

}
