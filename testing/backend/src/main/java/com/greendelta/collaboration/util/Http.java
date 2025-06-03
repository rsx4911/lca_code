package com.greendelta.collaboration.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;

import com.greendelta.collaboration.controller.util.Response;

public class Http {

	public static CloseableHttpResponse execute(CloseableHttpClient client, HttpUriRequest request) throws IOException {
		var response = client.execute(request);
		var code = response.getStatusLine().getStatusCode();
		if (code >= 400) {
			var message = getString(response);
			throw Response.status(HttpStatus.valueOf(code), message);
		}
		return response;
	}

	public static String getString(CloseableHttpResponse response) throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

}
