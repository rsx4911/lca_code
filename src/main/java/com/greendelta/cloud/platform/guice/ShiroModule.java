package com.greendelta.cloud.platform.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.greendelta.cloud.platform.shiro.JpaRealm;
import com.greendelta.cloud.util.Logs;

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
		// addFilterChain("/ws/public/**", ANON);
		// addFilterChain("/**", USER);
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

}
