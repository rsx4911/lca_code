package com.greendelta.collaboration.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RepositoryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 3597855854783144681L;

	public RepositoryNotFoundException(String repositoryId) {
		super("No repository '" + repositoryId + "' found");
	}

}
