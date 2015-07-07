package com.greendelta.cloud.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.error.ClientException.ErrorDescriptor;
import com.greendelta.cloud.platform.guice.GuicyTest;

public class SessionResourceTest extends GuicyTest {

	@Inject
	private SessionResource resource;

	@Inject
	private Provider<Subject> subjectProvider;

	@Test
	public void login() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("name", USER);
		formData.put("password", PASS);
		resource.login(formData);
		Subject subject = SecurityUtils.getSubject();
		Assert.assertEquals(true, subject.isAuthenticated());
		Assert.assertEquals(USER, subject.getPrincipal());
	}

	@Test(expected = WebApplicationException.class)
	public void loginNotExistingUser() {
		try {
			Map<String, Object> formData = new HashMap<>();
			formData.put("name", "not_existing");
			formData.put("password", "12345");
			resource.login(formData);
		} catch (WebApplicationException e) {
			Assert.assertEquals(ErrorDescriptor.class, e.getResponse().getEntity().getClass());
			ErrorDescriptor error = (ErrorDescriptor) e.getResponse().getEntity();
			Assert.assertEquals("Invalid credentials", error.getData());
			throw e;
		}
	}

	@Test(expected = WebApplicationException.class)
	public void loginWrongPassword() {
		try {
			Map<String, Object> formData = new HashMap<>();
			formData.put("name", USER);
			formData.put("password", "54321");
			resource.login(formData);
		} catch (WebApplicationException e) {
			Assert.assertEquals(ErrorDescriptor.class, e.getResponse().getEntity().getClass());
			ErrorDescriptor error = (ErrorDescriptor) e.getResponse().getEntity();
			Assert.assertEquals("Invalid credentials", error.getData());
			throw e;
		}
	}

	@Test
	public void logout() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("name", USER);
		formData.put("password", PASS);
		resource.login(formData);
		resource.logout();
		Subject subject = subjectProvider.get();
		Assert.assertEquals(false, subject.isAuthenticated());
		Assert.assertNull(subject.getPrincipal());
	}

}
