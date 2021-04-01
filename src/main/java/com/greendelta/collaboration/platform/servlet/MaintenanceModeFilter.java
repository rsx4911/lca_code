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

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

public class MaintenanceModeFilter implements Filter {

	private final SettingsService settingsService;
	private final UserService userService;

	@Inject
	public MaintenanceModeFilter(SettingsService settingsService, UserService userService) {
		this.settingsService = settingsService;
		this.userService = userService;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		if (!(servletRequest instanceof HttpServletRequest)) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		boolean maintenanceMode = settingsService.is(ServerSetting.MAINTENANCE_MODE);
		if (maintenanceMode) {
			String url = request.getRequestURL().toString();
			boolean isLoginUrl = url.endsWith("/ws/public/login");
			User user = userService.getCurrentUser();
			if (!isLoginUrl && !user.isAdmin()) {
				HttpServletResponse response = (HttpServletResponse) servletResponse;
				response.setStatus(406);
				String message = settingsService.get(ServerSetting.MAINTENANCE_MESSAGE);
				response.getWriter().print(message);
				return;
			}
		}
		chain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {

	}

}