package com.greendelta.cloud.platform.guice;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.derby.jdbc.EmbeddedDriver;
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

	private static final Logger log = LoggerFactory.getLogger(GuiceConfig.class);
	private volatile Set<ShutdownListener> shutdownListeners;
	private ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		String databasePath = PropertiesModule.getProperties().getProperty("database.path");
		if (shutdownListeners != null)
			for (ShutdownListener listener : shutdownListeners)
				listener.shutdown();
		unlockDatabase(databasePath);
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
		String repositoriesPath = PropertiesModule.getProperties().getProperty("repository.path");
		String librariesPath = PropertiesModule.getProperties().getProperty("library.path");
		JpaPersistModule jpaModule = new JpaPersistModule(persistenceUnit);
		Properties properties = new Properties();
		checkAndCreateDirectories(repositoriesPath);
		checkAndCreateDirectories(librariesPath);
		if (!new File(databasePath).exists()) {
			checkAndCreateDirectories(new File(databasePath).getParent());
			createDatabase(databasePath);
			new File(repositoriesPath, "admin").mkdir();
		}
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:derby:" + databasePath);
		jpaModule.properties(properties);
		return new Module[] { new WebappModule(), new ShiroAopModule(),
				new ShiroModule(servletContext), jpaModule,
				new JerseyModule(resourcePackages), new EhCacheModule(),
				new PropertiesModule(), new MailModule() };
	}

	private void checkAndCreateDirectories(String path) {
		if (!new File(path).exists())
			new File(path).mkdirs();
	}

	private void createDatabase(String databasePath) {
		log.info("Creating new database");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(
				"database.sql")))) {
			DriverManager.registerDriver(new EmbeddedDriver());
			DriverManager.getConnection("jdbc:derby:" + databasePath + ";create=true").close();
			String line = null;
			while ((line = reader.readLine()) != null)
				update("jdbc:derby:" + databasePath, line);
			unlockDatabase(databasePath);
		} catch (Exception e) {
			log.error("Error creating inital database", e);
		}
	}

	private void update(String url, String query) throws SQLException {
		log.info(query);
		try (Connection con = DriverManager.getConnection(url)) {
			try (Statement s = con.createStatement()) {
				s.executeUpdate(query);
			}
		}
	}

	private void unlockDatabase(String databasePath) {
		try {
			DriverManager.getConnection("jdbc:derby:" + databasePath + ";shutdown=true");
		} catch (SQLException e) {
			// Derby 10.9.1.0 shutdown raises a SQLException with code "XJ015"
			if (!"XJ015".equals(e.getSQLState())) {
				log.error("Error shutting down database", e);
			}
		}
	}

	private static final class Injections {

		@Inject(optional = true)
		private Set<ShutdownListener> shutdownListeners;

		@Inject(optional = true)
		private Set<StartupListener> startupListeners;

	}

}
