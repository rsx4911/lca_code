package com.greendelta.cloud.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class Respond {

	public static Response ok() {
		return status(Status.OK);
	}

	public static Response ok(Object entity) {
		return status(Status.OK, entity);
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
		return Response.status(status).entity(entity).build();
	}

	public static Response invalid(String field, String message) {
		Map<String, String> error = new HashMap<>();
		error.put("field", field);
		error.put("message", message);
		return badRequest(error);
	}
	
}
