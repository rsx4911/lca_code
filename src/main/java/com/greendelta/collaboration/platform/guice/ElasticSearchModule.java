package com.greendelta.collaboration.platform.guice;

import java.io.File;
import java.io.IOException;

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
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;

class ElasticSearchModule extends AbstractModule {

	@Override
	protected void configure() {
		BindUtils.multibind(binder(), StartupListener.class, NodeStartupListener.class);
		BindUtils.multibind(binder(), ShutdownListener.class, NodeShutdownListener.class);
	}

	@Provides
	@Singleton
	public Node provideNode(@Named("search.path") String home) {
		if (!new File(home).exists() || new File(home).listFiles().length == 0)
			throw new IllegalArgumentException("Search home not initialized, did you run the installer?");	
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
		private Node node;

		@Override
		public void startup() {
			try {
				node.start();
			} catch (NodeValidationException e) {
				e.printStackTrace();
			}
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
