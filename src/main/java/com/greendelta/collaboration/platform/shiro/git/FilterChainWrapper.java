package com.greendelta.collaboration.platform.shiro.git;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		chain.doFilter(this.request, this.response);
	}

}