package com.greendelta.collaboration.platform.guice;

import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.openlca.cloud.util.Logs;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.collaboration.platform.guice.util.StartupListener;
import com.greendelta.collaboration.platform.servlet.RequestListener;

public class GuiceConfig extends GuiceServletContextListener {

	private static final Logger log = LogManager.getLogger(GuiceConfig.class);
	private volatile Set<ShutdownListener> shutdownListeners;
	private ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (shutdownListeners != null)
			for (ShutdownListener listener : shutdownListeners)
				listener.shutdown();
		super.contextDestroyed(servletContextEvent);
	}

	@Override
	protected Injector getInjector() {
		Module[] modules = getModules();
		log.info("Creating guice injector with modules {}", Logs.collectClasses(modules));
		Injector injector = Guice.createInjector(modules);
		Injections injected = injector.getInstance(Injections.class);
		this.shutdownListeners = injected.shutdownListeners;
		runStartupListeners(injected.startupListeners);
		servletContext.addListener(RequestListener.getInstance());
		return injector;
	}

	private void runStartupListeners(Set<StartupListener> listeners) {
		if (listeners != null)
			for (StartupListener listener : listeners)
				listener.startup();
	}

	private Module[] getModules() {
		String databasePath = servletContext.getInitParameter("app.database");
		if (databasePath == null || databasePath.isEmpty()) {
			databasePath = System.getProperty("app.database");
		}
		JpaPersistModule jpaModule = new JpaPersistModule("prod");
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:derby:" + databasePath);
		jpaModule.properties(properties);
		String resourcePackages = "com.greendelta.collaboration.webservice";
		return new Module[] { new WebappModule(), new ShiroAopModule(), new ShiroModule(servletContext), jpaModule,
				new JerseyModule(resourcePackages), new EhCacheModule(), new MailModule(), new ElasticSearchModule() };
	}

	private static final class Injections {

		@Inject(optional = true)
		private Set<ShutdownListener> shutdownListeners;

		@Inject(optional = true)
		private Set<StartupListener> startupListeners;

	}

}
