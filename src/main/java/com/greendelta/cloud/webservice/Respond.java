package com.greendelta.cloud.webservice;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

class Respond {

	static Response ok() {
		return status(Status.OK);
	}

	static Response ok(Object entity) {
		return status(Status.OK, entity);
	}

	static Response conflict() {
		return status(Status.CONFLICT);
	}

	static Response conflict(Object entity) {
		return status(Status.CONFLICT, entity);
	}

	static Response notFound() {
		return status(Status.NOT_FOUND);
	}

	static Response notFound(Object entity) {
		return status(Status.NOT_FOUND, entity);
	}

	static Response unauthorized() {
		return status(Status.UNAUTHORIZED);
	}

	static Response unauthorized(Object entity) {
		return status(Status.UNAUTHORIZED, entity);
	}

	static Response created() {
		return status(Status.CREATED);
	}

	static Response created(Object entity) {
		return status(Status.CREATED, entity);
	}

	static Response noContent() {
		return status(Status.NO_CONTENT);
	}

	static Response noContent(Object entity) {
		return status(Status.NO_CONTENT, entity);
	}

	static Response forbidden() {
		return status(Status.FORBIDDEN);
	}

	static Response forbidden(Object entity) {
		return status(Status.FORBIDDEN, entity);
	}

	private static Response status(Status status) {
		return Response.status(status).build();
	}

	private static Response status(Status status, Object entity) {
		return Response.status(status).entity(entity).build();
	}

}
