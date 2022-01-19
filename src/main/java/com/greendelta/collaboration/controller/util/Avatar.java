package com.greendelta.collaboration.controller.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Avatar {

	private static final Logger log = LogManager.getLogger(Avatar.class);

	public static byte[] get(byte[] bytes, String defaultPath) {
		if (bytes != null)
			return bytes;
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
}
