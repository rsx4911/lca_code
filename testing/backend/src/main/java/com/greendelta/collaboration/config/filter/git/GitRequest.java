package com.greendelta.collaboration.config.filter.git;

import java.util.Base64;

import org.eclipse.jgit.http.server.GitSmartHttpTools;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.util.Requests;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class GitRequest extends HttpServletRequestWrapper {

	private String remoteUser;

	public GitRequest(ServletRequest request) {
		super((HttpServletRequest) request);
	}

	@Override
	public HttpServletRequest getRequest() {
		return (HttpServletRequest) super.getRequest();
	}

	@Override
	public String getPathInfo() {
		// default servlet returns incorrect pathinfo
		return Requests.getRoute(this);
	}

	@Override
	public String getRemoteUser() {
		return remoteUser;
	}

	public boolean basicHttpLogin(SessionService sessionService, UserService userService) {
		var auth = getRequest().getHeader("Authorization");
		if (auth == null)
			return false;
		var typeAndBase64 = auth.split(" ");
		if (typeAndBase64.length != 2 || !typeAndBase64[0].equals("Basic"))
			return false;
		var principal = new String(Base64.getDecoder().decode(typeAndBase64[1])).split(":");
		if (principal.length != 2)
			return false;
		var username = principal[0];
		var password = Password.getPasswordWithoutToken(principal[1]);
		var token = Password.getToken(principal[1]);
		try {
			var user = sessionService.login(this, username, password, token);
			this.remoteUser = user.username;
			return true;
		} catch (ResponseStatusException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
				throw new BadCredentialsException(e.getMessage());
			return false;
		}
	}

	public void basicHttpLogout(SessionService sessionService) {
		sessionService.logout(this);
		this.remoteUser = null;
	}

	public GitAction getGitAction() {
		var pathInfo = getPathInfo();
		if (pathInfo != null && pathInfo.endsWith("/" + GitSmartHttpTools.RECEIVE_PACK))
			return GitAction.GIT_PUSH;
		var query = getQueryString();
		if (query != null && query.equals("service=" + GitSmartHttpTools.RECEIVE_PACK))
			return GitAction.GIT_PUSH_SERVICE;
		return GitAction.OTHER;
	}

	public enum GitAction {

		GIT_PUSH,

		GIT_PUSH_SERVICE,

		OTHER;

	}

}
