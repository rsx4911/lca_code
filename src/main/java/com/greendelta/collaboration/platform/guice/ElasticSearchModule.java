package com.greendelta.collaboration.platform.guice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.openlca.core.model.ModelType;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.collaboration.platform.guice.util.StartupListener;
import com.greendelta.collaboration.service.RepositoryUpgrades;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.Resources;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;
import com.greendelta.search.wrapper.es.EsSettings;

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
		return new EsClient(client);
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
			Map<String, Object> settings = new HashMap<>();
			settings.put(EsSettings.CONFIG, Resources.get(getClass(), "es-settings.json"));
			Map<String, String> mappings = new HashMap<>();
			for (ModelType type : ModelTypes.SORTED) {
				String typeName = type.name().toLowerCase();
				String mapping = new EsMapping(typeName).build();
				mappings.put(typeName, mapping);
			}
			mappings.put(ModelType.CATEGORY.name().toLowerCase(),
					new EsMapping(ModelType.CATEGORY.name().toLowerCase()).build());
			settings.put(EsSettings.MAPPINGS, mappings);
			searchService.createIndex(settings);
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
