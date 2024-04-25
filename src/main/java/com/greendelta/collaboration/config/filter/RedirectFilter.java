package com.greendelta.collaboration.config.filter;

import java.io.File;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.server.ResponseStatusException;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SessionService;
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
public class RedirectFilter implements Filter {

	private SettingsService settings;
	private UserService userService;
	private SessionService sessionService;
	private GitFilterConfig gitFilterConfig;

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (settings != null)
			return;
		var app = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		settings = app.getBean(SettingsService.class);
		userService = app.getBean(UserService.class);
		sessionService = app.getBean(SessionService.class);
		gitFilterConfig = app.getBean(GitFilterConfig.class);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		var request = (HttpServletRequest) req;
		var response = (HttpServletResponse) res;
		var route = Requests.getRoute(request);
		if (route.startsWith("ws/") || Routes.isPublicResource(route) || gitFilterConfig.isGitUrl(request)) {
			chain.doFilter(request, response);
		} else {
			try {
				var user = userService.getCurrentUser();
				var publicIndex = request.getServletContext().getRealPath("index_public.html");
				var releasesEnabled = settings.is(ServerSetting.RELEASES_ENABLED);
				var homepageEnabled = settings.is(ServerSetting.HOMEPAGE_ENABLED);
				var searchEnabled = settings.searchConfig.isSearchAvailable();
				var signUpEnabled = settings.is(ServerSetting.USER_REGISTRATION_ENABLED);
				var redirectToLogin = (route.equals("") && !homepageEnabled)
						|| (route.equals("search") && !searchEnabled)
						|| (route.equals("sign-up") && !signUpEnabled)
						|| !releasesEnabled;
				if (Routes.isLoginUrl(route) && !user.isAnonymous()) {
					response.sendRedirect(request.getContextPath() + "/");
				} else if (user.isAnonymous() && redirectToLogin && !route.equals("login")) {
					response.sendRedirect(request.getContextPath() + "/login");
				} else if (user.isAnonymous() && !Routes.isPublicUrl(route) && new File(publicIndex).exists()) {
					request.getRequestDispatcher("/index_public.html").forward(request, response);
				} else {
					chain.doFilter(request, response);
				}
			} catch (ResponseStatusException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					sessionService.logout(request);
					response.sendRedirect(request.getContextPath() + "/login?error=" + e.getReason());
				}
			}
		}
	}

}
