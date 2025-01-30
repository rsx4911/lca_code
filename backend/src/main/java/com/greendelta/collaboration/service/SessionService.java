package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SessionService {

	private final static Logger log = LogManager.getLogger(SessionService.class);
	private final AuthenticationManager authManager;
	private final UserService userService;
	private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

	@Autowired(required = false)
	private ClientRegistrationRepository authProviderRepository;
	private List<AuthProvider> authProviders;

	public SessionService(AuthenticationManager authManager, UserService userService) {
		this.authManager = authManager;
		this.userService = userService;
	}

	public List<AuthProvider> getAuthProviders() {
		if (authProviders == null) {
			authProviders = getAuthProviders(authProviderRepository);
		}
		return authProviders;
	}

	private List<AuthProvider> getAuthProviders(ClientRegistrationRepository authProviderRepository) {
		if (!(authProviderRepository instanceof InMemoryClientRegistrationRepository providers))
			return new ArrayList<>();
		var it = providers.iterator();
		var authProviders = new ArrayList<AuthProvider>();
		while (it.hasNext()) {
			var p = it.next();
			authProviders.add(new AuthProvider(p.getRegistrationId(), p.getClientName()));
		}
		return authProviders;
	}

	public User login(HttpServletRequest request, String username, String password, Integer token) {
		try {
			var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (BadCredentialsException e) {
			var user = userService.getForUsername(username);
			if (user != null && Strings.nullOrEmpty(user.password))
				throw Response.badRequest(
						"We have updated our password encryption. Since we only store encrypted passwords, we are not able to migrate your current password. Please use the 'Forgot your password?' link below to request a new password being sent to your email address.");
			throw Response.unauthorized("Invalid credentials");
		}
		try {
			var user = userService.getCurrentUser();
			if (user.isAnonymous())
				throw Response.unauthorized("Unknown error");
			if (!Strings.nullOrEmpty(user.twoFactorSecret)) {
				if (token == null || token == 0) {
					logout(request);
					throw Response.badRequest("tokenRequired");
				}
				var valid = authenticator.authorize(user.twoFactorSecret, token);
				if (!valid)
					throw Response.badRequest("Invalid token");
			}
			log.info("User {} successfully logged in", username);
			return user;
		} catch (ResponseStatusException e) {
			logout(request);
			throw e;
		}
	}

	public void logout(HttpServletRequest request) {
		new SecurityContextLogoutHandler().logout(request, null, null);
	}

	public record AuthProvider(String id, String name) {

		public AuthProvider(String id, String name) {
			this.id = id;
			this.name = name != null ? name : id;
		}

	}

}
