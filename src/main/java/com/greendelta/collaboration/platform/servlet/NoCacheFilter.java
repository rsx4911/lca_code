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

public class NoCacheFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (request instanceof HttpServletRequest)
			process((HttpServletRequest) request, (HttpServletResponse) response);
		chain.doFilter(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Expires", "");
		response.setHeader("Last-Modified", "");
		response.setHeader("Pragma", "");
		response.setHeader("Cache-Control", "no-cache");
	}

	@Override
	public void destroy() {

	}

}
