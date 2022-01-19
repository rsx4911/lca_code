package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AccessFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !isAccessDenied((HttpServletRequest) request)) {
			chain.doFilter(request, response);
			return;
		}
		onAccessDenied((HttpServletResponse) response);
	}

	protected boolean isAccessDenied(HttpServletRequest request) throws IOException, ServletException {
		return true;
	}

	protected abstract void onAccessDenied(HttpServletResponse response) throws IOException, ServletException;

	@Override
	public void destroy() {

	}

}
