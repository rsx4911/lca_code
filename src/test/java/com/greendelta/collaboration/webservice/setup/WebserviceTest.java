package com.greendelta.collaboration.webservice.setup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.NewCookie;

import org.apache.catalina.LifecycleException;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.openlca.cloud.api.CredentialSupplier;
import org.openlca.cloud.api.RepositoryClient;
import org.openlca.cloud.api.RepositoryConfig;
import org.openlca.cloud.util.WebRequests;
import org.openlca.cloud.util.WebRequests.Type;
import org.openlca.cloud.util.WebRequests.WebRequestException;
import org.openlca.core.database.IDatabase;

import com.sun.jersey.api.client.ClientResponse;

public class WebserviceTest {

	private static final String REPOSITORY_ID = "admin/test";
	private TestServer server;
	private Setup setup;

	@Before
	public void startServer() throws IOException, InterruptedException, MavenInvocationException, LifecycleException,
			WebRequestException {
		Assume.assumeTrue("true".equals(System.getProperty("collab.integrationTests")));
		setup = new Setup();
		server = new TestServer(setup);
		server.start();
		String sessionId = login();
		createRepository(sessionId);
	}

	private String login() throws WebRequestException {
		Map<String, String> data = new HashMap<>();
		data.put("username", "admin");
		data.put("password", setup.adminPassword);
		ClientResponse response = WebRequests.call(Type.POST, setup.getUrl("public/login"), null, data);
		for (NewCookie cookie : response.getCookies())
			if (cookie.getName().equals("JSESSIONID"))
				return cookie.getValue();
		return null;
	}

	private void createRepository(String sessionId) throws WebRequestException {
		WebRequests.call(Type.POST, setup.getUrl("repository/" + REPOSITORY_ID), sessionId);
	}

	protected RepositoryClient getClient(IDatabase database) {
		RepositoryConfig config = RepositoryConfig.connect(database, setup.getBaseUrl(), REPOSITORY_ID,
				new CredentialSupplier("admin", setup.adminPassword));
		return new RepositoryClient(config);
	}

	@After
	public void stopServer() throws LifecycleException {
		if (server != null) {
			server.stop();
		}
	}

}
