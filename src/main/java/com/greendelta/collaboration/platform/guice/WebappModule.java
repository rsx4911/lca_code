package com.greendelta.collaboration.platform.guice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.openlca.cloud.util.Logs;

import websocket.WebsocketConfigurator;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.greendelta.collaboration.platform.servlet.DefaultServlet;

class WebappModule extends ServletModule {

	static final String[] STATIC_RESOURCES = { "css/", "images/", "fonts/", "js/", "graph/" };
	private static final Logger log = LogManager.getLogger(WebappModule.class);

	@Override
	protected void configureServlets() {
		filter("/ws/*", "/sockets/*").through(PersistFilter.class);
		requestStaticInjection(WebsocketConfigurator.class);
		configureNonStaticResources();
		bind(ShiroFilter.class).in(Singleton.class);
		filter("/*").through(ShiroFilter.class);
		log.info("Successfully configured {}", Logs.simpleClassName(this));
	}

	private void configureNonStaticResources() {
		String statics = null;
		for (String sr : STATIC_RESOURCES)
			if (statics == null)
				statics = sr;
			else
				statics += "|" + sr;
		String webservices = "ws/|sockets/";
		String webapp = "^/(?!" + statics + "|" + webservices + "|[^/]+[.]html).*";
		serveRegex(webapp).with(DefaultServlet.class);
	}

}
