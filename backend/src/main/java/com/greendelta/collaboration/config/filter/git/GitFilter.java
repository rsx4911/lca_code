package com.greendelta.collaboration.config.filter.git;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.jgit.http.server.glue.ServletBinder;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.openlca.git.model.Commit;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.greendelta.collaboration.config.filter.git.GitRequest.GitAction;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.IndexService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Requests;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter
@Component
public class GitFilter extends org.eclipse.jgit.http.server.GitFilter {

	private GroupService groupService;
	private RepositoryService repoService;
	private IndexService indexService;
	private SettingsService settings;
	private NotificationService notificationService;
	private SessionService sessionService;
	private UserService userService;
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
		groupService = app.getBean(GroupService.class);
		repoService = app.getBean(RepositoryService.class);
		indexService = app.getBean(IndexService.class);
		settings = app.getBean(SettingsService.class);
		notificationService = app.getBean(NotificationService.class);
		sessionService = app.getBean(SessionService.class);
		userService = app.getBean(UserService.class);
		this.config = app.getBean(GitFilterConfig.class);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		var request = req instanceof GitRequest ? (GitRequest) req : new GitRequest(req);
		var loggedIn = request.basicHttpLogin(sessionService, userService);
		var previousCommit = getPreviousCommitIfGitPush(request);
		super.doFilter(request, response, new FilterChainWrapper(request, response, chain));
		if (!config.isGitUrl(request))
			return;
		if (request.getGitAction() == GitAction.GIT_PUSH) {
			runPushPostProcessing(request, previousCommit);
		}
		if (loggedIn) {
			request.basicHttpLogout(sessionService);
		}
	}

	private Commit getPreviousCommitIfGitPush(GitRequest request) {
		if (!config.isGitUrl(request) || request.getGitAction() != GitAction.GIT_PUSH)
			return null;
		var path = RepositoryPath.of(Requests.getRoute(request));
		try (var repo = repoService.get(path.group, path.repo)) {
			return repo.commits.find().latest();
		}
	}

	private void runPushPostProcessing(GitRequest request, Commit previousCommit) {
		var path = RepositoryPath.of(Requests.getRoute(request));
		try (var repo = repoService.get(path.group, path.repo)) {
			var latestCommit = repo.commits.find().latest();
			notificationService.dataPushed(repo, latestCommit).send();
			var groupSettings = groupService.getSettings(repo.group);
			checkGroupSizeLimit(repo.group, groupSettings.get(GroupSetting.MAX_SIZE, 0));
			var username = request.getRemoteUser();
			var user = userService.getForUsername(username);
			checkGroupSizeLimit(username, user.settings.maxSize);
			indexService.indexPrivateAsync(RepositoryPath.of(repo.path()), previousCommit, latestCommit);
		}
	}

	private void checkGroupSizeLimit(String group, long maxSize) {
		if (maxSize == 0l)
			return;
		var actualSize = groupService.getSize(group);
		if (maxSize < actualSize) {
			notificationService.groupSizeLimitExceeded(group, maxSize, actualSize).send();
		}
	}

}
