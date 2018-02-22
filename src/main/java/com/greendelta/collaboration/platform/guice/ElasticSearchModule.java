package com.greendelta.collaboration.platform.guice;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;

class ElasticSearchModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	public SearchClient provideSearchClient(@Named("search.cluster") String cluster, @Named("search.host") String host,
			@Named("search.index") String indexName) {
		Builder settingsBuilder = Settings.builder()
				.put("cluster.name", cluster);
		Settings settings = settingsBuilder.build();
		TransportClient client = new PreBuiltTransportClient(settings);
		try {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return new EsClient(client, indexName, "dataset");
	}

}
