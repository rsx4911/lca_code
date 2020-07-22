package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.io.Resources;

public class Respond {

	private static final Logger log = LogManager.getLogger(Respond.class);

	public static Response ok() {
		return status(Status.OK);
	}

	public static Response ok(Object entity) {
		return status(Status.OK, entity);
	}

	public static Response ok(byte[] bytes, String defaultPath) {
		if (bytes != null)
			return Respond.ok(bytes);
		try {
			bytes = Resources.toByteArray(Respond.class.getResource(defaultPath));
		} catch (IOException e) {
			log.error("Error loading default value", e);
		}
		return Respond.ok(bytes);
	}

	public static Response ok(String filename, File file) {
		return ok(filename, file, null);
	}

	public static Response ok(String filename, File file, Runnable callback) {
		if (!file.exists())
			return Respond.notFound();
		long filesize = 0;
		try {
			filesize = Files.size(file.toPath());
		} catch (IOException e) {
			// ignore, not relevant
		}
		return ok(filename, filesize, new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				Files.copy(file.toPath(), output);
				if (callback != null)
					callback.run();
			}
		});
	}

	public static Response ok(String filename, long filesize, StreamingOutput stream) {
		ResponseBuilder builder = Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		if (!Strings.isNullOrEmpty(filename))
			builder.header("Content-Disposition", "attachment; filename=" + filename);
		if (filesize > 0)
			builder.header("Content-Length", filesize);
		return builder.build();
	}

	public static Response badRequest() {
		return status(Status.BAD_REQUEST);
	}

	public static Response badRequest(Object entity) {
		return status(Status.BAD_REQUEST, entity);
	}

	public static Response conflict() {
		return status(Status.CONFLICT);
	}

	public static Response conflict(Object entity) {
		return status(Status.CONFLICT, entity);
	}

	public static Response notFound() {
		return status(Status.NOT_FOUND);
	}

	public static Response notFound(Object entity) {
		return status(Status.NOT_FOUND, entity);
	}

	public static Response unauthorized() {
		return status(Status.UNAUTHORIZED);
	}

	public static Response unauthorized(Object entity) {
		return status(Status.UNAUTHORIZED, entity);
	}

	public static Response created() {
		return status(Status.CREATED);
	}

	public static Response created(Object entity) {
		return status(Status.CREATED, entity);
	}

	public static Response noContent() {
		return status(Status.NO_CONTENT);
	}

	public static Response noContent(Object entity) {
		return status(Status.NO_CONTENT, entity);
	}

	public static Response forbidden() {
		return status(Status.FORBIDDEN);
	}

	public static Response forbidden(Object entity) {
		return status(Status.FORBIDDEN, entity);
	}

	public static Response status(Status status) {
		return Response.status(status).build();
	}

	public static Response status(Status status, Object entity) {
		return status(status, entity, null);
	}

	public static Response status(Status status, Object entity, CacheControl cacheControl) {
		ResponseBuilder builder = Response.status(status).entity(entity);
		if (cacheControl != null) {
			builder.cacheControl(cacheControl);
		}
		return builder.build();
	}

	public static Response invalid(String field, String message) {
		return badRequest(toData(field, message));
	}

	public static Response conflict(String field, String message) {
		return conflict(toData(field, message));
	}

	private static Map<String, Object> toData(String field, String message) {
		Map<String, Object> error = new HashMap<>();
		error.put("field", field);
		error.put("message", message);
		return error;
	}

	public static Response error(String message) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).build();
	}

}
