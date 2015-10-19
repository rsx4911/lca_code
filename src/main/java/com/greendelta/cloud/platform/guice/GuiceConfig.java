package com.greendelta.cloud.platform.guice;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.guice.aop.ShiroAopModule;
import org.openlca.cloud.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.greendelta.cloud.platform.guice.util.ShutdownListener;
import com.greendelta.cloud.platform.guice.util.StartupListener;

public class GuiceConfig extends GuiceServletContextListener {

	private static final Logger log = LoggerFactory
			.getLogger(GuiceConfig.class);
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
		log.debug("Creating guice injector with modules {}",
				Logs.collectClasses(modules));
		Injector injector = Guice.createInjector(modules);
		Injections injected = injector.getInstance(Injections.class);
		this.shutdownListeners = injected.shutdownListeners;
		runStartupListeners(injected.startupListeners);
		return injector;
	}

	private void runStartupListeners(Set<StartupListener> listeners) {
		if (listeners != null)
			for (StartupListener listener : listeners)
				listener.startup();
	}

	private Module[] getModules() {
		String env = System.getProperty("app.env");
		if (!Strings.isNullOrEmpty(env))
			PropertiesModule.setEnvironment(env);
		String resourcePackages = PropertiesModule.getProperties().getProperty("jersey.resource.packages");
		String persistenceUnit = PropertiesModule.getProperties().getProperty("persistence.unit");
		String databasePath = PropertiesModule.getProperties().getProperty("database.path");
		JpaPersistModule jpaModule = new JpaPersistModule(persistenceUnit);
		Properties properties = new Properties();
		if (!new File(databasePath).exists()) {
			properties.setProperty("javax.persistence.jdbc.url", "jdbc:derby:" + databasePath + ";create=true");
			properties.setProperty("eclipselink.ddl-generation", "drop-and-create-tables");
			properties.setProperty("eclipselink.ddl-generation.output-mode", "database");
		} else
			properties.setProperty("javax.persistence.jdbc.url", "jdbc:derby:" + databasePath);
		jpaModule.properties(properties);
		return new Module[] { new WebappModule(), new ShiroAopModule(), new ShiroModule(servletContext),
				jpaModule, new JerseyModule(resourcePackages), new EhCacheModule(),
				new PropertiesModule() };
	}

	private static final class Injections {

		@Inject(optional = true)
		private Set<ShutdownListener> shutdownListeners;

		@Inject(optional = true)
		private Set<StartupListener> startupListeners;

	}

}
