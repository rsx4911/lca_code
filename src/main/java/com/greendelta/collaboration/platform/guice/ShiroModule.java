package com.greendelta.collaboration.platform.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.openlca.cloud.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.SessionScoped;
import com.greendelta.collaboration.platform.guice.util.CloudSession;
import com.greendelta.collaboration.platform.shiro.AuthenticationFilter;
import com.greendelta.collaboration.platform.shiro.JpaRealm;
import com.greendelta.collaboration.util.Names;

class ShiroModule extends ShiroWebModule {

	private static final Logger log = LoggerFactory.getLogger(ShiroModule.class);
	private static final Key<RolesAuthorizationFilter> ADMIN_USER = config(ROLES, "admin");
	private static final Key<AuthenticationFilter> LOGGED_IN_USER = Key.get(AuthenticationFilter.class);

	ShiroModule(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		bindRealm().to(JpaRealm.class).in(Singleton.class);
		bind(JpaRealm.class);
		expose(JpaRealm.class);
		expose(Subject.class);
		expose(CloudSession.class);
		addFilterChain("/ws/public/**", ANON);
		addFilterChain("/ws/admin/**", ROLES, ADMIN_USER);
		addFilterChain("/ws/**", LOGGED_IN_USER);
		addFilterChain("/sockets/public/**", ANON);
		addFilterChain("/sockets/admin/**", ROLES, ADMIN_USER);
		addFilterChain("/sockets/**", LOGGED_IN_USER);
		for (String userRoute : Names.getUserRoutes())
			addFilterChain("/" + userRoute + "/**", LOGGED_IN_USER);
		addFilterChain("/", LOGGED_IN_USER);		
		addFilterChain("/**", ANON);
		log.debug("Successfully configured {}", Logs.simpleClassName(this));
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
