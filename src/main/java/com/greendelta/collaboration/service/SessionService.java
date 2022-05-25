package com.greendelta.collaboration.service;

import javax.servlet.http.HttpServletRequest;

import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.user.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Service
public class SessionService {

	private final AuthenticationManager authManager;
	private final UserService userService;
	private final SettingsService settingsService;
	private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

	@Autowired
	public SessionService(AuthenticationManager authManager, UserService userService, SettingsService settingsService) {
		this.authManager = authManager;
		this.userService = userService;
		this.settingsService = settingsService;
	}
	
	public LoginResponse login(HttpServletRequest request, String username, String password, Integer token) {
		try {
			var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (BadCredentialsException e) {
			var user = userService.getForUsername(username);
			if (Strings.nullOrEmpty(user.password))
				return new LoginResponse(HttpStatus.BAD_REQUEST,
						"We have updated our password encryption. Since we only store encrypted passwords, we are not able to migrate your current password. Please use the 'Forgot your password?' link below to request a new password being sent to your email address.");
			return new LoginResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		if (userService.isAnonymous())
			return new LoginResponse(HttpStatus.UNAUTHORIZED, "Unknown error");
		var user = userService.getCurrentUser();
		if (user.isDeactivated()) {
			logout(request);
			return new LoginResponse(HttpStatus.UNAUTHORIZED, "User is deactivated or approval is pending");
		}
		if (!Strings.nullOrEmpty(user.twoFactorSecret)) {
			if (token == null || token == 0) {
				logout(request);
				return new LoginResponse(HttpStatus.BAD_REQUEST, "tokenRequired");
			}
			var valid = authenticator.authorize(user.twoFactorSecret, token);
			if (!valid) {
				logout(request);
				return new LoginResponse(HttpStatus.UNAUTHORIZED, "Invalid token");
			}
		}
		var maintenanceMode = settingsService.is(ServerSetting.MAINTENANCE_MODE);
		if (maintenanceMode && !user.isAdmin()) {
			logout(request);
			return new LoginResponse(HttpStatus.FORBIDDEN, settingsService.get(ServerSetting.MAINTENANCE_MESSAGE));
		}
		return new LoginResponse(HttpStatus.OK, null);
	}

	public void logout(HttpServletRequest request) {
		new SecurityContextLogoutHandler().logout(request, null, null);
	}

	public record LoginResponse(HttpStatus status, String message) {
	}

}
