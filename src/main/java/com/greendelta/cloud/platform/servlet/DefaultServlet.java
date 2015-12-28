package com.greendelta.cloud.platform.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -7021790186597193927L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean isLoginUrl = request.getRequestURL().toString()
				.endsWith("/login");
		if (isLoginUrl)
			forward("/login.html", request, response);
		else
			forward("/index.html", request, response);
	}

	private void forward(String path, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(path).forward(request, response);
	}

}
