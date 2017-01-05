package com.greendelta.collaboration.webservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.AuthorizationException;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger log = LoggerFactory.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable e) {
		if (e instanceof WebApplicationException)
			return ((WebApplicationException) e).getResponse();
		if (e instanceof UnsupportedSchemaException)
			return Response.status(Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
		if (e instanceof AuthorizationException)
			return Response.status(Status.FORBIDDEN).build();
		log.error("Unexpected error", e);
		return Response.status(getStatus(e)).entity(getMessage(e)).type(MediaType.APPLICATION_JSON).build();
	}

	private int getStatus(Throwable e) {
		return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}

	private String getMessage(Throwable e) {
		if (e instanceof WebApplicationException)
			return e.getMessage();
		return "Server error, please contact your admin";
	}

}
