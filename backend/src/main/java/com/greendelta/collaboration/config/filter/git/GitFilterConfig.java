package com.greendelta.collaboration.config.filter.git;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.greendelta.collaboration.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class GitFilterConfig {

	final Set<String> stringPatterns = new HashSet<>();
	final Set<Pattern> regexPatterns = new HashSet<>();
	private final static Logger log = LogManager.getLogger(GitFilterConfig.class);

	public boolean isGitUrl(HttpServletRequest request) {
		var gitRequest = request instanceof GitRequest ? (GitRequest) request : new GitRequest(request);
		var pathInfo = gitRequest.getPathInfo();
		log.info("This is the {} ,,,,{},,,{},,,{}  {}   {}",gitRequest,pathInfo, stringPatterns,regexPatterns, stringPatterns.size(), regexPatterns.size());
		for (var pattern : stringPatterns)
			if (pathInfo.endsWith(pattern))
				return true;
		for (var pattern : regexPatterns)
			if (pattern.matcher(pathInfo).matches())
				return true;
		return false;
	}

}
