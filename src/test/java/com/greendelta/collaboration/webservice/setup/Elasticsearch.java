package com.greendelta.collaboration.webservice.setup;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public class Elasticsearch {

	private EmbeddedElastic elastic;

	public void start(Setup setup) throws Exception {
		try {
			elastic = EmbeddedElastic.builder().withElasticVersion(setup.esVersion)
					.withSetting(PopularProperties.HTTP_PORT, setup.searchPort)
					.withSetting(PopularProperties.CLUSTER_NAME, setup.searchCluster).build();
			elastic.start();
		} catch (Exception e) {
			elastic = null;
			throw e;
		}
	}

	public void stop() throws Exception {
		if (elastic != null) {
			elastic.stop();
		}
	}

}
