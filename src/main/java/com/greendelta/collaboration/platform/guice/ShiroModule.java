package com.greendelta.collaboration.platform.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.subject.Subject;
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

class ShiroModule extends ShiroWebModule {

	private static final Logger log = LoggerFactory.getLogger(ShiroModule.class);

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
		addFilterChain("/ws/admin/**", ROLES, config(ROLES, "admin"));
		addFilterChain("/sockets/admin/**", ROLES, config(ROLES, "admin"));
		addFilterChain("/login", ANON);
		addFilterChain("/imprint", ANON);
		addFilterChain("/public/**", ANON);
		addFilterChain("/ws/public/**", ANON);
		addFilterChain("/sockets/public/**", ANON);
		for (String sr : WebappModule.STATIC_RESOURCES)
			if (sr.endsWith("/"))
				addFilterChain("/" + sr + "/**", ANON);
			else
				addFilterChain("/" + sr, ANON);
		addFilterChain("/**", Key.get(AuthenticationFilter.class));
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
