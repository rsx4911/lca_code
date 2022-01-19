package com.greendelta.collaboration.config.filter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Routes;

@WebFilter(asyncSupported = true)
public class SinglePageFilter implements Filter {

	// custom gulp build can change publicly available "html" resources
	public static final List<String> CUSTOM_PUBLIC_RESOURCES = Arrays.asList("/header", "/imprint", "/index", "/index", "/login", "/test");
	private final UserService userService;
	private final SettingsService settingsService;
	private final GitFilterConfig gitFilterConfig;
	
	@Autowired
	public SinglePageFilter(UserService userService, SettingsService settingsService, GitFilterConfig gitFilterConfig) {
		this.userService = userService;
		this.settingsService = settingsService;
		this.gitFilterConfig = gitFilterConfig;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		var request = (HttpServletRequest) req;
		var response = (HttpServletResponse) resp;
		if (!doApply(request)) {
			chain.doFilter(request, response);
			return;
		}
		var url = request.getRequestURI();
		var isMaintenanceMode = settingsService.is(ServerSetting.MAINTENANCE_MODE);
		var isLoginUrl = url.equals("/login") || url.equals("/reset-password") || url.equals("/sign-up");
		var isJobUrl = url.equals("/job");
		var isMaintenanceUrl = url.equals("/maintenance");
		var user = userService.getCurrentUser();
		if (isMaintenanceMode && !isLoginUrl && !isMaintenanceUrl && !user.isAdmin()) {
			redirect(request.getContextPath() + "/maintenance", response);
			return;
		}
		if (isJobUrl) {
			forward("/job.html", request, response);
			return;
		}
		var publicRepositoriesEnabled = settingsService.is(ServerSetting.PUBLIC_REPOSITORY_ENABLED);
		var userRegistrationEnabled = settingsService.is(ServerSetting.USER_REGISTRATION_ENABLED);
		if (user.id == 0 && (isLoginUrl || !publicRepositoriesEnabled)) {
			if ((!publicRepositoriesEnabled && !isLoginUrl) || (!userRegistrationEnabled && url.equals("/sign-up"))) {
				redirect("/login", response);
				return;
			}
			forward("/login.html", request, response);
			return;
		}
		if ((isLoginUrl && user.id != 0) || (isMaintenanceUrl && !isMaintenanceMode)) {
			redirect(request.getContextPath() + "/", response);
			return;
		}
		var route = url.substring(url.lastIndexOf('/'));
		if (CUSTOM_PUBLIC_RESOURCES.contains(route) || route.equals("/maintenance") || route.equals("/imprint")) {
			forward(route + ".html", request, response);
			return;
		}
		// String redirectUrl = sessionProvider.get().redirectUrl;
		// sessionProvider.get().redirectUrl = null;
		// if (user.id != 0 && !Strings.nullOrEmpty(redirectUrl)) {
		// redirect(redirectUrl, response);
		// return;
		// }
		var publicIndex = request.getServletContext().getRealPath("index_public.html");
		if (user.id == 0 && publicIndex != null && new File(publicIndex).exists()) {
			forward("/index_public.html", request, response);
		} else {
			forward("/index.html", request, response);
		}
	}

	private boolean doApply(HttpServletRequest request) throws IOException, ServletException {
		var url = request.getRequestURI();
		if (url.startsWith("/ws/"))
			return false;
		url = url.substring(1);
		if (!url.contains("/"))
			return true;
		if (Routes.isPublicResource(url.substring(0, url.indexOf('/'))))
			return false;
		return !gitFilterConfig.isGitUrl(request);
	}

	private void forward(String path, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.getRequestDispatcher(path).forward(request, response);
	}

	private void redirect(String path, HttpServletResponse response) throws IOException {
		response.sendRedirect(path);
	}

}
