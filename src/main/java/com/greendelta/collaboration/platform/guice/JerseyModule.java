package com.greendelta.collaboration.platform.guice;

import java.util.Map;

import javax.ws.rs.Produces;

import org.openlca.cloud.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

class JerseyModule extends JerseyServletModule {

	private static final Logger log = LoggerFactory.getLogger(JerseyModule.class);

	private String resourcePackages;

	public JerseyModule(String resourcePackages) {
		this.resourcePackages = resourcePackages;
	}

	@Override
	protected void configureServlets() {
		Map<String, String> guiceJerseyParameter = ImmutableMap.of(PackagesResourceConfig.PROPERTY_PACKAGES,
				resourcePackages);
		serve("/ws/*").with(GuiceContainer.class, guiceJerseyParameter);
		log.debug("Successfully configured {}", Logs.simpleClassName(this));
	}

	@Provides
	@Singleton
	public ObjectMapper provideObjectMapper() {
		return new ObjectMapper();
	}

	@Provides
	@Singleton
	@Produces
	public JacksonJsonProvider createJacksonJsonProvider(ObjectMapper objectMapper) {
		return new JacksonJsonProvider(objectMapper);
	}
}