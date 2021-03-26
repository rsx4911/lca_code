package com.greendelta.collaboration.platform.shiro;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.openlca.cloud.error.RepositoryNotFoundException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.platform.guice.util.CloudSession;
import com.greendelta.collaboration.platform.shiro.git.GitFilter;
import com.greendelta.collaboration.platform.shiro.git.GitRequest;
import com.greendelta.collaboration.service.user.AccessService;

/**
 * This filter should only be used for repository urls (/[group]/[repo]/**).
 * Other authorization checks are configured directly in the shiro module or
 * will be performed in the web services or web sockets directly
 */
public class RepoAccessFilter extends org.apache.shiro.web.filter.authz.AuthorizationFilter {

	private final Provider<CloudSession> sessionProvider;
	private final AccessService accessService;
	private final Provider<Subject> subjectProvider;
	private final GitFilter gitFilter;

	@Inject
	public RepoAccessFilter(Provider<CloudSession> sessionProvider, AccessService accessService,
			Provider<Subject> subjectProvider, GitFilter gitFilter) {
		this.sessionProvider = sessionProvider;
		this.accessService = accessService;
		this.subjectProvider = subjectProvider;
		this.gitFilter = gitFilter;
	}

	private boolean isApi(ServletRequest request) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String url = httpRequest.getRequestURL().toString();
		return url.contains("/ws/") || url.contains("/sockets/");
	}

	@Override
	protected void executeChain(ServletRequest request, ServletResponse response, FilterChain chain) throws Exception {
		if (isApi(request) || !gitFilter.isGitUrl(request)) {
			super.executeChain(request, response, chain);
		}
	}

	@Override
	protected boolean onAccessDenied(ServletRequest req, ServletResponse resp) {
		try {
			HttpServletRequest request = WebUtils.toHttp(req);
			HttpServletResponse response = WebUtils.toHttp(resp);
			if (isApi(req)) {
				response.reset();
				response.setStatus(403);
			} else if (gitFilter.isGitUrl(req)) {
				response.reset();
				response.setStatus(401);
				response.setHeader("WWW-Authenticate", "Basic realm=\"Collaboration Server\"");
			} else {
				sessionProvider.get().redirectUrl = request.getRequestURI();
				response.sendRedirect(req.getServletContext().getContextPath() + "/login");
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue)
			throws ServletException, IOException {
		gitFilter.init(req.getServletContext());
		HttpServletRequest request = WebUtils.toHttp(req);
		HttpServletResponse response = WebUtils.toHttp(resp);
		String url = request.getRequestURI();
		if (isApi(request))
			return true; // web service or web socket -> ignore
		if (url.startsWith("/"))
			url = url.substring(1);
		if (!url.contains("/"))
			return true; // not a repository url -> ignore
		String group = url.substring(0, url.indexOf("/"));
		String repo = url.substring(url.indexOf("/") + 1);
		if (repo.contains("/"))
			repo = repo.substring(0, repo.indexOf("/"));
		try {
			boolean isGitUrl = gitFilter.isGitUrl(req);
			String repoId = group + "/" + repo;
			if (!isGitUrl)
				return accessService.canRead(repoId);
			return canGitAccess(new GitRequest(request), response, repoId);
		} catch (RepositoryNotFoundException e) {
			response.reset();
			response.setStatus(404);
			return true;
		}
	}

	private boolean canGitAccess(GitRequest request, HttpServletResponse response, String repoId)
			throws IOException, ServletException {
		Subject subject = subjectProvider.get();
		request.basicHttpLogin(subject);
		boolean canAccess = false;
		if (gitFilter.isGitPush(request)) {
			canAccess = accessService.canWrite(repoId);
		} else {
			canAccess = accessService.canRead(repoId);
		}
		request.basicHttpLogout(subject);
		if (!canAccess)
			return false;
		gitFilter.doFilter(request, response, null);
		return true;
	}

}
