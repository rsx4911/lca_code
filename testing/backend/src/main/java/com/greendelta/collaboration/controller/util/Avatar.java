package com.greendelta.collaboration.controller.util;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Avatar {

	private static final Logger log = LogManager.getLogger(Avatar.class);

	public static byte[] get(Blob blob, String defaultPath) {
		if (blob != null)
			return read(blob);
		return get(defaultPath);
	}

	public static byte[] get(String defaultPath) {
		try {
			return Avatar.class.getResourceAsStream(defaultPath).readAllBytes();
		} catch (IOException e) {
			log.error("Error reading avatar from " + defaultPath);
			return null;
		}
	}

	private static byte[] read(Blob blob) {
		try {
			return blob.getBinaryStream().readAllBytes();
		} catch (SQLException | IOException e) {
			log.error("Error reading blob data", e);
			return null;
		}
	}

}