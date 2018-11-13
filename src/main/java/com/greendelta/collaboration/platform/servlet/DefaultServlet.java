package com.greendelta.collaboration.platform.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.greendelta.collaboration.platform.guice.util.CloudSession;

@Singleton
public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -7021790186597193927L;
	private Provider<CloudSession> sessionProvider;
	private Provider<Subject> subjectProvider;

	@Inject
	public DefaultServlet(Provider<CloudSession> sessionProvider, Provider<Subject> subjectProvider) {
		this.sessionProvider = sessionProvider;
		this.subjectProvider = subjectProvider;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURL().toString();
		boolean isLoginUrl = url.endsWith("/login");
		boolean isImprintUrl = url.endsWith("/imprint");
		boolean isGraphUrl = url.endsWith("/graph/graph.html");
		if (isGraphUrl) {
			forward("/graph/graph.html", request, response);
		} else if (isImprintUrl) {
			forward("/imprint.html", request, response);
		} else if (isLoginUrl) {
			Subject subject = subjectProvider.get();
			if (subject != null && subject.isAuthenticated()) {
				response.sendRedirect(request.getContextPath() + "/");
			} else {
				forward("/login.html", request, response);
			}
		} else {
			String redirectUrl = sessionProvider.get().redirectUrl;
			sessionProvider.get().redirectUrl = null;
			Subject subject = subjectProvider.get();
			if (subject != null && subject.isAuthenticated() && !Strings.isNullOrEmpty(redirectUrl)) {
				response.sendRedirect(redirectUrl);
			} else {
				File publicIndex = new File(request.getServletContext().getRealPath("index_public.html"));
				if (!subject.isAuthenticated() && publicIndex.exists()) {
					forward("/index_public.html", request, response);
				} else {
					forward("/index.html", request, response);					
				}
			}
		}
	}

	private void forward(String path, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher(path).forward(request, response);
	}

}
