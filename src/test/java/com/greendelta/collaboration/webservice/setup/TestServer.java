package com.greendelta.collaboration.webservice.setup;

import java.io.IOException;

import org.apache.catalina.LifecycleException;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class TestServer {

	private final Setup setup;
	private final Elasticsearch elastic = new Elasticsearch();
	private final Tomcat tomcat = new Tomcat();

	public TestServer(Setup setup) {
		this.setup = setup;
	}

	public void start() throws IOException, InterruptedException, MavenInvocationException, LifecycleException {
		System.out.println("Starting elastic at port " + setup.searchPort);
		elastic.start(setup);
		System.out.println("Initializing data");
		Data.init(setup);	
		System.out.println("Starting tomcat at port " + setup.tomcatPort);
		tomcat.start(setup);
	}

	public void stop() throws LifecycleException {
		System.out.println("Stopping tomcat");
		try {
			tomcat.stop();
		} catch (LifecycleException e) {
			System.out.println("Error stopping tomcat: " + e.getMessage());
		}
		System.out.println("Clearing data");
		Data.clear(setup);
		System.out.println("Stopping elastic");
		elastic.stop();
		System.out.println("Done");
	}

}
