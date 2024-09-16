package com.greendelta.collaboration.config.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Requests;
import com.greendelta.collaboration.util.Routes;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter
@Component
public class MaintenanceFilter implements Filter {

	private static final List<String> allowedUrls = Arrays.asList(
			"maintenance", "login", "imprint", "ws/public/login", "ws/public/imprint");
	private SettingsService settings;
	private UserService userService;

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (settings != null)
			return;
		var app = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		userService = app.getBean(UserService.class);
		settings = app.getBean(SettingsService.class);
	}

	@Override
	public final void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		var request = (HttpServletRequest) req;
		var response = (HttpServletResponse) res;
		var maintenanceMode = settings.is(ServerSetting.MAINTENANCE_MODE);
		var route = Requests.getRoute(request);
		var skipFilter = !maintenanceMode
				|| userService.getCurrentUser().isAdmin()
				|| allowedUrls.contains(route)
				|| Routes.isPublicResource(route);
		if (skipFilter) {
			chain.doFilter(request, response);
		} else if (!route.startsWith("/ws/")) {
			response.sendRedirect(request.getContextPath() + "/maintenance");
		} else {
			response.setStatus(406);
			var message = settings.get(ServerSetting.MAINTENANCE_MESSAGE);
			response.getWriter().print(message);
		}
	}

}