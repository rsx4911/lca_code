package com.greendelta.collaboration.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorConfig {

	private final SettingsService settings;

	public ErrorConfig(SettingsService settings) {
		this.settings = settings;
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<?> handleException(ResponseStatusException e) {
		return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getReason());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public Object handle(NoResourceFoundException e, HttpServletRequest request) {
		var path = e.getResourcePath();
		if (path.startsWith("ws/") || e.getHttpMethod() != HttpMethod.GET)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No service " + path + " found");
		return switch (path) {
			case "login", "sign-up", "reset-password" -> "forward:/login.html";
			case "imprint" -> "forward:/imprint.html";
			case "job" -> "forward:/job.html";
			case "maintenance" -> settings.is(ServerSetting.MAINTENANCE_MODE)
					? "forward:/maintenance.html"
					: "redirect:/";
			default -> "forward:/";
		};
	}

}
