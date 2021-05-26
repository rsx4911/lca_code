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
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.error.RepositoryNotFoundException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.platform.guice.util.CloudSession;
import com.greendelta.collaboration.platform.shiro.git.GitFilter;
import com.greendelta.collaboration.platform.shiro.git.GitRequest;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.util.io.RepositoryJsonWriter;

/**
 * This filter should only be used for repository urls (/[group]/[repo]/**).
 * Other authorization checks are configured directly in the shiro module or
 * will be performed in the web services or web sockets directly
 */
public class RepoAccessFilter extends org.apache.shiro.web.filter.authz.AuthorizationFilter {

	private final Provider<CloudSession> sessionProvider;
	private final AccessService accessService;
	private final RepositoryService repoService;
	private final SearchService searchService;
	private final NotificationService notificationService;
	private final Provider<Subject> subjectProvider;
	private final GitFilter gitFilter;

	@Inject
	public RepoAccessFilter(Provider<CloudSession> sessionProvider, AccessService accessService,
			RepositoryService repoService, SearchService searchService, NotificationService notificationService, Provider<Subject> subjectProvider,
			GitFilter gitFilter) {
		this.sessionProvider = sessionProvider;
		this.accessService = accessService;
		this.repoService = repoService;
		this.searchService = searchService;
		this.notificationService = notificationService;
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
		} else if (gitFilter.isGitPush(request)) {
			String repoId = getRepoId(request);
			Repository repo = repoService.get(repoId);
			searchService.updateAsync(repo); // TODO test this
			if (repo.settings.is(RepositorySetting.PUBLIC_ACCESS)
					&& repo.settings.is(RepositorySetting.JSON_FILE_GENERATION)) {
				RepositoryJsonWriter.writeCurrentAsync(repo); // TODO test this
			}
			Commit commit = repo.commits.find().latest();
			notificationService.dataCommitted(repo, commit);
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
		try {
			boolean isGitUrl = gitFilter.isGitUrl(req);
			String repoId = getRepoId(request);
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
			canAccess = accessService.canWrite(repoId)
					&& !repoService.get(repoId).settings.is(RepositorySetting.PROHIBIT_COMMITS);
		} else {
			canAccess = accessService.canRead(repoId);
		}
		request.basicHttpLogout(subject);
		if (!canAccess)
			return false;
		gitFilter.doFilter(request, response, null);
		return true;
	}

	private String getRepoId(ServletRequest request) {
		String url = ((HttpServletRequest) request).getRequestURI();
		if (url.startsWith("/"))
			url = url.substring(1);
		String group = url.substring(0, url.indexOf("/"));
		String repo = url.substring(url.indexOf("/") + 1);
		if (repo.contains("/"))
			repo = repo.substring(0, repo.indexOf("/"));
		return group + "/" + repo;
	}

}
