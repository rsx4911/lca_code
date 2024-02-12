package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter
public class WsApiFilter implements Filter {

	// must be synced with major version (pom). If and only if a breaking change
	// is implemented major versions have to be increased
	private static final int SERVER_MAJOR_API_VERSION = 2;
	private static final Logger log = LogManager.getLogger(WsApiFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public final void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		var request = (HttpServletRequest) req;
		var response = (HttpServletResponse) res;
		if (getApiMajorVersion(request) != SERVER_MAJOR_API_VERSION) {
			response.sendError(406, "Client API version does not match");
		} else {
			chain.doFilter(request, response);
		}
	}

	private int getApiMajorVersion(HttpServletRequest request) {
		if (request == null)
			return SERVER_MAJOR_API_VERSION;
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
