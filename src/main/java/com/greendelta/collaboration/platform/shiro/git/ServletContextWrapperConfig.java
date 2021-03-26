package com.greendelta.collaboration.platform.shiro.git;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

class ServletContextWrapperConfig implements FilterConfig {

	private final ServletContext context;

	ServletContextWrapperConfig(ServletContext context) {
		this.context = context;
	}

	@Override
	public String getFilterName() {
		return GitFilter.class.getName();
	}

	@Override
	public String getInitParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

}