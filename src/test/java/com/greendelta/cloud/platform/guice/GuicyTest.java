package com.greendelta.cloud.platform.guice;

import java.io.File;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.junit.After;
import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.greendelta.cloud.platform.guice.util.StartupListener;
import com.greendelta.cloud.service.UserService;

public abstract class GuicyTest {

	protected static final String USER = "testuser1";
	protected static final String PASS = "12345sechs";
	private static Injector injector;
	private static Injected injected;
	private long userId;

	private static class Injected {
		@Inject(optional = true)
		private Set<StartupListener> startupListeners;

		@Inject
		private Provider<Subject> subjectProvider;

		@Inject
		private UserService userService;

	}

	static {
		PropertiesModule.setEnvironment("test");
		String filestorePath = PropertiesModule.getProperties().getProperty("filestore.path");
		if (filestorePath != null)
			new File(filestorePath).delete();
		injector = Guice.createInjector(getModules());
		injector.getInstance(PersistService.class).start();
		SecurityUtils.setSecurityManager(injector.getInstance(WebSecurityManager.class));
		injected = new Injected();
		injector.injectMembers(injected);
		Injected listeners = new Injected();
		injector.injectMembers(listeners);
		if (listeners.startupListeners != null)
			for (StartupListener listener : listeners.startupListeners)
				listener.startup();
	}

	@Before
	public void before() {
		injector.injectMembers(this);
		userId = injected.userService.createNewUser(USER, PASS).getId();
	}

	@After
	public void after() {
		Subject subject = injected.subjectProvider.get();
		if (subject.isAuthenticated())
			subject.logout();
		injected.userService.delete(userId);
	}

	private static Module[] getModules() {
		String resourcePackages = PropertiesModule.getProperties().getProperty("jersey.resource.packages");
		String persistenceUnit = PropertiesModule.getProperties().getProperty("persistence.unit");
		return new Module[] { new WebappModule(), new ShiroAopModule(), new ShiroTestModule(),
				new JpaPersistModule(persistenceUnit), new JerseyModule(resourcePackages), new EhCacheModule(),
				new PropertiesModule() };
	}

}
