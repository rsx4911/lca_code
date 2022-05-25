package com.greendelta.collaboration.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.greendelta.collaboration.config.filter.git.GitFilterConfig;
import com.greendelta.collaboration.model.Authority;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Requests;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;
	private final SettingsService settingsService;
	private final GitFilterConfig gitFilterConfig;

	@Autowired
	public SecurityConfig(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService,
			SettingsService settingsService, GitFilterConfig gitFilterConfig) {
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
		this.settingsService = settingsService;
		this.gitFilterConfig = gitFilterConfig;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.servletApi().and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(this::handleUnauthorized).and()
				.authorizeRequests()
				.antMatchers("/job").permitAll()
				.antMatchers("/ws/public/**").permitAll()
				.antMatchers("/ws/admin/**").hasAuthority(Authority.ADMIN.getAuthority())
				.antMatchers("/ws/datamanager/**").hasAuthority(Authority.DATA_MANAGER.getAuthority())
				.antMatchers("/ws/usermanager/**").hasAuthority(Authority.USER_MANAGER.getAuthority())
				.antMatchers("/ws/**").authenticated()
				.antMatchers("/**").access("@repoAccessCheck.canAccess(request)").and()
				.logout().logoutUrl("/ws/public/logout").logoutSuccessHandler(getLogoutSuccessHandler());
	}

	private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException {
		var url = request.getRequestURL().toString();
		var isGitUrl = false;
		if (url.contains("/ws/") || url.contains("/sockets/") || (isGitUrl = gitFilterConfig.isGitUrl(request))) {
			response.reset();			
			if (e instanceof BadCredentialsException) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());				
			} else {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());				
				if (isGitUrl) {
					String serverName = settingsService.serverConfig.get(ServerSetting.SERVER_NAME);
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

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

}