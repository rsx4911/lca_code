package com.greendelta.collaboration.config.filter.git;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class GitFilterConfig {

	final Set<String> stringPatterns = new HashSet<>();
	final Set<Pattern> regexPatterns = new HashSet<>();

	public boolean isGitUrl(HttpServletRequest request) {
		var gitRequest = request instanceof GitRequest ? (GitRequest) request : new GitRequest(request);
		var pathInfo = gitRequest.getPathInfo();
		for (var pattern : stringPatterns)
			if (pathInfo.endsWith(pattern))
				return true;
		for (var pattern : regexPatterns)
			if (pattern.matcher(pathInfo).matches())
				return true;
		return false;
	}

}
