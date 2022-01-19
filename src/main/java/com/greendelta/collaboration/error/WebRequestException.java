package com.greendelta.collaboration.error;

import org.springframework.http.HttpStatus;

public class WebRequestException extends Exception {

	private static final long serialVersionUID = 8442122095563213053L;
	public final HttpStatus status;
	public final String message;

	public WebRequestException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}