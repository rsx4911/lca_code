package com.greendelta.collaboration.platform.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.platform.guice.util.CloudSession;
import com.greendelta.collaboration.service.AccessService;

/**
 * This filter should only be used for repository urls (/[group]/[repo]/**).
 * Other authorization checks are configured directly in the shiro module or
 * will be performed in the web services or web sockets directly
 */
public class RepoAccessFilter extends org.apache.shiro.web.filter.authz.AuthorizationFilter {

	@Inject
	private Provider<CloudSession> sessionProvider;

	@Inject
	private AccessService accessService;

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
		try {
			HttpServletRequest httpRequest = WebUtils.toHttp(request);
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			String url = httpRequest.getRequestURL().toString();
			if (url.contains("/ws/") || url.contains("/sockets/")) {
				httpResponse.reset();
				httpResponse.setStatus(403);
			} else {
				sessionProvider.get().redirectUrl = httpRequest.getRequestURI();
				httpResponse.sendRedirect(request.getServletContext().getContextPath() + "/login");
			}
		} catch (ClassCastException | IOException ex) {
			return false;
		}
		return false;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String url = httpRequest.getRequestURI().toString();
		if (url.contains("/ws/") || url.contains("/sockets/"))
			return true; // web service or web socket -> ignore
		if (url.startsWith("/"))
			url = url.substring(1);
		if (!url.contains("/"))
			return true; // not a repository url -> ignore
		String group = url.substring(0, url.indexOf("/"));
		String repo = url.substring(url.indexOf("/") + 1);
		if (repo.contains("/"))
			repo = repo.substring(0, repo.indexOf("/"));
		return accessService.canRead(group + "/" + repo);
	}
}
