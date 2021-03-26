package com.greendelta.collaboration.platform.guice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.openlca.cloud.util.Logs;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.greendelta.collaboration.platform.servlet.DefaultServlet;
import com.greendelta.collaboration.platform.servlet.NoCacheFilter;
import com.greendelta.collaboration.platform.servlet.WsApiFilter;

import websocket.WebsocketConfigurator;

public class WebappModule extends ServletModule {

	public static final String[] STATIC_RESOURCE_DIRECTORIES = { "css", "images", "fonts", "js", "graph" };
	private static final Logger log = LogManager.getLogger(WebappModule.class);

	@Override
	protected void configureServlets() {
		bind(NoCacheFilter.class).in(Singleton.class);
		bind(WsApiFilter.class).in(Singleton.class);
		bind(ShiroFilter.class).in(Singleton.class);
		requestStaticInjection(WebsocketConfigurator.class);
		filter("/ws/*", "/sockets/*").through(PersistFilter.class);
		filter("/ws/*").through(WsApiFilter.class);
		filter("/*").through(NoCacheFilter.class); // Filter decides
		filter("/*").through(ShiroFilter.class);
		configureNonStaticResources();
		log.info("Successfully configured {}", Logs.simpleClassName(this));
	}

	private void configureNonStaticResources() {
		String statics = "";
		for (String sr : STATIC_RESOURCE_DIRECTORIES) {
			statics += (statics.isEmpty() ? sr : "|" + sr) + "/";
		}
		String webservices = "ws/|sockets/";
		String webapp = "^/(?!" + statics + "|" + webservices + "|[^/]+[.]html).*";
		serveRegex(webapp).with(DefaultServlet.class);
	}

}
