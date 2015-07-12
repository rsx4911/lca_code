package com.greendelta.cloud.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.platform.guice.GuicyTest;

public class SessionResourceTest extends GuicyTest {

	@Inject
	private SessionResource resource;

	@Inject
	private Provider<Subject> subjectProvider;

	@Test
	public void login() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("username", USER);
		formData.put("password", PASS);
		resource.login(formData);
		Subject subject = SecurityUtils.getSubject();
		Assert.assertEquals(true, subject.isAuthenticated());
		Assert.assertEquals(USER, subject.getPrincipal());
	}

	@Test
	public void loginNotExistingUser() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("username", "not_existing");
		formData.put("password", "12345");
		Response response = resource.login(formData);
		Assert.assertEquals(401, response.getStatus());
	}

	@Test
	public void loginWrongPassword() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("username", USER);
		formData.put("password", "54321");
		Response response = resource.login(formData);
		Assert.assertEquals(401, response.getStatus());
	}

	@Test
	public void logout() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("username", USER);
		formData.put("password", PASS);
		resource.login(formData);
		resource.logout();
		Subject subject = subjectProvider.get();
		Assert.assertEquals(false, subject.isAuthenticated());
		Assert.assertNull(subject.getPrincipal());
	}

}
