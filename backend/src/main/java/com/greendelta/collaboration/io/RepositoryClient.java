package com.greendelta.collaboration.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openlca.util.Strings;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.util.Http;

public class RepositoryClient implements AutoCloseable {

	private final String baseUrl;
	private final String username;
	private final String password;
	private final CloseableHttpClient client;
	private final CookieStore cookieStore;

	public RepositoryClient(String baseUrl, String username, String password) throws IOException {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.cookieStore = new BasicCookieStore();
		this.client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
	}

	public void exportRepository(String repository, InputStreamConsumer consumer) throws IOException {
		login();
		try (var response = Http.execute(client, new HttpGet(baseUrl + "repository/export/" + repository))) {
			consumer.accept(response.getEntity().getContent());
		}
		logout();
	}

	private void login() throws IOException {
		if (Strings.nullOrEmpty(username) || Strings.nullOrEmpty(password))
			return;
		var post = new HttpPost(baseUrl + "public/login");
		var data = """
				{"username": "%s", "password": "%s"}
				""".formatted(username, password);
		post.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));
		try (var response = Http.execute(client, post)) {
			var result = Http.getString(response);
			if ("tokenRequired".equals(result)) {
				var message = """
						{"field": "%s", "message": "%s"}
						""".formatted("token", "Token required");
				throw Response.badRequest(message);
			}
			var header = response.getFirstHeader("Set-Cookie");
			if (header == null)
				return;
			var value = header.getValue();
			if (!value.contains("JSESSIONID"))
				return;
			var index = value.indexOf("JSESSIONID=");
			var endIndex = value.indexOf(";", index);
			var sessionId = value.substring(index + "JSESSIONID=".length(), endIndex);
			var url = URI.create(baseUrl);
			var cookie = new BasicClientCookie("JSESSIONID", sessionId);
			cookie.setDomain(url.getHost());
			cookie.setPath(url.getPath());
			cookieStore.addCookie(cookie);
		}
	}

	private void logout() throws IOException {
		if (cookieStore.getCookies().isEmpty())
			return;
		try (var response = Http.execute(client, new HttpPost(baseUrl + "public/logout"))) {
			cookieStore.clear();
		}
	}

	@Override
	public void close() throws IOException {
		client.close();
	}

	public interface InputStreamConsumer {

		public void accept(InputStream stream) throws IOException;

	}

}
