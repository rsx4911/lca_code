package com.greendelta.cloud.platform.guice;

import org.apache.shiro.web.servlet.ShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;
import com.greendelta.cloud.util.Logs;

class WebappModule extends ServletModule {

	private static final Logger log = LoggerFactory.getLogger(WebappModule.class);

	@Override
	protected void configureServlets() {
		filter("/ws/*").through(PersistFilter.class);
		bind(ShiroFilter.class).in(Singleton.class);
		filter("/*").through(ShiroFilter.class);
		log.debug("Successfully configured {}", Logs.simpleClassName(this));
	}

}
