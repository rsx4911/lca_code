package com.greendelta.collaboration.platform.guice;

import net.sf.ehcache.CacheManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openlca.cloud.util.Logs;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;

class EhCacheModule extends AbstractModule {

	private static final Logger log = LogManager.getLogger(EhCacheModule.class);

	@Override
	protected void configure() {
		BindUtils.multibind(binder(), ShutdownListener.class, CacheManagerShutdownListener.class);
		log.info("Successfully configured {}", Logs.simpleClassName(this));
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
			log.info("Shutting down cache manager");
			cacheManager.shutdown();
			log.debug("Shut down cache manager");
		}

	}

}
