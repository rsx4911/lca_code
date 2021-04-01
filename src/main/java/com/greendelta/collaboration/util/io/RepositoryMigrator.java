package com.greendelta.collaboration.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.openlca.cloud.api.CredentialSupplier;
import org.openlca.cloud.api.RepositoryClient;
import org.openlca.cloud.api.RepositoryConfig;
import org.openlca.cloud.util.WebRequests.WebRequestException;

import com.greendelta.collaboration.service.IndexService;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;

public class RepositoryMigrator {

	private final RepositoryService service;
	private final IndexService indexService;

	public RepositoryMigrator(RepositoryService service, IndexService indexService) {
		this.service = service;
		this.indexService = indexService;
	}

	public MigrateResponse migrate(String url, Repository repo, String username, String password, Integer token)
			throws MalformedURLException, IOException, WebRequestException {
		String repoId = url.substring(url.lastIndexOf("/") + 1);
		url = url.substring(0, url.lastIndexOf("/"));
		repoId = url.substring(url.lastIndexOf("/") + 1) + '/' + repoId;
		url = url.substring(0, url.lastIndexOf("/")) + "/ws";
		RepositoryConfig config = new RepositoryConfig(null, url, repoId);
		config.credentials = new CredentialSupplier(username, password);
		config.credentials.setTokenSupplier(() -> {
			if (token != null && token != 0)
				return token;
			throw new TokenRequiredException();
		});
		RepositoryClient client = new RepositoryClient(config);
		try {
			InputStream stream = client.export();
			if (stream == null)
				return MigrateResponse.NO_CONTENT;
			service.unpack(repo, stream);
			indexService.index(repo);
			return MigrateResponse.SUCCESS;
		} catch (TokenRequiredException e) {
			return MigrateResponse.TOKEN_REQUIRED;
		} finally {
			client.logout();
		}
	}

	public static enum MigrateResponse {
		
		SUCCESS,
		
		NO_CONTENT,
		
		TOKEN_REQUIRED;
		
	}
	
	private class TokenRequiredException extends IllegalStateException {

		private static final long serialVersionUID = 7824693893433376344L;
		
	}
	
}
