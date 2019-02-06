package com.greendelta.collaboration.webservice.setup;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.catalina.LifecycleException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class Tomcat {

	private org.apache.catalina.startup.Tomcat tomcat;
	private String dbValue;

	public void start(Setup setup) throws MavenInvocationException, LifecycleException {
		File warFile = buildWar();
		dbValue = System.getProperty("app.database");
		System.setProperty("app.database", setup.dbDir.getAbsolutePath());
		try {
			tomcat = new org.apache.catalina.startup.Tomcat();
			tomcat.setPort(setup.tomcatPort);
			tomcat.setBaseDir(setup.tomcatDir.getAbsolutePath());
			tomcat.getHost().setAppBase(setup.tomcatDir.getAbsolutePath());
			tomcat.getHost().setAutoDeploy(true);
			tomcat.getHost().setDeployOnStartup(true);
			tomcat.start();
		} catch (LifecycleException e) {
			tomcat = null;
			throw e;
		}
		tomcat.addWebapp(tomcat.getHost(), "", warFile.getAbsolutePath());
	}

	private File buildWar() throws MavenInvocationException {
		File warFile = new File(new File("").getAbsolutePath(), "target/lca-collaboration.war");
		if (!"true".equals(System.getProperty("collab.build")) && warFile.exists())
			return warFile;
		InvocationRequest request = new DefaultInvocationRequest();
		request.setBaseDirectory(new File(""));
		request.setPomFile(new File(new File(""), "pom.xml"));
		request.setGoals(Arrays.asList(new String[] { "clean", "package" }));
		Properties properties = new Properties();
		properties.setProperty("skipTests", "true");
		request.setProperties(properties);
		Invoker invoker = new DefaultInvoker();
		invoker.execute(request);
		return warFile;
	}

	public void stop() throws LifecycleException {
		if (tomcat != null) {
			tomcat.stop();
		}
		if (dbValue != null) {
			System.setProperty("app.database", dbValue);
		}
	}

}
