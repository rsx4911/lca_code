package com.greendelta.collaboration.controller.util;

import java.io.File;

import org.openlca.util.Strings;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.greendelta.collaboration.model.Permission;

public class Response {

	public static <T> ResponseEntity<T> ok(T entity) {
		return ResponseEntity.ok(entity);
	}

	public static ResponseEntity<Resource> ok(File file) {
		return ok(file.getName(), file);
	}

	public static ResponseEntity<Resource> ok(String filename, File file) {
		return resourceBuilder(filename, new PathResource(file.toPath()));
	}

	public static ResponseEntity<Resource> ok(String filename, byte[] data) {
		return resourceBuilder(filename, new ByteArrayResource(data));
	}

	private static ResponseEntity<Resource> resourceBuilder(String name, Resource resource) {
		var contentDisposition = "attachment; filename=" + name;
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", contentDisposition)
				.body(resource);
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

	public static ResponseStatusException forbidden(String path, Permission action) {
		if (Strings.nullOrEmpty(path))
			return status(HttpStatus.FORBIDDEN, "No permission to perform '" + action.name() + "'");
		return status(HttpStatus.FORBIDDEN, "No permission to perform '" + action.name() + "' on '" + path + "'");
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

	public static ResponseStatusException status(HttpStatus status, String message) {
		return new ResponseStatusException(status, message);
	}

}
