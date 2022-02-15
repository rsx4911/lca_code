package com.greendelta.collaboration.config.filter.git;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.http.server.glue.ServletBinder;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.springframework.beans.factory.annotation.Autowired;

import com.greendelta.collaboration.io.RepositoryJsonWriter;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.NotificationService;

@WebFilter(asyncSupported = true)
public class GitFilter extends org.eclipse.jgit.http.server.GitFilter {

	private final RepositoryService repoService;
	private final SearchService searchService;
	private final SettingsService settingsService;
	private final NotificationService notificationService;
	private final SessionService sessionService;
	private final GitFilterConfig config;

	@Autowired
	public GitFilter(RepositoryService repoService, SearchService searchService, SettingsService settingsService,
			NotificationService notificationService, SessionService sessionService, GitFilterConfig config) {
		this.repoService = repoService;
		this.searchService = searchService;
		this.settingsService = settingsService;
		this.notificationService = notificationService;
		this.sessionService = sessionService;
		this.config = config;
	}

	@Override
	public ServletBinder serve(String path) {
		config.stringPatterns.add(path.substring(1));
		return super.serve(path);
	}

	@Override
	public ServletBinder serveRegex(String expression) {
		config.regexPatterns.add(Pattern.compile(expression));
		return super.serveRegex(expression);
	}

	@Override
	public void setRepositoryResolver(RepositoryResolver<HttpServletRequest> resolver) {
		super.setRepositoryResolver(resolver);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String path = settingsService.get(ServerSetting.REPOSITORY_PATH);
		if (path == null)
			return;
		setRepositoryResolver(new FileResolver<>(new File(path), true));
		super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		var request = req instanceof GitRequest ? (GitRequest) req : new GitRequest(req);
		request.basicHttpLogin(sessionService);
		super.doFilter(request, response, new FilterChainWrapper(request, response, chain));
		if (!config.isGitUrl(request))
			return;
		if (request.isGitPush()) {
			runCommitPostProcessing(new RepositoryPath(request.getRequestURI()));
		}
		request.basicHttpLogout(sessionService);
	}

	private void runCommitPostProcessing(RepositoryPath path) {
		// TODO
		// try (var repo = repoService.get(path)) {
		// var commit = repo.commits().head();
		// var isPublic = repo.settings.is(RepositorySetting.PUBLIC_ACCESS);
		// var generateJson =
		// repo.settings.is(RepositorySetting.JSON_FILE_GENERATION);
		// if (isPublic && generateJson) {
		// RepositoryJsonWriter.writeCurrentAsync(repo);
		// }
		// notificationService.dataCommitted(repo, commit);
		// new Thread(() -> searchService.index(repo, commit)).run();
		// }
	}

}
