package com.greendelta.collaboration.platform.guice;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openlca.cloud.util.Logs;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

class PropertiesModule extends AbstractModule {

	private static final Logger log = LogManager.getLogger(PropertiesModule.class);
	private static Properties properties;
	private static String environment;

	@Override
	protected void configure() {
		Names.bindProperties(binder(), getProperties());
		log.info("Successfully configured {}", Logs.simpleClassName(this));
	}

	static void setEnvironment(String environment) {
		if (PropertiesModule.environment != null)
			throw new IllegalStateException("Cannot change environment, was already set");
		PropertiesModule.environment = environment;
	}

	static Properties getProperties() {
		if (properties != null)
			return properties;
		properties = load("app.properties");
		if (environment == null)
			return properties;
		Properties environmentSpecific = load("app." + environment + ".properties");
		for (String key : environmentSpecific.stringPropertyNames())
			properties.put(key, environmentSpecific.getProperty(key));
		return properties;
	}

	private static Properties load(String resource) {
		Properties properties = new Properties();
		URL url = getResource(resource);
		if (url == null)
			return properties;
		try {
			properties.load(url.openStream());
		} catch (IOException e) {
			log.error("Error loading app.properties", e);
		}
		return properties;
	}

	private static URL getResource(String resource) {
		try {
			URL url = Resources.getResource(resource);
			return url;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

}
