package com.greendelta.collaboration.config;

import java.io.IOException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
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
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Requests;
import com.greendelta.collaboration.util.Routes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final PermissionsService permissions;
	private final UserService userService;
	private final SettingsService settings;
	private final GitFilterConfig gitFilterConfig;
	private final boolean basicAuthEnabled;
	private AuthenticationManager authManager;
	@Autowired(required = false)
	private ClientRegistrationRepository authProviderRepository;

	public SecurityConfig(PermissionsService permissions, UserService userService, SettingsService settings,
			GitFilterConfig gitFilterConfig, @Value("${authentication.basic-auth:#{false}}") boolean basicAuthEnabled) {
		this.permissions = permissions;
		this.userService = userService;
		this.settings = settings;
		this.gitFilterConfig = gitFilterConfig;
		this.basicAuthEnabled = basicAuthEnabled;
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http = http
				.headers(config -> config
						.frameOptions(options -> options
								.sameOrigin()))
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
						.logoutSuccessHandler(getLogoutSuccessHandler())
						.deleteCookies("JSESSIONID"));
		if (authProviderRepository != null) {
			http = http.oauth2Login(config -> config
					.successHandler(this::onOauthSuccess)
					.userInfoEndpoint(endpoint -> endpoint
							.oidcUserService(userService)));
		}
		if (basicAuthEnabled) {
			http = http.httpBasic(config -> config
					.realmName(settings.serverConfig.get(ServerSetting.SERVER_NAME)));
		}
		return http.build();
	}

	private void onOauthSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication auth) throws ServletException, IOException {
		if (userService.getUser(auth) == null && auth.getPrincipal() instanceof OAuth2User oauthUser) {
			var user = userService.createUser(oauthUser);
			// if (user == null)
			// ;// TODO handle this
			if (settings.is(ServerSetting.USER_REGISTRATION_APPROVAL_ENABLED)) {
				user.deactivate();
			}
			userService.insert(user);
		}
		new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, auth);
	}

	private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException {
		var route = Requests.getRoute(request);
		var isGitUrl = false;
		if (route.startsWith("ws/") || route.startsWith("stomp/") || (isGitUrl = gitFilterConfig.isGitUrl(request))) {
			response.reset();
			if (e instanceof BadCredentialsException) {
				if (e.getMessage().contains("tokenRequired")) {
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
			response.sendRedirect(Requests.getLoginRedirectUrl(request));
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
		var sessionService = new SessionService(authManager, userService);
		var loggedIn = request.basicHttpLogin(sessionService, userService);
		try {
			if (request.getGitAction() == GitAction.GIT_PUSH || request.getGitAction() == GitAction.GIT_PUSH_SERVICE)
				return permissions.canWriteTo(repoId) && !areCommitsProhibited(repoId);
			return permissions.canRead(repoId);
		} finally {
			if (loggedIn) {
				request.basicHttpLogout(sessionService);
			}
		}
	}

	private boolean areCommitsProhibited(String repoId) {
		return settings.get(SettingType.REPOSITORY_SETTING, repoId, permissions::canSetSettingsOf)
				.is(RepositorySetting.PROHIBIT_COMMITS);
	}

	private LogoutSuccessHandler getLogoutSuccessHandler() {
		return new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
	}

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
		this.authManager = authenticationConfiguration.getAuthenticationManager();
		return this.authManager;
	}

}