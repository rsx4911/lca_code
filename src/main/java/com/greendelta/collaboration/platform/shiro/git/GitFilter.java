package com.greendelta.collaboration.platform.shiro.git;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.eclipse.jgit.http.server.GitSmartHttpTools;
import org.eclipse.jgit.http.server.glue.ServletBinder;
import org.eclipse.jgit.transport.resolver.FileResolver;

public class GitFilter extends org.eclipse.jgit.http.server.GitFilter {

	private final Set<String> stringPatterns = new HashSet<>();
	private final Set<Pattern> regexPatterns = new HashSet<>();
	private boolean initialized;

	@Override
	public ServletBinder serve(String path) {
		stringPatterns.add(path.substring(1));
		return super.serve(path);
	}

	@Override
	public ServletBinder serveRegex(String expression) {
		regexPatterns.add(Pattern.compile(expression));
		return super.serveRegex(expression);
	}

	public void init(ServletContext context) throws ServletException {
		if (initialized)
			return;
		init(new ServletContextWrapperConfig(context));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		setRepositoryResolver(new FileResolver<>(new File("C:/Users/Sebastian/test/git/server"), true));
		super.init(filterConfig);
		initialized = true;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof GitRequest)) {
			request = new GitRequest(request);
		}
		super.doFilter(request, response, new FilterChainWrapper(request, response, chain));
	}

	public boolean isGitUrl(ServletRequest request) {
		String pathInfo = new GitRequest(request).getPathInfo();
		for (String pattern : stringPatterns)
			if (pathInfo.endsWith(pattern))
				return true;
		for (Pattern pattern : regexPatterns)
			if (pattern.matcher(pathInfo).matches())
				return true;
		return false;
	}

	public boolean isGitPush(ServletRequest request) {
		String pathInfo = new GitRequest(request).getPathInfo();
		String query = WebUtils.toHttp(request).getQueryString();
		if (pathInfo != null && pathInfo.endsWith("/" + GitSmartHttpTools.RECEIVE_PACK))
			return true;
		return query != null && query.equals("service=" + GitSmartHttpTools.RECEIVE_PACK);
	}

}
