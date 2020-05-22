package com.greendelta.collaboration.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.function.Function;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.google.gson.Gson;

public class JsonIteratorStreamingOutput<T> implements StreamingOutput {

	private final Iterator<T> iterator;
	private final Function<T, Object> converter;

	public JsonIteratorStreamingOutput(Iterator<T> iterator) {
		this.iterator = iterator;
		this.converter = null;
	}

	public JsonIteratorStreamingOutput(Iterator<T> iterator, Function<T, Object> converter) {
		this.iterator = iterator;
		this.converter = converter;
	}

	@Override
	public void write(OutputStream output) throws IOException, WebApplicationException {
		Gson gson = new Gson();
		try (BufferedOutputStream stream = new BufferedOutputStream(output)) {
			stream.write("[".getBytes("utf-8"));
			boolean first = true;
			while (iterator.hasNext()) {
				if (!first) {
					stream.write(",".getBytes("utf-8"));
				}
				first = false;
				Object next = converter != null ? converter.apply(iterator.next()) : iterator.next();
				String json = gson.toJson(next);
				stream.write(json.getBytes("utf-8"));
			}
			stream.write("]".getBytes("utf-8"));
		}
	}

}
