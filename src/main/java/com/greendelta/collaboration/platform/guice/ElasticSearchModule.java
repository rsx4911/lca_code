package com.greendelta.collaboration.platform.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.collaboration.service.SettingsService;

class ElasticSearchModule extends AbstractModule {

	@Override
	protected void configure() {
		BindUtils.multibind(binder(), ShutdownListener.class, ElasticSearchShutdownListener.class);
	}

	private static class ElasticSearchShutdownListener implements ShutdownListener {

		@Inject
		private SettingsService settingsService;

		@Override
		public void shutdown() {
			settingsService.getSearchConfig().close();
		}

	}

}
