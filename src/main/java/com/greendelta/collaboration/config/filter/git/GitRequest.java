package com.greendelta.collaboration.config.filter.git;

import java.util.Base64;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.jgit.http.server.GitSmartHttpTools;
import org.springframework.security.authentication.BadCredentialsException;

import com.greendelta.collaboration.service.SessionService;
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
		return Requests.getRelativePath(this);
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
		try {
			sessionService.login(principal[0], principal[1]);
			this.remoteUser = principal[0];
			return true;
		} catch (BadCredentialsException e) {
			return false;
		}
	}

	public void basicHttpLogout(SessionService sessionService) {
		sessionService.logout(this);
		this.remoteUser = null;
	}

	public boolean isGitPush() {
		var pathInfo = getPathInfo();
		if (pathInfo != null && pathInfo.endsWith("/" + GitSmartHttpTools.RECEIVE_PACK))
			return true;
		var query = getQueryString();
		return query != null && query.equals("service=" + GitSmartHttpTools.RECEIVE_PACK);
	}

}
