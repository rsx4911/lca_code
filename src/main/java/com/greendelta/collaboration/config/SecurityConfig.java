package com.greendelta.collaboration.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openlca.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.model.Authority;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Requests;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final SettingsService settings;
	private final GitFilterConfig gitFilterConfig;

	public SecurityConfig(SettingsService settings, GitFilterConfig gitFilterConfig) {
		this.settings = settings;
		this.gitFilterConfig = gitFilterConfig;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(config -> config
						.disable())
				.exceptionHandling(config -> config
						.authenticationEntryPoint(this::handleUnauthenticated))
				.authorizeRequests(config -> config
						.antMatchers("/job").permitAll()
						.antMatchers("/ws/public/**").permitAll()
						.antMatchers("/ws/admin/**").hasAuthority(Authority.ADMIN.getAuthority())
						.antMatchers("/ws/datamanager/**").hasAuthority(Authority.DATA_MANAGER.getAuthority())
						.antMatchers("/ws/usermanager/**").hasAuthority(Authority.USER_MANAGER.getAuthority())
						.antMatchers("/ws/**", "/stomp/**").authenticated()
						.antMatchers("/**").access("@repoAccessCheck.canAccess(request)")
				)
				.logout(config -> config
						.logoutUrl("/ws/public/logout")
						.logoutSuccessHandler(getLogoutSuccessHandler()))
				.build();
	}

	private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e)
			throws IOException {
		var url = request.getRequestURL().toString();
		var isGitUrl = false;
		if (url.contains("/ws/") || url.contains("/stomp/") || (isGitUrl = gitFilterConfig.isGitUrl(request))) {
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
		} else {
			var part = url.substring(url.lastIndexOf("/") + 1);
			if (!Arrays.asList("login", "reset-password", "sign-up").contains(part)) {
				var route = Requests.getRelativePath(request);
				var query = request.getQueryString();
				if (!Strings.nullOrEmpty(query)) {
					route += "?" + query;
				}
				route = URLEncoder.encode(route, StandardCharsets.UTF_8.toString());
				response.sendRedirect(request.getServletContext().getContextPath() + "/login?redirectUrl=" + route);
			}
		}
	}

	private LogoutSuccessHandler getLogoutSuccessHandler() {
		return new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}