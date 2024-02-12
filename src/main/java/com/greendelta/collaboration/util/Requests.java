package com.greendelta.collaboration.util;

import jakarta.servlet.http.HttpServletRequest;

public class Requests {

	public static String getRoute(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length() + 1);
	}

}
