package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

@WebFilter(value = "/ws/*", asyncSupported = true)
public class WsMaintenanceFilter extends AccessFilter {

	private final SettingsService settingsService;
	private final UserService userService;

	@Autowired
	public WsMaintenanceFilter(SettingsService settingsService, UserService userService) {
		this.settingsService = settingsService;
		this.userService = userService;
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