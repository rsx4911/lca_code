package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

@WebFilter(value = "/ws/*", asyncSupported = true)
@Component
public class WsMaintenanceFilter extends AccessFilter {

	private SettingsService settingsService;
	private UserService userService;

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (settingsService != null)
			return;
		var app = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		userService = app.getBean(UserService.class);
		settingsService = app.getBean(SettingsService.class);
	}

	@Override
	protected boolean isAccessDenied(HttpServletRequest request) {
		var maintenanceMode = settingsService.is(ServerSetting.MAINTENANCE_MODE);
		if (!maintenanceMode)
			return false;
		var url = request.getRequestURL().toString();
		var isLoginUrl = url.endsWith("/ws/public/login");
		var user = userService.getCurrentUser();
		if (isLoginUrl || user.isAdmin())
			return false;
		return true;
	}

	@Override
	protected void onAccessDenied(HttpServletResponse response) throws IOException {
		response.setStatus(406);
		var message = settingsService.get(ServerSetting.MAINTENANCE_MESSAGE);
		response.getWriter().print(message);
	}

}