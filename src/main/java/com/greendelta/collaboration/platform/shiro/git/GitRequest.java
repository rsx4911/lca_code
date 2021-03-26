package com.greendelta.collaboration.platform.shiro.git;

import java.util.Base64;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

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
		// guice 3.0 servlet returns incorrect pathinfo
		String pathInfo = getRequest().getRequestURI();
		String contextPath = getRequest().getContextPath();
		if (contextPath != null && contextPath.length() > 0) {
			pathInfo = pathInfo.substring(contextPath.length() + 1);
		}
		return pathInfo;
	}

	@Override
	public String getRemoteUser() {
		return remoteUser;
	}
	
	public void basicHttpLogin(Subject subject) {
		if (subject.isAuthenticated())
			return;
		String auth = getRequest().getHeader("Authorization");
		if (auth == null)
			return;
		String[] typeAndBase64 = auth.split(" ");
		if (typeAndBase64.length != 2 || !typeAndBase64[0].equals("Basic"))
			return;
		String[] principal = new String(Base64.getDecoder().decode(typeAndBase64[1])).split(":");
		if (principal.length != 2)
			return;
		subject.login(new UsernamePasswordToken(principal[0], principal[1]));
		this.remoteUser = principal[0];
	}

	public void basicHttpLogout(Subject subject) {
		if (!subject.isAuthenticated())
			return;
		subject.logout();
	}

}
