package com.greendelta.collaboration.config.filter.git;

import java.util.Base64;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.eclipse.jgit.http.server.GitSmartHttpTools;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.util.Password;
import com.greendelta.collaboration.util.Requests;

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

	public boolean basicHttpLogin(SessionService sessionService) {
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
		var response = sessionService.login(this, username, password, token);
		if (response.status() == HttpStatus.OK) {
			this.remoteUser = username;
			return true;
		}
		if (response.status() == HttpStatus.BAD_REQUEST)
			throw new BadCredentialsException(response.message());
		return false;
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
