package com.greendelta.collaboration.controller.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.openlca.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.error.WebRequestException;

public class Response {

	public static <T> ResponseEntity<T> ok(T entity) {
		return ResponseEntity.ok(entity);
	}

	public static ResponseEntity<StreamingResponseBody> ok(String filename, File file) {
		return ok(filename, file, null);
	}

	public static ResponseEntity<StreamingResponseBody> ok(String filename, File file, Runnable callback) {
		if (!file.exists())
			throw Response.notFound();
		var filesize = 0l;
		try {
			filesize = Files.size(file.toPath());
		} catch (IOException e) {
			// ignore, not relevant
		}
		return ok(filename, filesize, output -> {
			Files.copy(file.toPath(), output);
			if (callback != null) {
				callback.run();
			}
		});
	}

	public static ResponseEntity<byte[]> ok(String filename, byte[] data) {
		var builder = ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM);
		if (!Strings.nullOrEmpty(filename)) {
			builder = builder.header("Content-Disposition", "attachment; filename=" + filename);
		}
		if (data.length > 0) {
			builder = builder.header("Content-Length", Long.toString(data.length));
		}
		return builder.body(data);
	}

	public static ResponseEntity<StreamingResponseBody> ok(String filename, long filesize,
			StreamingResponseBody stream) {
		var builder = ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM);
		if (!Strings.nullOrEmpty(filename)) {
			builder = builder.header("Content-Disposition", "attachment; filename=" + filename);
		}
		if (filesize > 0) {
			builder = builder.header("Content-Length", Long.toString(filesize));
		}
		return builder.body(stream);
	}

	public static <T> ResponseEntity<T> noContent() {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	public static <T> ResponseEntity<T> created() {
		return created(null);
	}

	public static <T> ResponseEntity<T> created(T entity) {
		return ResponseEntity.status(HttpStatus.CREATED).body(entity);
	}

	public static ResponseStatusException badRequest() {
		return status(HttpStatus.BAD_REQUEST);
	}

	public static ResponseStatusException badRequest(String message) {
		return status(HttpStatus.BAD_REQUEST, message);
	}

	public static ResponseStatusException conflict() {
		return status(HttpStatus.CONFLICT);
	}

	public static ResponseStatusException conflict(String message) {
		return status(HttpStatus.CONFLICT, message);
	}

	public static ResponseStatusException notFound() {
		return status(HttpStatus.NOT_FOUND);
	}

	public static ResponseStatusException notFound(String message) {
		return status(HttpStatus.NOT_FOUND, message);
	}

	public static ResponseStatusException unauthorized() {
		return status(HttpStatus.UNAUTHORIZED);
	}

	public static ResponseStatusException unauthorized(String message) {
		return status(HttpStatus.UNAUTHORIZED, message);
	}

	public static ResponseStatusException unavailable() {
		return status(HttpStatus.SERVICE_UNAVAILABLE);
	}

	public static ResponseStatusException unavailable(String message) {
		return status(HttpStatus.SERVICE_UNAVAILABLE, message);
	}

	public static ResponseStatusException forbidden() {
		return status(HttpStatus.FORBIDDEN);
	}

	public static ResponseStatusException forbidden(String message) {
		return status(HttpStatus.FORBIDDEN, message);
	}

	public static ResponseStatusException error() {
		return status(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static ResponseStatusException error(String message) {
		return status(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}

	private static ResponseStatusException status(HttpStatus status) {
		return status(status, null);
	}

	public static ResponseStatusException badRequest(String field, String message) {
		if (field == null)
			return badRequest(message);
		return badRequest(toData(field, message));
	}

	private static String toData(String field, String message) {
		return """
				{"field": "%s", "message": "%s"}
				""".formatted(field, message);
	}

	public static ResponseStatusException status(WebRequestException e) {
		return status(e.status, e.message);
	}

	public static ResponseStatusException status(HttpStatus status, String message) {
		return new ResponseStatusException(status, message);
	}

}
