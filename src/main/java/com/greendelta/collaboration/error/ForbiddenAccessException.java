package com.greendelta.collaboration.error;

import org.openlca.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenAccessException extends RuntimeException {

	private static final long serialVersionUID = -5922024730075076184L;

	public ForbiddenAccessException(String path, String action) {
		super("No permission to perform '" + action + "'" + (Strings.nullOrEmpty(path) ? "" : " on '" + path + "'"));
	}

}
