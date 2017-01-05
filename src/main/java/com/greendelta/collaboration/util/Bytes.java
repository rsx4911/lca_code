package com.greendelta.collaboration.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;

import com.google.common.io.ByteStreams;

public class Bytes {

	public static byte[] readStream(InputStream file) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteStreams.copy(file, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}

}
