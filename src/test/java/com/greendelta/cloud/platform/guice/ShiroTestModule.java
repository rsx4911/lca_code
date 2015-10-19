package com.greendelta.cloud.platform.guice;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.openlca.cloud.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.greendelta.cloud.platform.shiro.JpaRealm;

public class ShiroTestModule extends AbstractModule {

	private static final Logger log = LoggerFactory.getLogger(ShiroTestModule.class);

	@Override
	protected void configure() {
		bind(Realm.class).to(JpaRealm.class).in(Singleton.class);
		log.debug("Successfully configured {}", Logs.simpleClassName(this));
	}

	@Provides
	@Singleton
	WebSecurityManager provideSecurityManager(Realm realm, SessionManager sessionManager, CacheManager cacheManager,
			RememberMeManager rememberMeManager) {
		DefaultWebSecurityManager result = new DefaultWebSecurityManager();
		result.setSessionManager(sessionManager);
		result.setRememberMeManager(rememberMeManager);
		result.setCacheManager(cacheManager);
		result.setRealm(realm);
		return result;
	}

	@Provides
	@Singleton
	SessionManager provideSessionManager(CacheManager cacheManager) {
		DefaultWebSessionManager manager = new DefaultWebSessionManager();
		manager.setSessionDAO(new EnterpriseCacheSessionDAO());
		manager.setCacheManager(cacheManager);
		return manager;
	}

	@Provides
	@Singleton
	CacheManager providerCacheManager() {
		return new EhCacheManager();
	}

	@Provides
	@Singleton
	RememberMeManager provideRememberMeManager() {
		return new CookieRememberMeManager();
	}

	@Provides
	public Subject provideSubject() {
		return SecurityUtils.getSubject();
	}

}
