package com.greendelta.collaboration.platform.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.platform.guice.ShiroModule;
import com.greendelta.collaboration.platform.guice.util.CloudSession;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;

@Singleton
public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -7021790186597193927L;
	private final Provider<CloudSession> sessionProvider;
	private final UserService userService;
	private final SettingsService settingsService;

	@Inject
	public DefaultServlet(Provider<CloudSession> sessionProvider, UserService userService,
			SettingsService settingsService) {
		this.sessionProvider = sessionProvider;
		this.userService = userService;
		this.settingsService = settingsService;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean isMaintenanceMode = settingsService.is(ServerSetting.MAINTENANCE_MODE);
		String url = request.getRequestURL().toString();
		boolean isLoginUrl = url.endsWith("/login") || url.endsWith("/reset-password") || url.endsWith("/sign-up");
		boolean isJobUrl = url.endsWith("/job");
		boolean isMaintenanceUrl = url.endsWith("/maintenance");
		User user = userService.getCurrentUser();
		if (isMaintenanceMode && !isLoginUrl && !isMaintenanceUrl && !user.isAdmin()) {
			response.sendRedirect(request.getContextPath() + "/maintenance");
			return;
		}
		if (isJobUrl) {
			forward("/job.html", request, response);
			return;
		}
		if (user.id == 0 && (isLoginUrl || !settingsService.is(ServerSetting.PUBLIC_REPOSITORY_ENABLED))) {
			if ((!settingsService.is(ServerSetting.PUBLIC_REPOSITORY_ENABLED) && !isLoginUrl)
					|| (!settingsService.is(ServerSetting.USER_REGISTRATION_ENABLED) && url.endsWith("/sign-up"))) {
				response.sendRedirect("/login");
				return;
			}
			forward("/login.html", request, response);
			return;
		}
		if ((isLoginUrl && user.id != 0) || (isMaintenanceUrl && !isMaintenanceMode)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}
		String route = url.substring(url.lastIndexOf('/'));
		if (Arrays.asList(ShiroModule.CUSTOM_PUBLIC_RESOURCES).contains(route) || route.equals("/maintenance")
				|| route.equals("/imprint")) {
			forward(route + ".html", request, response);
			return;
		}
		String redirectUrl = sessionProvider.get().redirectUrl;
		sessionProvider.get().redirectUrl = null;
		if (user.id != 0 && !Strings.isNullOrEmpty(redirectUrl)) {
			response.sendRedirect(redirectUrl);
			return;
		}
		String publicIndex = request.getServletContext().getRealPath("index_public.html");
		if (user.id == 0 && publicIndex != null && new File(publicIndex).exists()) {
			forward("/index_public.html", request, response);
		} else {
			forward("/index.html", request, response);
		}
	}

	private void forward(String path, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher(path).forward(request, response);
	}

}
