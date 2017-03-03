package com.greendelta.collaboration.platform.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.platform.guice.util.CloudSession;

public class AuthenticationFilter extends UserFilter {

	@Inject
	private Provider<CloudSession> sessionProvider;

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		try {
			HttpServletRequest httpRequest = WebUtils.toHttp(request);
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			boolean isWebServiceUrl = httpRequest.getRequestURL().toString().contains("/ws/");
			if (!isWebServiceUrl) {
				sessionProvider.get().redirectUrl = httpRequest.getRequestURI();
				httpResponse.sendRedirect(request.getServletContext().getContextPath() + "/login");
			} else {
				httpResponse.reset();
				httpResponse.setStatus(401);
			}
		} catch (ClassCastException ex) {
			return super.onAccessDenied(request, response);
		}
		return false;
	}

}
