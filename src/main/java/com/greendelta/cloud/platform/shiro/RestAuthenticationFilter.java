package com.greendelta.cloud.platform.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

public class RestAuthenticationFilter extends UserFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		try {
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			httpResponse.reset();
			httpResponse.setStatus(403);
		} catch (ClassCastException ex) {
			return super.onAccessDenied(request, response);
		}
		return false;
	}

}
