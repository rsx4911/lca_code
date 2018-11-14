package com.greendelta.collaboration.platform.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.User;
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
		boolean isMaintenanceMode = settingsService.is(Key.MAINTENANCE_MODE);
		String url = request.getRequestURL().toString();
		boolean isLoginUrl = url.endsWith("/login");
		boolean isImprintUrl = url.endsWith("/imprint");
		boolean isGraphUrl = url.endsWith("/graph/graph.html");
		boolean isMaintenanceUrl = url.endsWith("/maintenance");
		User user = userService.getCurrentUser();
		if (isMaintenanceMode && !isLoginUrl && !isMaintenanceUrl && !user.isAdmin()) {
			response.sendRedirect(request.getContextPath() + "/maintenance");
			return;
		}
		if (isGraphUrl) {
			forward("/graph/graph.html", request, response);
			return;
		}
		if (isImprintUrl) {
			forward("/imprint.html", request, response);
			return;
		}
		if (isMaintenanceUrl) {
			forward("/maintenance.html", request, response);
			return;
		}
		if (isLoginUrl && !user.hasId()) {
			forward("/login.html", request, response);
			return;
		}
		if (isLoginUrl && user.hasId()) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}
		String redirectUrl = sessionProvider.get().redirectUrl;
		sessionProvider.get().redirectUrl = null;
		if (user.hasId() && !Strings.isNullOrEmpty(redirectUrl)) {
			response.sendRedirect(redirectUrl);
			return;
		}
		File publicIndex = new File(request.getServletContext().getRealPath("index_public.html"));
		if (user.hasId() && publicIndex.exists()) {
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
