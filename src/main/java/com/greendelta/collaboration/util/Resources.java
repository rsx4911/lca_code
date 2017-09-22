package com.greendelta.collaboration.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.logging.log4j.core.util.IOUtils;

public class Resources {

	public static String get(Class<?> clazz, String name) {
		InputStream stream = clazz.getResourceAsStream(name);
		if (stream == null)
			return null;
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(new InputStreamReader(stream), writer);
		} catch (IOException e) {
			return null;
		}
		return writer.toString();
	}

	
}
