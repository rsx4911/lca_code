package com.greendelta.collaboration.platform.guice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.openlca.cloud.util.Logs;

import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.SessionScoped;
import com.greendelta.collaboration.platform.guice.util.CloudSession;
import com.greendelta.collaboration.platform.servlet.MaintenanceModeFilter;
import com.greendelta.collaboration.platform.shiro.AuthenticationFilter;
import com.greendelta.collaboration.platform.shiro.JpaRealm;
import com.greendelta.collaboration.platform.shiro.RepoAccessFilter;
import com.greendelta.collaboration.util.Names;

public class ShiroModule extends ShiroWebModule {

	private static final Logger log = LogManager.getLogger(ShiroModule.class);
	private static final Key<RolesAuthorizationFilter> ADMIN_USER = config(ROLES, "admin");
	private static final Key<RolesAuthorizationFilter> USER_MANAGER_USER = config(ROLES, "userManager");
	private static final Key<RolesAuthorizationFilter> DATA_MANAGER_USER = config(ROLES, "dataManager");
	private static final Key<AuthenticationFilter> LOGGED_IN_USER = Key.get(AuthenticationFilter.class);
	private static final Key<RepoAccessFilter> REPO_ACCESS = Key.get(RepoAccessFilter.class);
	// custom gulp build can change the "html" resources that are publicly available
	public static final String[] CUSTOM_PUBLIC_RESOURCES = { "/contact", "/cookies", "/imprint", "/login", "/privacy" };

	ShiroModule(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	protected void configureShiroWeb() {
		bindRealm().to(JpaRealm.class).in(Singleton.class);
		bind(JpaRealm.class);
		expose(JpaRealm.class);
		expose(Subject.class);
		expose(CloudSession.class);
		anonymous("/ws/public/**", "/sockets/public/**");
		admin("/ws/admin/**", "/sockets/admin/**");
		userManager("/ws/usermanager/**", "/sockets/usermanager/**");
		dataManager("/ws/datamanager/**", "/sockets/datamanager/**");
		user("/ws/**", "/sockets/**");
		user(matchAll(Names.getUserRoutes()));
		anonymous("/", "/login", "/imprint", "/maintenance", "/search", "/search/**");
		anonymous(CUSTOM_PUBLIC_RESOURCES);
		anonymous(matchAll(WebappModule.STATIC_RESOURCE_DIRECTORIES));
		user("/*"); // group urls
		member("/**"); // repository urls
		log.info("Successfully configured {}", Logs.simpleClassName(this));
	}

	private String[] matchAll(String[] in) {
		List<String> routes = new ArrayList<>();
		for (String route : in)
			routes.add("/" + route + "/**");
		return routes.toArray(new String[routes.size()]);
	}

	@SuppressWarnings("unchecked")
	private void anonymous(String... patterns) {
		filterChains(patterns, ANON);
	}

	@SuppressWarnings("unchecked")
	private void admin(String... patterns) {
		filterChains(patterns, ROLES, ADMIN_USER);
	}

	@SuppressWarnings("unchecked")
	private void userManager(String... patterns) {
		filterChains(patterns, ROLES, USER_MANAGER_USER);
	}

	@SuppressWarnings("unchecked")
	private void dataManager(String... patterns) {
		filterChains(patterns, ROLES, DATA_MANAGER_USER);
	}

	@SuppressWarnings("unchecked")
	private void user(String... patterns) {
		filterChains(patterns, LOGGED_IN_USER);
	}

	@SuppressWarnings("unchecked")
	private void member(String... patterns) {
		filterChains(patterns, REPO_ACCESS);
	}

	@SuppressWarnings("unchecked")
	private void filterChains(String[] patterns, Key<? extends Filter>... keys) {
		for (String pattern : patterns) {
			filterChain(pattern, keys);
		}
	}

	@SuppressWarnings("unchecked")
	private void filterChain(String pattern, Key<? extends Filter>... keys) {
		List<Key<? extends Filter>> allKeys = new ArrayList<>();
		if (pattern.startsWith("/ws/") || pattern.startsWith("/socket/")) {
			allKeys.add(Key.get(MaintenanceModeFilter.class));
		}
		allKeys.addAll(Arrays.asList(keys));
		addFilterChain(pattern, allKeys.toArray(new Key[allKeys.size()]));
	}

	@Provides
	@Singleton
	CacheManager providerCacheManager() {
		return new EhCacheManager();
	}

	@Provides
	@RequestScoped
	public Subject provideSubject() {
		return SecurityUtils.getSubject();
	}

	@Provides
	@SessionScoped
	public CloudSession provideSession() {
		return new CloudSession();
	}

}
