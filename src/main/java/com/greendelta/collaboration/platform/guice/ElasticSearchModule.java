package com.greendelta.collaboration.platform.guice;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;

class ElasticSearchModule extends AbstractModule {

	@Override
	protected void configure() {
		BindUtils.multibind(binder(), ShutdownListener.class, ElasticSearchShutdownListener.class);
	}

	@Provides
	@Singleton
	public Client provideClient(@Named("search.cluster") String cluster, @Named("search.host") String host) {
		Builder settingsBuilder = Settings.builder()
				.put("cluster.name", cluster);
		Settings settings = settingsBuilder.build();
		TransportClient client = new PreBuiltTransportClient(settings);
		try {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}

	@Provides
	@Singleton
	public SearchClient provideSearchClient(Client client, @Named("search.index") String indexName) {
		return new EsClient(client, indexName, "dataset");
	}

	private static class ElasticSearchShutdownListener implements ShutdownListener {

		@Inject
		private Client client;

		@Override
		public void shutdown() {
			client.close();
		}

	}

}
