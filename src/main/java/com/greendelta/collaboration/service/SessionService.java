package com.greendelta.collaboration.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

	private final AuthenticationManager authManager;

	@Autowired
	public SessionService(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	public void login(String username, String password) {
		var token = new UsernamePasswordAuthenticationToken(username, password);
		var auth = authManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	public void logout(HttpServletRequest request) {
		new SecurityContextLogoutHandler().logout(request, null, null);
	}

}
