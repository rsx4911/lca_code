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
import org.openlca.git.model.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.greendelta.collaboration.config.filter.git.GitRequest.GitAction;
import com.greendelta.collaboration.io.RepositoryJsonWriter;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.util.Requests;

@WebFilter(asyncSupported = true)
@Component
public class GitFilter extends org.eclipse.jgit.http.server.GitFilter {

	private RepositoryService repoService;
	private PostReceiveService postReceiveService;
	private SettingsService settings;
	private NotificationService notificationService;
	private SessionService sessionService;
	private GitFilterConfig config;

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
	public void init(FilterConfig config) throws ServletException {
		initBeans(config);
		String path = settings.get(ServerSetting.REPOSITORY_PATH);
		if (path == null)
			return;
		setRepositoryResolver(new FileResolver<>(new File(path), true));
		super.init(config);
	}

	private void initBeans(FilterConfig config) {
		if (settings != null)
			return;
		var app = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		repoService = app.getBean(RepositoryService.class);
		postReceiveService = app.getBean(PostReceiveService.class);
		settings = app.getBean(SettingsService.class);
		notificationService = app.getBean(NotificationService.class);
		sessionService = app.getBean(SessionService.class);
		this.config = app.getBean(GitFilterConfig.class);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		var request = req instanceof GitRequest ? (GitRequest) req : new GitRequest(req);
		request.basicHttpLogin(sessionService);
		super.doFilter(request, response, new FilterChainWrapper(request, response, chain));
		if (!config.isGitUrl(request))
			return;
		if (request.getGitAction() == GitAction.GIT_PUSH) {
			runCommitPostProcessing(new RepositoryPath(Requests.getRelativePath(request)));
		}
		request.basicHttpLogout(sessionService);
	}

	private void runCommitPostProcessing(RepositoryPath path) {
		try (var repo = repoService.get(path.group, path.repo)) {
			var commit = repo.commits().head();
			var isPublic = repo.settings.is(RepositorySetting.PUBLIC_ACCESS);
			var generateJson = repo.settings.is(RepositorySetting.JSON_FILE_GENERATION);
			notificationService.dataCommitted(repo, commit);
			if (isPublic && generateJson) {
				postReceiveService.generateJson(repo);
			}
			postReceiveService.index(repo, commit);
		}
	}

	@Service
	public static class PostReceiveService {

		private final SearchService searchService;

		@Autowired
		public PostReceiveService(SearchService searchService) {
			this.searchService = searchService;
		}

		@Async
		public void index(Repository repo, Commit commit) {
			searchService.index(repo, commit);
		}

		@Async
		public void generateJson(Repository repo) {
			RepositoryJsonWriter.writeCurrent(repo);
		}

	}

}
