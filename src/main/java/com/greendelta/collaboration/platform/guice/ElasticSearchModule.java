package com.greendelta.collaboration.platform.guice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.logging.log4j.core.util.IOUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.collaboration.platform.guice.util.StartupListener;
import com.greendelta.collaboration.search.SearchClient;
import com.greendelta.collaboration.search.elasticsearch.EsClient;
import com.greendelta.collaboration.service.RepositoryUpgrades;
import com.greendelta.collaboration.service.SearchService;

class ElasticSearchModule extends AbstractModule {

	@Override
	protected void configure() {
		BindUtils.multibind(binder(), StartupListener.class, NodeStartupListener.class);
		BindUtils.multibind(binder(), ShutdownListener.class, NodeShutdownListener.class);
	}

	@Provides
	@Singleton
	public Node provideNode() {
		String home = PropertiesModule.getProperties().getProperty("search.path");
		Builder settingsBuilder = Settings.builder()
				.put("http.enabled", "false")
				.put("transport.type", "local")
				.put("path.home", home);
		Settings settings = settingsBuilder.build();
		return new Node(settings);
	}

	@Provides
	@Singleton
	public Client provideClient(Node node) {
		return node.client();
	}

	@Provides
	@Singleton
	public SearchClient provideSearchClient(Client client) {
		String settings = getResource("elasticsearch-settings.json");
		String mapping = getResource("elasticsearch-mapping.json");
		return new EsClient(client, settings, mapping);
	}

	private String getResource(String name) {
		InputStream stream = getClass().getResourceAsStream(name);
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(new InputStreamReader(stream), writer);
		} catch (IOException e) {
			return "{}";
		}
		return writer.toString();
	}

	private static class NodeStartupListener implements StartupListener {

		@Inject
		private SearchService searchService;

		@Inject
		private Node node;

		@Inject
		@Named("search.path")
		private String searchPath;

		@Override
		public void startup() {
			try {
				node.start();
			} catch (NodeValidationException e) {
				e.printStackTrace();
			}
			searchService.initializeIndex();
			String repoPath = PropertiesModule.getProperties().getProperty("repository.path");
			RepositoryUpgrades.upgrade(repoPath, searchService);
		}
	}

	private static class NodeShutdownListener implements ShutdownListener {

		@Inject
		private Node node;

		@Override
		public void shutdown() {
			try {
				node.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
