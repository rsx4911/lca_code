package com.greendelta.collaboration.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.openlca.cloud.api.CredentialSupplier;
import org.openlca.cloud.api.RepositoryClient;
import org.openlca.cloud.api.RepositoryConfig;
import org.openlca.cloud.util.WebRequests.WebRequestException;

public class RepositoryMigrator {

	private final RepositoryService service;
	private final IndexService indexService;

	public RepositoryMigrator(RepositoryService service, IndexService indexService) {
		this.service = service;
		this.indexService = indexService;
	}

	public void migrate(String url, Repository repo, String username, String password)
			throws MalformedURLException, IOException, WebRequestException {
		String repoId = url.substring(url.lastIndexOf("/") + 1);
		url = url.substring(0, url.lastIndexOf("/"));
		repoId = url.substring(url.lastIndexOf("/") + 1) + '/' + repoId;
		url = url.substring(0, url.lastIndexOf("/")) + "/ws";
		RepositoryConfig config = new RepositoryConfig(null, url, repoId, new CredentialSupplier(username, password));
		RepositoryClient client = new RepositoryClient(config);
		try {
			InputStream stream = client.export();
			if (stream == null)
				return;
			service.unpack(repo, stream);
			indexService.index(repo);
		} finally {
			client.logout();
		}
	}

}
