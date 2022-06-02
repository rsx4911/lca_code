package com.greendelta.collaboration.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {

	private static final long serialVersionUID = -5922024730075076184L;

	public ServiceUnavailableException(String message) {
		super(message);
	}

}
