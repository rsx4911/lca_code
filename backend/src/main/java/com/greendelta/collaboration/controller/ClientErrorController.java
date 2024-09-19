package com.greendelta.collaboration.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/public/error")
public class ClientErrorController {

	private final Logger log = LogManager.getLogger(ClientErrorController.class);
	private final UserService userService;

	public ClientErrorController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public void handleClientError(@RequestBody Map<String, String> info) {
		log.error("Client error [user=" + getUserInfo() + ", path=" + info.get("path") + "]",
				createException(info.get("stacktrace")));
	}

	private String getUserInfo() {
		var user = userService.getCurrentUser();
		if (user.isAnonymous())
			return "anonymous";
		var info = "{";
		info += "id: " + user.id;
		info += ", name: " + user.username;
		info += ", email: " + user.email;
		info += "}";
		return info;
	}

	private Throwable createException(String error) {
		if (error == null) {
			error = "";
		}
		var parts = error.contains("\t")
				? error.split("\tat ")
				: error.split(" at ");
		var t = new Throwable(parts[0].trim());
		var stackTrace = new StackTraceElement[parts.length - 1];
		for (var i = 1; i < parts.length; i++) {
			var clazz = "";
			var method = "";
			var part = parts[i].trim();
			String ref = null;
			if (part.contains("(")) {
				var clazzAndMethod = part.substring(0, part.indexOf("(")).trim();
				if (clazzAndMethod.contains(".")) {
					clazz = clazzAndMethod.substring(0, clazzAndMethod.lastIndexOf(".")).trim();
					method = clazzAndMethod.substring(clazzAndMethod.lastIndexOf(".") + 1).trim();
				} else {
					method = clazzAndMethod;
				}
				ref = part.substring(part.indexOf("(") + 1).trim();
			} else {
				ref = part.trim();
			}
			if (ref.indexOf(':') != -1) {
				ref = ref.substring(0, ref.lastIndexOf(":"));
			}
			var index = ref.lastIndexOf(":");
			var line = 0;
			String file = null;
			if (index != -1) {
				file = ref.substring(0, index);
				String linePart = ref.substring(index + 1);
				line = Integer.parseInt(linePart);
			}
			stackTrace[i - 1] = new StackTraceElement(clazz, method, file, line);
		}
		t.setStackTrace(stackTrace);
		return t;
	}

}
