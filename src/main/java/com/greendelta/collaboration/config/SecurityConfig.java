package com.greendelta.collaboration.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.openlca.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.config.filter.git.GitRequest;
import com.greendelta.collaboration.config.filter.git.GitRequest.GitAction;
import com.greendelta.collaboration.model.Authority;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SessionService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Requests;
import com.greendelta.collaboration.util.Routes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final AccessService accessService;
	private final UserService userService;
	private final SettingsService settings;
	private final GitFilterConfig gitFilterConfig;
	private AuthenticationManager authManager;

	public SecurityConfig(AccessService accessService, UserService userService, SettingsService settings,
			GitFilterConfig gitFilterConfig) {
		this.accessService = accessService;
		this.userService = userService;
		this.settings = settings;
		this.gitFilterConfig = gitFilterConfig;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.sessionManagement(config -> config
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.securityContext(config -> config
						.requireExplicitSave(false))
				.csrf(config -> config
						.disable())
				.exceptionHandling(config -> config
						.authenticationEntryPoint(this::handleUnauthenticated))
				.securityMatcher("/**").authorizeHttpRequests(config -> config
						.requestMatchers("/job", "/ws/public/**").permitAll()
						.requestMatchers("/ws/admin/**").hasAuthority(Authority.ADMIN.getAuthority())
						.requestMatchers("/ws/datamanager/**").hasAuthority(Authority.DATA_MANAGER.getAuthority())
						.requestMatchers("/ws/usermanager/**").hasAuthority(Authority.USER_MANAGER.getAuthority())
						.requestMatchers("/ws/**", "/stomp/**").authenticated()
						.requestMatchers("/**").access(this::canAccessRepo))
				.logout(config -> config
						.logoutUrl("/ws/public/logout")
						.logoutSuccessHandler(getLogoutSuccessHandler()))
				.build();
	}

	private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e)
			throws IOException {
		var route = Requests.getRoute(request);
		var isGitUrl = false;
		if (route.startsWith("ws/") || route.startsWith("stomp/") || (isGitUrl = gitFilterConfig.isGitUrl(request))) {
			response.reset();
			if (e instanceof BadCredentialsException) {
				if (e.getMessage().equals("tokenRequired")) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
				} else {
					response.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
				}
			} else {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				if (isGitUrl) {
					String serverName = settings.serverConfig.get(ServerSetting.SERVER_NAME);
					response.setHeader("WWW-Authenticate", "Basic realm=\"" + serverName + "\"");
				}
			}
		} else if (!Routes.isLoginUrl(route)) {
			var redirectUrl = Requests.getRoute(request);
			var query = request.getQueryString();
			if (!Strings.nullOrEmpty(query)) {
				redirectUrl += "?" + query;
			}
			redirectUrl = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.toString());
			response.sendRedirect(request.getServletContext().getContextPath() + "/login?redirectUrl=" + redirectUrl);
		}
	}

	private AuthorizationDecision canAccessRepo(Supplier<Authentication> authentication,
			RequestAuthorizationContext context) {
		var request = context.getRequest();
		var route = RepositoryPath.of(Requests.getRoute(request));
		if (!route.isGroupOrRepo())
			return new AuthorizationDecision(true);
		// web access is checked via the controllers
		if (!gitFilterConfig.isGitUrl(request))
			return new AuthorizationDecision(true);
		var canGitAccess = canGitAccess(new GitRequest(request), route.toString());
		return new AuthorizationDecision(canGitAccess);
	}

	private boolean canGitAccess(GitRequest request, String repoId) {
		var sessionService = new SessionService(authManager, userService, settings);
		var loggedIn = request.basicHttpLogin(sessionService);
		try {
			if (request.getGitAction() == GitAction.GIT_PUSH || request.getGitAction() == GitAction.GIT_PUSH_SERVICE)
				return accessService.canWrite(repoId) && !areCommitsProhibited(repoId);
			return accessService.canRead(repoId);
		} finally {
			if (loggedIn) {
				request.basicHttpLogout(sessionService);
			}
		}
	}

	private boolean areCommitsProhibited(String repoId) {
		return settings.get(SettingType.REPOSITORY_SETTING, repoId, accessService::canSetSettings)
				.is(RepositorySetting.PROHIBIT_COMMITS);
	}

	private LogoutSuccessHandler getLogoutSuccessHandler() {
		return new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		this.authManager = authenticationConfiguration.getAuthenticationManager();
		return this.authManager;
	}

}