package com.greendelta.collaboration.webservice.setup;

import java.io.IOException;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public class Elasticsearch {

	private EmbeddedElastic elastic;

	public void start(Setup setup) throws IOException, InterruptedException {
		try {
			elastic = EmbeddedElastic.builder().withElasticVersion(setup.esVersion)
					.withSetting(PopularProperties.HTTP_PORT, setup.searchPort)
					.withSetting(PopularProperties.CLUSTER_NAME, setup.searchCluster).build();
			elastic.start();
		} catch (IOException | InterruptedException e) {
			elastic = null;
			throw e;
		}
	}

	public void stop() {
		if (elastic != null) {
			elastic.stop();
		}
	}

}
