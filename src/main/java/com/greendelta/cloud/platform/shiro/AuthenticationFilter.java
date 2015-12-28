package com.greendelta.cloud.platform.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

public class AuthenticationFilter extends UserFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		try {
			HttpServletRequest httpRequest = WebUtils.toHttp(request);
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			boolean isWebServiceUrl = httpRequest.getRequestURL().toString()
					.contains("/ws/");
			if (!isWebServiceUrl)
				httpResponse.sendRedirect("/login");
			else {
				httpResponse.reset();
				httpResponse.setStatus(401);
			}
		} catch (ClassCastException ex) {
			return super.onAccessDenied(request, response);
		}
		return false;
	}

}
