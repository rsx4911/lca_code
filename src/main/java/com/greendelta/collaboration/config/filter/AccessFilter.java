package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
