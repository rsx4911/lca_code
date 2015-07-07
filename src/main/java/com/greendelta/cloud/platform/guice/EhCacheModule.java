package com.greendelta.cloud.platform.guice;

import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.greendelta.cloud.platform.guice.util.BindUtils;
import com.greendelta.cloud.platform.guice.util.ShutdownListener;
import com.greendelta.cloud.util.Logs;

class EhCacheModule extends AbstractModule {

	private static final Logger log = LoggerFactory.getLogger(EhCacheModule.class);

	@Override
	protected void configure() {
		BindUtils.multibind(binder(), ShutdownListener.class, CacheManagerShutdownListener.class);
		log.debug("Successfully configured {}", Logs.simpleClassName(this));
	}

	@Provides
	@Singleton
	public CacheManager provideCacheManager() {
		CacheManager cacheManager = CacheManager.getCacheManager("app-cache");
		if (cacheManager == null)
			cacheManager = CacheManager.create(Resources.getResource("ehcache.xml"));
		return cacheManager;
	}

	private static class CacheManagerShutdownListener implements ShutdownListener {

		@Inject
		private CacheManager cacheManager;

		@Override
		public void shutdown() {
			log.debug("Shutting down cache manager");
			cacheManager.shutdown();
			log.debug("Shut down cache manager");
		}

	}

}
