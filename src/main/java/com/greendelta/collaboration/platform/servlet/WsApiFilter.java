package com.greendelta.collaboration.platform.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WsApiFilter implements Filter {

	// must be synced with major version (pom). If and only if a breaking change is
	// implemented major versions have to be increased
	private static final int SERVER_MAJOR_API_VERSION = 1;
	private static final Logger log = LogManager.getLogger(WsApiFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}
		int clientMajorApiVersion = getApiMajorVersion((HttpServletRequest) request);
		if (clientMajorApiVersion == SERVER_MAJOR_API_VERSION) {
			chain.doFilter(request, response);
			return;
		}
		response.reset();
		((HttpServletResponse) response).setStatus(406);
	}

	private int getApiMajorVersion(HttpServletRequest request) {
		String version = request.getHeader("lca-cs-client-api-version");
		if (version == null || version.isEmpty())
			return SERVER_MAJOR_API_VERSION;
		try {
			return Integer.parseInt(version.substring(0, version.indexOf(".")));
		} catch (Throwable e) {
			log.error("Unknown client version: " + version, e);
			return SERVER_MAJOR_API_VERSION;
		}
	}

	@Override
	public void destroy() {

	}

}
