package com.greendelta.collaboration.config.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.server.ResponseStatusException;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.model.User;
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
				if (redirectToIndex(user, route)) {
					response.sendRedirect(request.getContextPath() + "/");
				} else if (redirectToLogin(user, route)) {
					response.sendRedirect(Requests.getLoginRedirectUrl(request));
				} else if (dispatchPublicIndex(user, route)) {
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

	private boolean redirectToIndex(User user, String route) {
		return !user.isAnonymous() && Routes.isLoginUrl(route);
	}
	
	private boolean redirectToLogin(User user, String route) {
		if (!user.isAnonymous() || route.equals("login"))
			return false;
		if (route.equals("") && !settings.is(ServerSetting.HOMEPAGE_ENABLED))
			return true;
		if (route.equals("search") && !settings.searchConfig.isSearchAvailable())
			return true;
		if (route.equals("sign-up") && !settings.is(ServerSetting.USER_REGISTRATION_ENABLED))
			return true;
		if (Routes.isPublicUrl(route))
			return false;
		if (!settings.is(ServerSetting.RELEASES_ENABLED))
			return true;
		return false;
	}

	private boolean dispatchPublicIndex(User user, String route) {
		var publicIndex = getClass().getResource("/static/index_public.html");
		return user.isAnonymous() && !Routes.isPublicUrl(route) && publicIndex != null;
	}

}
