package com.greendelta.collaboration.config;

import java.io.IOException;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorConfig {

	private static final Logger log = LoggerFactory.getLogger(ErrorConfig.class);
	private final UserService userService;
	private final SettingsService settings;

	public ErrorConfig(UserService userService, SettingsService settings) {
		this.userService = userService;
		this.settings = settings;
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

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<?> handleException(ResponseStatusException e) {
		return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getReason());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handle(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
		return logInfoAndRespond(e.getStatusCode(), e.getMessage(), request);
	}

	@ExceptionHandler(HttpMediaTypeException.class)
	public ResponseEntity<?> handle(HttpMediaTypeException e, HttpServletRequest request) {
		return logInfoAndRespond(e.getStatusCode(), e.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handle(MethodArgumentNotValidException e, HttpServletRequest request) {
		return logInfoAndRespond(e.getStatusCode(), e.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handle(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
		return logInfoAndRespond(HttpStatus.BAD_REQUEST, e.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handle(Exception e, HttpServletRequest request) {
		var path = getPath(request);
		if (!expected(e)) {
			log.error("Server error [user=" + getUserInfo() + ", path=" + path + "]", e);
		} else {
			log.debug("Server error [user=" + getUserInfo() + ", path=" + path + "]", e);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occured");
	}

	private ResponseEntity<?> logInfoAndRespond(HttpStatusCode status, String message, HttpServletRequest request) {
		var path = getPath(request);
		log.info("Status " + status.value() + ": " + message + " [" + request.getMethod() + " " + path + "]");
		return ResponseEntity.status(status).contentType(MediaType.TEXT_PLAIN).body(message);
	}

	private String getPath(HttpServletRequest request) {
		var path = request.getRequestURI().substring(request.getContextPath().length());
		var query = request.getQueryString();
		if (query == null)
			return path;
		return path + "?" + query;
	}

	private boolean expected(Exception e) {
		if (e instanceof ClientAbortException)
			return true;
		if (e instanceof IOException)
			return !"Connection reset by peer".equals(e.getMessage());
		if (e instanceof AsyncRequestTimeoutException)
			return true;
		return false;
	}

	private String getUserInfo() {
		var user = userService.getCurrentUser();
		if (user.isAnonymous())
			return "anonymous";
		return "{"
				+ "id: " + user.id
				+ ", name: " + user.name
				+ ", email: " + user.email
				+ "}";
	}
}
