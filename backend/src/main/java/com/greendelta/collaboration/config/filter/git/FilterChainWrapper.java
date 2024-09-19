package com.greendelta.collaboration.config.filter.git;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

class FilterChainWrapper implements FilterChain {

	private final ServletRequest request;
	private final ServletResponse response;
	private final FilterChain chain;

	FilterChainWrapper(ServletRequest request, ServletResponse response, FilterChain chain) {
		this.request = request;
		this.response = response;
		this.chain = chain;
	}

	@Override
	public final void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		chain.doFilter(this.request, this.response);
	}

}