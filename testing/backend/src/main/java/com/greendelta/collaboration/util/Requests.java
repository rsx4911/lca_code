package com.greendelta.collaboration.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.openlca.util.Strings;

import jakarta.servlet.http.HttpServletRequest;

public class Requests {

	public static String getRoute(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length() + 1);
	}
	
	public static String getLoginRedirectUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		var loginUrl = "login";
		var route = getRoute(request);
		if (!route.isEmpty() && !Routes.isPublicUrl(route) && !route.equals("search")) {
			var query = request.getQueryString();
			if (!Strings.nullOrEmpty(query)) {
				route += "?" + query;
			}
			route = URLEncoder.encode(route, StandardCharsets.UTF_8.toString());
			loginUrl += "?redirectUrl=" + route;
		}
		return request.getServletContext().getContextPath() + "/" + loginUrl;
	}

}
