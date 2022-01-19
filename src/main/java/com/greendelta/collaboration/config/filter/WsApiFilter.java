package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebFilter(value = "/ws/*", asyncSupported = true)
public class WsApiFilter extends AccessFilter {

	// must be synced with major version (pom). If and only if a breaking change
	// is implemented major versions have to be increased
	private static final int SERVER_MAJOR_API_VERSION = 2;
	private static final Logger log = LogManager.getLogger(WsApiFilter.class);

	@Override
	protected boolean isAccessDenied(HttpServletRequest request) {
		var clientMajorApiVersion = getApiMajorVersion(request);
		return clientMajorApiVersion != SERVER_MAJOR_API_VERSION;
	}

	@Override
	protected void onAccessDenied(HttpServletResponse response) throws IOException {
		response.sendError(406, "Client API version does not match");
	}

	private int getApiMajorVersion(HttpServletRequest request) {
		var version = request.getHeader("lca-cs-client-api-version");
		if (version == null || version.isEmpty())
			return SERVER_MAJOR_API_VERSION;
		try {
			return Integer.parseInt(version.substring(0, version.indexOf(".")));
		} catch (Throwable e) {
			log.error("Unknown client version: " + version, e);
			return SERVER_MAJOR_API_VERSION;
		}
	}

}
